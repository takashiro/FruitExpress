package cn.weifruit.fruitexp.util;

import android.webkit.WebView;

public interface UrlHandler {
    public void run(WebClient client, WebView view, String url);
}
