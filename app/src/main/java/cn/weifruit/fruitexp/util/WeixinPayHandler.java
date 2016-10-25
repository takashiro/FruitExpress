package cn.weifruit.fruitexp.util;

import android.util.JsonReader;
import android.webkit.CookieManager;
import android.webkit.WebView;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

class WeixinPayHandler implements UrlHandler {

    public void run(WebClient client, WebView view, String url) {
        final String apiUrl = url.substring(WebClient.SERVER_URL.length());
        final IWXAPI wechatApi = client.getWechatApi();
        final HttpClient api = new HttpClient(WebClient.SERVER_URL);
        Runnable payThread = new Runnable() {
            @Override
            public void run() {
                PayReq pay = new PayReq();
                try {
                    JsonReader reader = new JsonReader(new InputStreamReader(api.get(apiUrl)));
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
                            default: reader.skipValue();
                        }
                    }
                    reader.endObject();
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }

                wechatApi.sendReq(pay);
            }
        };

        Thread thread = new Thread(payThread);
        thread.start();
    }
}
