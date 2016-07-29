package cn.weifruit.fruitexp;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MarketActivity extends Activity {

    WebView mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        mContentView = (WebView) findViewById(R.id.content_view);
        mContentView.setWebViewClient(new WebViewClient());
        mContentView.loadUrl("http://weifruit.cn");
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
