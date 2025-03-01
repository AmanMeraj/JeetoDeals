package com.deals.jeetodeals;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private RelativeLayout errorView;
    private Button retryButton;
    private Toolbar toolbar;
    private String url;
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        // Initialize views
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        errorView = findViewById(R.id.errorView);
        retryButton = findViewById(R.id.retryButton);
        toolbar = findViewById(R.id.toolbar);

        // Set up toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Extract URL and title from intent
        url = getIntent().getStringExtra("url");
        title = getIntent().getStringExtra("title");

        // Set the title in the toolbar
        if (getSupportActionBar() != null && title != null) {
            getSupportActionBar().setTitle(title);
        }

        setupWebView();

        // Set up retry button
        retryButton.setOnClickListener(v -> {
            errorView.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            webView.loadUrl(url);
        });
    }

    private void setupWebView() {
        // Configure WebView settings
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);

        // Show progress indicator while loading
        progressBar.setVisibility(View.VISIBLE);

        // Hide error views initially
        errorView.setVisibility(View.GONE);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (progressBar != null) {
                    progressBar.setProgress(newProgress);
                    if (newProgress == 100) {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Hide progress bar
                progressBar.setVisibility(View.GONE);

                // Hide error view
                errorView.setVisibility(View.GONE);

                // Make WebView visible
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                // Only show error for main frame, not resources
                if (request.isForMainFrame()) {
                    webView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    errorView.setVisibility(View.VISIBLE);
                }
            }

            // For older Android versions
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (failingUrl.equals(url)) {
                    webView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    errorView.setVisibility(View.VISIBLE);
                }
            }
        });

        // Load the URL
        webView.loadUrl(url);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}