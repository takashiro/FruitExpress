package cn.weifruit.fruitexp.util;

import android.app.Activity;
import android.util.JsonReader;
import android.webkit.WebView;

import com.alipay.sdk.app.PayTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Map;

public class AlipayHandler implements UrlHandler {

    @Override
    public void run(WebClient client, WebView view, String url) {
        final Activity activity = (Activity) client.getContext();
        final HttpClient api = new HttpClient(WebClient.SERVER_URL);
        final String apiUrl = url.substring(WebClient.SERVER_URL.length());
        final WebView webView = view;

        Runnable task = new Runnable() {
            @Override
            public void run() {
                InputStream stream = api.get(apiUrl);
                StringBuffer buffer = new StringBuffer();
                byte[] bytes = new byte[1024];
                int length = 0;
                for (;;) {
                    try {
                        length = stream.read(bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                    if (length > 0) {
                        buffer.append(new String(bytes, 0, length));
                    } else {
                        break;
                    }
                }

                String orderInfo = buffer.toString();
                PayTask task = new PayTask(activity);
                Map<String, String> response = task.payV2(orderInfo, true);
                String result = response.get("result");
                String trade_no = null;
                String out_trade_no = null;
                JsonReader reader = new JsonReader(new StringReader(result));
                try {
                    reader.beginObject();
                    while (reader.hasNext()) {
                        String name = reader.nextName();
                        if (name.equals("alipay_trade_app_pay_response")) {
                            reader.beginObject();
                            while (reader.hasNext()) {
                                String key = reader.nextName();
                                if (key.equals("trade_no")) {
                                    trade_no = reader.nextString();
                                } else if (key.equals("out_trade_no")) {
                                    out_trade_no = reader.nextString();
                                } else {
                                    reader.skipValue();
                                }
                            }
                            reader.endObject();
                        } else {
                            reader.skipValue();
                        }
                    }
                    reader.endObject();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (trade_no != null && out_trade_no != null) {
                    final String notifyUrl = WebClient.SERVER_URL + "/module/alipay/api/callback.php?trade_no=" + trade_no + "&out_trade_no=" + out_trade_no;
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            webView.loadUrl(notifyUrl);
                        }
                    });
                }
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

}
