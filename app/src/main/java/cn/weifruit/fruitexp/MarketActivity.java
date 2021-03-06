package cn.weifruit.fruitexp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

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
        mContentView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress){
                ProgressBar bar = (ProgressBar) findViewById(R.id.progress_bar);
                if (newProgress < 100) {
                    bar.setVisibility(ProgressBar.VISIBLE);
                    bar.setProgress(newProgress);
                } else {
                    bar.setVisibility(ProgressBar.INVISIBLE);
                }
            }
        });

        Intent intent = getIntent();
        if (intent.getAction().equals("loadUrl")){
            String url = intent.getStringExtra("url");
            if (url != null && !url.isEmpty()) {
                mContentView.loadUrl(WebClient.SERVER_URL + "/" + url);
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
