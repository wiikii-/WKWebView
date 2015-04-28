package com.wiikii.library.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wiikii.library.R;
import com.wiikii.library.widget.SRProgressBar;


/**
 * Created by wiikii on 15/4/9.
 */
public class WebViewActivity extends Activity {

    public static final String WEB_VIEW_DATA_URL = "URL";
    public static final String WEB_VIEW_DATA_DATA = "Data";
    public static final String WEB_VIEW_DATA_TITLE = "Title";
    public static final String WEB_VIEW_DATA_SHARE = "ShowShare";


    private SRProgressBar mProgressBar;
    private WebView mWebView;

    private TextView mTitleView;
    private Button mBackButton;
    private ImageButton mShareButton;

    private View mPrevImageView;
    private View mBackImageView;
    private View mRefreshImageView;

    private String mWebViewUrl;
    private String mWebViewData;
    private String mWebViewTitle;
    private boolean mWebViewShowShare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_web_view);

        initWebViewData();

        mProgressBar = (SRProgressBar) findViewById(R.id.progress_bar);
        mWebView = (WebView) findViewById(R.id.web_view);

        mTitleView = (TextView) findViewById(R.id.title_text);
        mBackButton = (Button) findViewById(R.id.back_btn);
        mShareButton = (ImageButton) findViewById(R.id.share_btn);

        mPrevImageView = findViewById(R.id.btn_browser_forward);
        mBackImageView = findViewById(R.id.btn_browser_back);
        mRefreshImageView = findViewById(R.id.btn_browser_refresh);

        if(mWebViewTitle != null) {
            mTitleView.setText(mWebViewTitle);
        }

        if(mWebViewShowShare) {
            mShareButton.setVisibility(View.INVISIBLE);
        }

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mShareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((TextUtils.isEmpty(mWebView.getUrl())) || TextUtils.isEmpty(mWebView.getTitle())) {
                    return;
                }

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, mWebView.getTitle().toString() + "\n" + mWebView.getUrl());
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.web_view_share_chooser_title)));
            }
        });

        mPrevImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWebView.canGoForward()) {
                    mWebView.goForward();
                    initPrevBackView();
                }
            }
        });

        mBackImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mWebView.canGoBack()) {
                    mWebView.goBack();
                    initPrevBackView();
                }
            }
        });

        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebView.reload();
                initPrevBackView();
            }
        });
        
        initWebView();
    }

    private void initWebView() {
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setAppCacheEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    Intent browserIntent = new Intent();
                    browserIntent.setAction(Intent.ACTION_VIEW);
                    browserIntent.setData(Uri.parse(url));
                    browserIntent.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
                    startActivity(browserIntent);
                    return;
                } catch (Exception e) {
                    try {
                        Intent browserIntent = new Intent();
                        browserIntent.setAction(Intent.ACTION_VIEW);
                        browserIntent.setData(Uri.parse(url));
                        startActivity(browserIntent);
                        return;
                    } catch (Exception exception) {
                    }
                }
            }
        });
        mWebView.setHorizontalScrollBarEnabled(false);

        String cacheDir = getApplicationContext().getCacheDir().getAbsolutePath();
        mWebView.getSettings().setAppCachePath(cacheDir);
        mWebView.getSettings().setAllowFileAccess(true);
        CookieManager.getInstance().setAcceptCookie(true);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
            }
        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                setLoadingProgress(2F);
                setLoadProgressBarVisible(true);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(isFinishing()) {
                    return;
                }

                setLoadingProgress(100F);
                setLoadProgressBarVisible(false);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        if(!TextUtils.isEmpty(mWebViewUrl)) {
            mWebView.loadUrl(mWebViewUrl);
            if(!TextUtils.isEmpty(mWebViewData)) {
                mWebView.loadDataWithBaseURL(null, mWebViewData, "text/html", "UTF-8", null);
            }
        }
    }

    protected void initWebViewData() {
        this.mWebViewUrl = getIntent().getStringExtra(WEB_VIEW_DATA_URL);
        this.mWebViewData = getIntent().getStringExtra(WEB_VIEW_DATA_DATA);
        this.mWebViewTitle = getIntent().getStringExtra(WEB_VIEW_DATA_TITLE);
        this.mWebViewShowShare = getIntent().getBooleanExtra(WEB_VIEW_DATA_SHARE, false);
    }

    protected void initPrevBackView() {
        mPrevImageView.setEnabled(mWebView.canGoForward());
        mBackImageView.setEnabled(mWebView.canGoBack());
    }

    protected void setLoadingProgress(float progress) {
        mProgressBar.setProgress(progress);
    }

    protected void setLoadProgressBarVisible(boolean visible) {
        if(visible) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if((mWebView != null) && (mWebView.canGoBack())) {
            mWebView.goBack();
            initPrevBackView();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mWebView != null) {
            ((RelativeLayout) findViewById(R.id.web_view_root_box)).removeView(mWebView);
            mWebView.stopLoading();
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
            System.gc();
        }
    }
}
