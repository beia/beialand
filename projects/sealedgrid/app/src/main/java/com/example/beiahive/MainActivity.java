package com.example.beiahive;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class MainActivity extends AppCompatActivity {
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); //turns off the title at the top of the screen
        setContentView(R.layout.activity_main);

        //this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        mWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Tells the WebView to enable JavaScript execution - needed to execute js mining script
        webSettings.setDomStorageEnabled(true); // Gets whether the DOM Storage APIs are enabled.

        webSettings.setUseWideViewPort(true); //Gets whether the WebView supports the "viewport" HTML meta tag or will use a wide viewport
        webSettings.setBuiltInZoomControls(true); //Sets whether the WebView should use its built-in zoom mechanisms, pinch gestures
        webSettings.setDisplayZoomControls(false); //Sets whether the WebView should display on-screen zoom controls when using the built-in zoom mechanisms
        webSettings.setSupportZoom(true); //Sets whether the WebView should support zooming using its on-screen zoom controls and gestures
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //Gets whether JavaScript can open windows automatically
        mWebView.getSettings().setLoadWithOverviewMode(true); //Sets whether the WebView loads pages in overview mode, that is, zooms out the content to fit on screen by width
        mWebView.setInitialScale(1); //Sets the initial scale for this WebView - content will be zoomed out to be fit by width
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY); // draw the scrollbars inside the padding specified by the drawable
        mWebView.setScrollbarFadingEnabled(true); // Define whether scrollbars will fade when the view is not scrolling
        webSettings.setSaveFormData(true); //Sets whether the WebView should save form data

        //WebView webView = (WebView) findViewById(R.id.webView);
        //webView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient()); //very important, otherwise web site will open in Android browser instead of the app

        mWebView.loadUrl("http://www.beia-telecom.ro/bgi"); // load webpage

    }
}
