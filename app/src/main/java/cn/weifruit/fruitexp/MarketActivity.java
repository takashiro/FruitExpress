package cn.weifruit.fruitexp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import cn.weifruit.fruitexp.util.WebClient;

public class MarketActivity extends Activity {

    private WebView mContentView;
    private WebViewClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        mContentView = (WebView) findViewById(R.id.content_view);
        WebSettings settings = mContentView.getSettings();
        settings.setJavaScriptEnabled(true);
        mClient = new WebClient(this);
        mContentView.setWebViewClient(mClient);

        Intent intent = getIntent();
        if (intent.getAction().equals("weixin_login")){
            String code = intent.getStringExtra("code");
            if (code != null && !code.isEmpty()) {
                mContentView.loadUrl(WebClient.SERVER_URL + "/index.php?mod=weixin:connect&is_client=1&action=login&code=" + code);
                return;
            }
        }
        mContentView.loadUrl(WebClient.SERVER_URL);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mContentView.canGoBack()) {
                mContentView.goBack();
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
