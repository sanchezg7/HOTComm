package com.gerardoslnv.hotcomm;

import android.os.Bundle;
import android.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class WebPageFragment extends Fragment {

    private String pageUrl = "http://herdofthunder.usf.edu/";
    WebView webView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_web_page, container, false);

        webView = (WebView) view.findViewById(R.id.hotWebpageView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(pageUrl);

        return view;
    }

    public boolean canWebViewGoBack(){
        return webView.canGoBack();
    }

    public void makeWebViewGoBack(){
        webView.goBack();
    }

}


