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
            // 주소창 관련 설정
            setSupportZoom(false)
            // 추가 설정으로 주소창 완전히 숨김
        }
        
        // WebView 자체에 주소창이 나타나지 않도록 설정
        webView.overScrollMode = View.OVER_SCROLL_NEVER
        
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                // 같은 도메인 내에서만 WebView에서 로드
                return if (url?.startsWith("https://dailyq.my") == true) {
                    false // WebView에서 로드
                } else {
                    // 외부 링크는 기본 브라우저에서 열기
                    android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url)).apply {
                        startActivity(this)
                    }
                    true
                }
            }
            
            override fun shouldOverrideUrlLoading(view: WebView?, request: android.webkit.WebResourceRequest?): Boolean {
                val url = request?.url?.toString()
                return if (url?.startsWith("https://dailyq.my") == true) {
                    false // WebView에서 로드
                } else {
                    // 외부 링크는 기본 브라우저에서 열기
                    url?.let {
                        android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(it)).apply {
                            startActivity(this)
                        }
                    }
                    true
                }
            }
            
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // 페이지 시작 시 주소창 숨김 유지
            }
            
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                // 페이지 로드 완료 후 주소창 숨김 유지
                // JavaScript로 주소창 관련 요소 숨기기 (필요시)
                view?.evaluateJavascript("""
                    (function() {
                        // 주소창이나 URL 표시 요소가 있다면 숨기기
                        var style = document.createElement('style');
                        style.innerHTML = 'body { -webkit-user-select: none; }';
                        document.head.appendChild(style);
                    })();
                """.trimIndent(), null)
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
            
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                // 제목 변경 시에도 주소창이 나타나지 않도록 처리
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