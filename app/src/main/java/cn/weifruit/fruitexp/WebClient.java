package cn.weifruit.fruitexp;

import android.content.Context;
import android.net.Uri;
import android.util.JsonReader;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.weifruit.fruitexp.wxapi.WxApiConfig;

class WebClient extends WebViewClient {

    static final String SERVER_URL = "https://weifruit.cn";
    static final String SERVER_HOST = "weifruit.cn";

    private Context mContext;
    private IWXAPI mWechatApi;

    WebClient(Context context){
        mContext = context;
        mWechatApi = WXAPIFactory.createWXAPI(mContext, WxApiConfig.APPID);
        if(!mWechatApi.registerApp(WxApiConfig.APPID)){
            Log.w("WebClient", "Failed to register WeChat 3rdparty App.");
        }
    }

    @Override
    @SuppressWarnings("Deprecated")
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        Uri uri = Uri.parse(url);
        if (uri.getHost().equals(SERVER_HOST)) {
            String mod = uri.getQueryParameter("mod");
            if (mod == null || mod.isEmpty()) {
                return false;
            }

            if (mod.equals("weixin:connect")) {
                SendAuth.Req auth = new SendAuth.Req();
                auth.scope = "snsapi_userinfo";
                auth.state = "login";
                mWechatApi.sendReq(auth);
                return true;
            } else if (mod.equals("weixin:pay")) {
                final String apiUrl = url;
                Runnable payThread = new Runnable() {
                    @Override
                    public void run() {
                        URL api = null;
                        try {
                            api = new URL(apiUrl);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            return;
                        }
                        HttpURLConnection conn = null;
                        try {
                            conn = (HttpURLConnection) api.openConnection();
                        } catch (IOException e) {
                            e.printStackTrace();;
                            return;
                        }

                        conn.setRequestProperty("User-Agent", "NativeApp");
                        String cookie = CookieManager.getInstance().getCookie(SERVER_URL);
                        conn.setRequestProperty("Cookie", cookie);
                        try {
                            conn.connect();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }

                        PayReq pay = new PayReq();
                        try {
                            JsonReader reader = new JsonReader(new InputStreamReader(conn.getInputStream()));
                            reader.beginObject();
                            while (reader.hasNext()) {
                                String name = reader.nextName();
                                switch (name) {
                                    case "appid": pay.appId = reader.nextString(); break;
                                    case "partnerid": pay.partnerId = reader.nextString(); break;
                                    case "prepayid": pay.prepayId = reader.nextString(); break;
                                    case "package": pay.packageValue = reader.nextString(); break;
                                    case "timestamp": pay.timeStamp = reader.nextString(); break;
                                    case "noncestr": pay.nonceStr = reader.nextString(); break;
                                    case "sign": pay.sign = reader.nextString(); break;
                                }
                            }
                            reader.endObject();
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }

                        mWechatApi.sendReq(pay);
                    }
                };

                Thread thread = new Thread(payThread);
                thread.start();

                return true;
            }
        }
        return false;
    }
}
