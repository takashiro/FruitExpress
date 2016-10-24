package cn.weifruit.fruitexp.util;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import java.util.LinkedHashMap;

import cn.weifruit.fruitexp.wxapi.WxApiConfig;

public class WebClient extends WebViewClient {

    public static final String SERVER_URL = "https://weifruit.cn";
    public static final String SERVER_HOST = "weifruit.cn";

    private Context mContext;
    private IWXAPI mWechatApi;
    private LinkedHashMap<String, UrlHandler> mHandler;

    public WebClient(Context context){
        mContext = context;
        mWechatApi = WXAPIFactory.createWXAPI(mContext, WxApiConfig.APPID);
        if(!mWechatApi.registerApp(WxApiConfig.APPID)){
            Log.w("WebClient", "Failed to register WeChat 3rdparty App.");
        }

        mHandler = new LinkedHashMap<String, UrlHandler>();
        mHandler.put("weixin:connect", new WeixinConnect());
        mHandler.put("weixin:pay", new WeixinPay());
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

            UrlHandler handler = mHandler.get(mod);
            if (handler != null) {
                handler.run(this, view, url);
                return true;
            }
        }
        return false;
    }

    IWXAPI getWechatApi() {
        return mWechatApi;
    }
}
