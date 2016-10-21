package cn.weifruit.fruitexp;

import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.weifruit.fruitexp.wxapi.WxApiConfig;

public class WebClient extends WebViewClient {

    static public final String SERVER_URL = "https://weifruit.cn";

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
        if (url.startsWith(SERVER_URL)) {
            String mod = null;
            int offset = url.indexOf('?');
            if (offset >= 0) {
                String[] parameters = url.substring(offset + 1).split("&");
                for (String parameter : parameters) {
                    if (parameter.startsWith("mod=")) {
                        mod = parameter.substring(4);
                        break;
                    }
                }
            }
            if (mod == null) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            if (mod.equals("weixin:connect")) {
                SendAuth.Req request = new SendAuth.Req();
                request.scope = "snsapi_userinfo";
                request.state = "login";
                mWechatApi.sendReq(request);
                return true;
            }
        }
        return super.shouldOverrideUrlLoading(view, url);
    }
}
