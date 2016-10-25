package cn.weifruit.fruitexp.util;

import android.webkit.WebView;

import com.tencent.mm.sdk.modelmsg.SendAuth;

public class WeixinConnectHandler implements UrlHandler {

    public void run(WebClient client, WebView view, String url){
        SendAuth.Req auth = new SendAuth.Req();
        auth.scope = "snsapi_userinfo";
        auth.state = "login";
        client.getWechatApi().sendReq(auth);
    }

}
