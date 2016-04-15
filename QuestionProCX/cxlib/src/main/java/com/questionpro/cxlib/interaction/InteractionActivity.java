package com.questionpro.cxlib.interaction;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.questionpro.cxlib.R;
import com.questionpro.cxlib.constants.CXConstants;
import com.questionpro.cxlib.dataconnect.TouchPoint;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by sachinsable on 12/04/16.
 */
public class InteractionActivity extends FragmentActivity {
    private final String LOG_TAG="InteractionActivity";
    private ProgressBar progressBar;
    private WebView webView;
    private String url = "";
    private TouchPoint touchPoint;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cx_webview_dialog);
        progressBar =(ProgressBar) findViewById(R.id.progressBar);
        webView = (WebView)findViewById(R.id.surveyWebView);
        webView.setWebViewClient(new CXWebViewClient());
        webView.setWebChromeClient(new CXWebChromeClient());
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.clearCache(true);
        webView.getSettings().setUserAgentString("AndroidWebView");
        if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        ImageButton closeButton = (ImageButton)findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        url = getIntent().getStringExtra(CXConstants.SURVEY_URL);
        touchPoint = (TouchPoint)getIntent().getSerializableExtra(CXConstants.EXTRA_TOUCH_POINT);
        if(touchPoint.isDialog()){
            setTheme(android.R.style.Theme_Dialog);
        }
        else{
            setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
        }
        if(url==null){
            finish();
        }
        else{
            webView.loadUrl(url);
        }

    }


    private class CXWebChromeClient extends WebChromeClient {
        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.d(LOG_TAG, consoleMessage.message());
            if(consoleMessage.message()!=null && consoleMessage.message().equalsIgnoreCase("cx_thank_you_page")){
                runTimer();
                return true;
            }

            return super.onConsoleMessage(consoleMessage);

        }

        @Override
        public void onCloseWindow(WebView window) {
            super.onCloseWindow(window);
            finish();
        }


    }


    private static final ScheduledExecutorService worker =
            Executors.newSingleThreadScheduledExecutor();

    private void runTimer() {

        Runnable task = new Runnable() {
            public void run() {
                finish();
            }
        };
        worker.schedule(task, 5, TimeUnit.SECONDS);

    }
    private class CXWebViewClient extends WebViewClient {


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (event.getKeyCode()) {
                    case KeyEvent.KEYCODE_BACK:
                        if (view.canGoBack()) {
                            view.goBack();
                        } else {
                           finish();
                        }
                        return false;

                }


            }
            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
        }

    }
}