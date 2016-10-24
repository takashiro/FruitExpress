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

class WeixinPay implements UrlHandler {

    public void run(WebClient client, WebView view, String url) {
        final String apiUrl = url;
        final IWXAPI wechatApi = client.getWechatApi();
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
                String cookie = CookieManager.getInstance().getCookie(WebClient.SERVER_URL);
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

                wechatApi.sendReq(pay);
            }
        };

        Thread thread = new Thread(payThread);
        thread.start();
    }
}
