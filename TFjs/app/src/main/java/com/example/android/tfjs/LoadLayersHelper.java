package com.example.android.tfjs;

import android.content.res.AssetManager;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class LoadLayersHelper extends WebViewClient {
    private AssetManager am;
    public LoadLayersHelper(AssetManager _am) {
        am = _am;
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest req){
        Uri Url = req.getUrl();
        String host = Url.getHost();
        if (!host.equals("astupidwebsitethatdefinitelydoesnotexist.ai"))
            return null;

        String path = Url.getPath();
        path = "mnist" + path;

        Log.v("tfjs", "Path: " + path);

        InputStream in;
        try {
            in = am.open(path);
        } catch (IOException e){
            Log.e("LoadLayersHelper", "Error when reading file in path: " + path);
            return null;
        }
        WebResourceResponse res = new WebResourceResponse("application/json", "UTF-8", in);
        return res;
    }
}
