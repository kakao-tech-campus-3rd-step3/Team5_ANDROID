package jin.project.dailyq

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ProgressBar

class MainActivity : android.app.Activity() {
    private lateinit var webView: WebView
    private lateinit var progressBar: ProgressBar
    
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 접속 기록 저장
        PreferenceManager.markVisitToday(this)
        
        // 알림 스케줄링 (앱 최초 실행 시 또는 매일)
        NotificationScheduler.scheduleNotifications(this)
        
        // WebView 설정 (주소창 없이 웹사이트 표시)
        webView = WebView(this)
        webView.layoutParams = android.view.ViewGroup.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT
        )
        
        progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal)
        val progressBarParams = FrameLayout.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )
        progressBar.layoutParams = progressBarParams
        progressBar.visibility = View.GONE
        progressBar.max = 100
        
        val layout = android.widget.FrameLayout(this).apply {
            addView(webView)
            addView(progressBar)
        }
        
        setContentView(layout)
        
        // WebView 설정
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            loadWithOverviewMode = true
            useWideViewPort = true
            builtInZoomControls = false
            displayZoomControls = false
        }
        
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // 같은 도메인 내에서만 WebView에서 로드
                return if (url?.startsWith("https://dailyq.my") == true) {
                    false
                } else {
                    // 외부 링크는 기본 브라우저에서 열기
                    android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)).apply {
                        startActivity(this)
                    }
                    true
                }
            }
        }
        
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                progressBar.progress = newProgress
                if (newProgress == 100) {
                    progressBar.visibility = View.GONE
                } else if (progressBar.visibility != View.VISIBLE) {
                    progressBar.visibility = View.VISIBLE
                }
            }
        }
        
        // 웹사이트 로드
        webView.loadUrl("https://dailyq.my")
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            @Suppress("DEPRECATION")
            super.onBackPressed()
        }
    }
    
    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}