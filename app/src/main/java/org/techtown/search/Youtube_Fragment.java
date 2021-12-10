package org.techtown.search;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.lang.reflect.InvocationTargetException;

public class Youtube_Fragment  extends Fragment {

    LinearLayout webLayout;
    static WebView webView;
    chromeClient client;
    public static boolean value;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.youtube_fragment, container, false);

        webLayout = rootView.findViewById(R.id.webLayout);
        webView = rootView.findViewById(R.id.webView);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new ViewClient());

        client = new chromeClient(getActivity());
        webView.setWebChromeClient(client);

        return rootView;

    }

    private class ViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public static class chromeClient extends WebChromeClient {


        Activity mActivity = null;
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        private int mOriginalOrientation;
        private FrameLayout mFullscreenContainer;
        final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        public chromeClient(Activity activity){
            this.mActivity = activity;
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            Log.d("1818", "웹뷰 호출 1");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (mCustomView != null) {
                    callback.onCustomViewHidden();
                    return;
                }

                mActivity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                        |View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_FULLSCREEN
                        |View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

                COVER_SCREEN_PARAMS.gravity = Gravity.CENTER;

                mOriginalOrientation = mActivity.getRequestedOrientation();

                FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
                mFullscreenContainer = new FullscreenHolder(mActivity);
                mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
                decor.addView(mFullscreenContainer);
                mCustomView = view;
                setFullscreen(true);
                mCustomViewCallback = callback;
//                mActivity.setRequestedOrientation(mOriginalOrientation);

                if (mActivity.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE){ //전체화면 가로에서 뒤로가기나 전체화면 나가기를 눌렀을 때
                    mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
                    value = false;
                }

            }
            super.onShowCustomView(view, callback);
        }

        @Override
        public void onShowCustomView(View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
            this.onShowCustomView(view, callback);
        }

        @Override
        public void onHideCustomView() {

            Log.d("1818", "웹뷰 호출 3");
            if (mCustomView == null) {
                return;
            }

            mActivity.getWindow().getDecorView().setSystemUiVisibility(View.VISIBLE);

            setFullscreen(false);
            FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
            decor.removeView(mFullscreenContainer);
            mFullscreenContainer = null;
            mCustomView = null;
            mCustomViewCallback.onCustomViewHidden();
//            mActivity.setRequestedOrientation(mOriginalOrientation);

            if (mActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){ //전체화면 가로에서 뒤로가기나 전체화면 나가기를 눌렀을 때
                mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                Log.d("1818", "false");
                value = false;
            }


        }


        private void setFullscreen(boolean enabled) {
            Log.d("1818", "웹뷰 호출 4");

            Window win = mActivity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
            if (enabled) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
                if (mCustomView != null) {
                    mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }
            }
            win.setAttributes(winParams);
        }

        private class FullscreenHolder extends FrameLayout {
            public FullscreenHolder(Activity ctx) {
                super(ctx);
                Log.d("1818", "웹뷰 호출 5");
                setBackgroundColor(ContextCompat.getColor(ctx, android.R.color.black));
            }
            @Override
            public boolean onTouchEvent(MotionEvent evt) {
                return true;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            Class.forName("android.webkit.WebView").getMethod("onPause", (Class[]) null).invoke(webView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            Class.forName("android.webkit.WebView").getMethod("onResume", (Class[]) null).invoke(webView, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);


        Log.d("1818", "호호호호출");


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        }, 2000);


        if (value){
            client.onHideCustomView();
        } else {
            value = true;
        }

    }


}
