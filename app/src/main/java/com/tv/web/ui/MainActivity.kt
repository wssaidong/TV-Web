package com.tv.web.ui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.tv.web.R

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var urlInput: EditText
    private lateinit var progressBar: ProgressBar
    private lateinit var loadingText: TextView
    private lateinit var homeBtn: ImageButton
    private lateinit var backBtn: ImageButton
    private lateinit var forwardBtn: ImageButton
    private lateinit var refreshBtn: ImageButton
    private lateinit var topBar: View

    private var isInputMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        initWebView()
        setupListeners()

        // 默认打开 about:blank，等待用户输入
        webView.loadUrl("about:blank")
    }

    private fun initViews() {
        webView = findViewById(R.id.webView)
        urlInput = findViewById(R.id.urlInput)
        progressBar = findViewById(R.id.progressBar)
        loadingText = findViewById(R.id.loadingText)
        homeBtn = findViewById(R.id.btnHome)
        backBtn = findViewById(R.id.btnBack)
        forwardBtn = findViewById(R.id.btnForward)
        refreshBtn = findViewById(R.id.btnRefresh)
        topBar = findViewById(R.id.topBar)

        // 设置 D-pad 可聚焦
        urlInput.isFocusable = true
        urlInput.isFocusableInTouchMode = true
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            databaseEnabled = true
            cacheMode = WebSettings.LOAD_DEFAULT
            builtInZoomControls = true
            displayZoomControls = false
            setSupportZoom(true)
            loadWithOverviewMode = true
            useWideViewPort = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            mediaPlaybackRequiresUserGesture = false
        }

        webView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.black))

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.visibility = View.GONE
                loadingText.visibility = View.GONE
                urlInput.setText(url)
                updateNavButtons()
            }

            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url.toString())
                return true
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress < 100) {
                    progressBar.visibility = View.VISIBLE
                    loadingText.visibility = View.VISIBLE
                    loadingText.text = "加载中 $newProgress%"
                } else {
                    progressBar.visibility = View.GONE
                    loadingText.visibility = View.GONE
                }
            }
        }
    }

    private fun setupListeners() {
        // 输完网址按确认跳转
        urlInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_GO ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                loadUrl(urlInput.text.toString())
                hideInput()
                true
            } else false
        }

        // 点击地址栏进入输入模式
        urlInput.setOnClickListener {
            showInput()
        }

        // 主页按钮
        homeBtn.setOnClickListener {
            urlInput.setText("")
            showInput()
        }

        // 后退
        backBtn.setOnClickListener {
            if (webView.canGoBack()) {
                webView.goBack()
            }
        }

        // 前进
        forwardBtn.setOnClickListener {
            if (webView.canGoForward()) {
                webView.goForward()
            }
        }

        // 刷新
        refreshBtn.setOnClickListener {
            webView.reload()
        }

        // 按返回键如果 WebView 能返回就返回，否则退出
        webView.requestFocus()
    }

    private fun loadUrl(url: String) {
        val trimmed = url.trim()
        val finalUrl = when {
            trimmed.isEmpty() -> return
            trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
            trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
            else -> "https://www.google.com/search?q=${Uri.encode(trimmed)}"
        }
        webView.loadUrl(finalUrl)
    }

    private fun showInput() {
        isInputMode = true
        urlInput.requestFocus()
        urlInput.setSelection(urlInput.text?.length ?: 0)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(urlInput, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideInput() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(urlInput.windowToken, 0)
        isInputMode = false
        webView.requestFocus()
    }

    private fun updateNavButtons() {
        backBtn.alpha = if (webView.canGoBack()) 1f else 0.3f
        forwardBtn.alpha = if (webView.canGoForward()) 1f else 0.3f
    }

    override fun onBackPressed() {
        when {
            isInputMode -> {
                hideInput()
            }
            webView.canGoBack() -> {
                webView.goBack()
            }
            else -> {
                super.onBackPressed()
            }
        }
    }

    override fun onDestroy() {
        webView.destroy()
        super.onDestroy()
    }
}
