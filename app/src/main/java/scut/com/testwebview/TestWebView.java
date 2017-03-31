package scut.com.testwebview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Message;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by yany on 2017/3/13.
 */

public class TestWebView extends WebView {
    private Context context;
    public TestWebView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    public TestWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context);
    }

    public TestWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init(context);
    }


    private void init(Context context){
        setWebViewClient(new MyWebViewClient());
        setWebChromeClient(new MyWebChromeClient());
        WebSettings settings = getSettings();

        //支持与JS交互
        settings.setJavaScriptEnabled(true);
        //支持通过Js打开新窗口
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        String cacheDirPath = context.getFilesDir().getAbsolutePath()+"cache/";
        System.out.println("cacheDirPath" + cacheDirPath);
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        // 开启DOM storage
        settings.setDomStorageEnabled(true);
        // 开启AppCache storage
        settings.setAppCacheEnabled(true);
        // 开启database storage
//        settings.setDatabaseEnabled(true);
        settings.setAppCachePath(cacheDirPath);
//        settings.setDatabasePath(cacheDirPath);
        //This method was deprecated in API level 18.
        //官方说你再怎么限制也是没有意义的，人家网页该是那么大还是那么大，建议不要使用这个方法
//        settings.setAppCacheMaxSize(20*1024*1024);

    }


    class MyWebViewClient extends WebViewClient{

        /**
         * 此方法在页面加载开始时调用，可以做页面加载前的预处理
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            System.out.println("onPageStarted");
        }



        /**
         * 此方法在页面加载结束时调用，可以做页面加载后的处理
         */
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            System.out.println("onPageFinished");
        }

        /**
         * 此方法在页面正在加载时调用，每一个资源（比如图片）的加载都会调用一次
         */
        @Override
        public void onLoadResource(WebView view, String url) {
            super.onLoadResource(view, url);
//            System.out.println("onLoadResource");
        }

        //此方法在页面访问出错的时候会调用
        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            System.out.println("onReceivedError："+ error.getDescription().toString());
//            loadUrl("file:///android_asset/error.html");
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            super.onReceivedHttpError(view, request, errorResponse);
            System.out.println("onReceivedHttpError: " + errorResponse.getStatusCode());
//            loadUrl("file:///android_asset/error.html");
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            System.out.println("onReceivedSslError");
            super.onReceivedSslError(view, handler, error);
        }


        //重写此方法才会使WebView加载url，而不是跳到系统默认浏览器去加载
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            //判断是否拦截
            if (url.contains("logo.gif")) {
                InputStream is = null;
                //打开assets文件夹里面用于替换的资源
                try {
                    is = context.getAssets().open("images/error-hanger.png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //替换资源：
                // 第一个参数image/png是http请求里面的Content-Type，
                // 第二个参数是编码类型，
                // 第三个就是要替换的内容的InputStream了
                WebResourceResponse response = new WebResourceResponse("image/png",
                        "utf-8", is);
                System.out.println("旧API");
                return response;
            }

            return super.shouldInterceptRequest(view, url);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            //判断是否拦截
            if (request.getUrl().toString().contains("logo.gif")) {
                InputStream is = null;
                //打开assets文件夹里面用于替换的资源
                try {
                    is = context.getAssets().open("images/error-hanger.png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //替换资源：
                // 第一个参数image/png是http请求里面的Content-Type，
                // 第二个参数是编码类型，
                // 第三个就是要替换的内容的InputStream了
                WebResourceResponse response = new WebResourceResponse("image/png",
                        "utf-8", is);
                System.out.println("新API");
                return response;
        }
            return super.shouldInterceptRequest(view, request);
    }
    }

    class MyWebChromeClient extends WebChromeClient{
        //此方法在WebView要求新建一个窗口，如对话框、新窗口等的时候调用
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            System.out.println("onCreateWindow");
            return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
        }

        //此方法在WebView要求关闭一个窗口时候调用
        @Override
        public void onCloseWindow(WebView window) {
            System.out.println("onCloseWindow");
            super.onCloseWindow(window);
        }

        //此方法在网页标题变化时会调用，获取网站标题
        @Override
        public void onReceivedTitle(WebView view, String title) {
            System.out.println("标题在这里: " + title);
        }

    }
}
