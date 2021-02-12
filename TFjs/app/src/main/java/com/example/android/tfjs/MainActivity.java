package com.example.android.tfjs;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.res.AssetManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";
    private Button mButtonStart;//, mButtonFresh;
    private TextView mTextView;
    private WebView mWebView;
    private Spinner mSpinner;
    private String [] models;
    private static String path = "mnist/models";
    private int index;
    private String platform;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        mWebView.setWebViewClient(new LoadLayersHelper(getAssets()));
        mWebView.setWebContentsDebuggingEnabled(true);
        mWebView.addJavascriptInterface(this, "control");
        // cpu:
//        platform = "cpu";
//        mWebView.loadUrl("file:///android_asset/mnist/template.html?backend=cpu" +
//                "&infertime=30000");

        // gpu:
        platform = "gpu";
        mWebView.loadUrl("file:///android_asset/mnist/template.html?backend=gpu" +
                "&infertime=30000");

        mButtonStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //index = 0;
                mWebView.loadUrl(String.format("javascript:infer(\"%s\")", models[index]));
            }
        });

//        mButtonFresh.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mWebView.reload();
//            }
//        });

    }

    private void initViews(){
        mButtonStart = findViewById(R.id.btn_start);
//        mButtonFresh = findViewById(R.id.btn_fresh);
        mTextView = findViewById(R.id.textView);
//        mWebView = findViewById(R.id.webView);
        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout, null);
        mWebView = v.findViewById(R.id.webview);

        // list all models
        AssetManager assetManager = this.getAssets();
        models = null;
        try {
            models = assetManager.list(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String model: models){
            Log.i(TAG, model);
        }

        // init spinner
        mSpinner = findViewById(R.id.spinner);
        index = 0;
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, models);
        mSpinner.setAdapter(spinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                index = pos;
                Log.i(TAG, "Spinner: selected model " + models[index]);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @JavascriptInterface
    public void onFinish(final String arg) {
        Log.i(TAG, "onFinish is called");
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                Log.i(TAG, arg);
                mTextView.setText(arg);
                Pattern pattern = Pattern.compile("^mnist-([0-9]+)-([0-9]+)$");
                Matcher matcher = pattern.matcher(models[index]);
                int width = 0;
                int depth = 0;
                if(matcher.find()){
                    depth = Integer.parseInt(matcher.group(1));
                    width = Integer.parseInt(matcher.group(2));
                    Log.i(TAG, String.valueOf(width));
                    Log.i(TAG, String.valueOf(depth));
                }

                Pattern pattern1 = Pattern.compile("^(.*?)Z/(.*?)Z/(.*?)/(.*?)$");
                Matcher matcher1 = pattern1.matcher(arg);
                LocalDateTime startLocalDateTime = null;
                LocalDateTime endLocalDateTime = null;
                int enduredTime = 0;
                int count = 0;
                if(matcher1.find()){
                    startLocalDateTime = LocalDateTime.parse(matcher1.group(1));
                    endLocalDateTime = LocalDateTime.parse(matcher1.group(2));
                    enduredTime = Integer.parseInt(matcher1.group(3));
                    count = Integer.parseInt(matcher1.group(4));
                }
//                updateDatabase("mnist", "tfjs",
//                        android.os.Build.BRAND + " " + android.os.Build.MODEL,
//                        android.os.Build.VERSION.RELEASE, platform, "cnn",
//                        width, depth, startLocalDateTime, endLocalDateTime, enduredTime, count);
            }
        });
    }

    @JavascriptInterface
    public void onRefresh(final String arg) {
        Log.i(TAG, "onRefresh is called");
        runOnUiThread(new Runnable(){
            @Override
            public void run() {
                Log.i(TAG, arg);
                mTextView.setText(arg);
//                if (index < models.length){
//                    mWebView.loadUrl(String.format("javascript:infer(\"%s\")", models[index++]));
//                } else {
//                    Log.i(TAG, "all model finished!");
//                }
            }
        });
    }


    void updateDatabase(String project, String framework, String device, String system,
                        String platform, String nntype, int width, int depth,
                        LocalDateTime startTime, LocalDateTime endTime, int enduredTime,
                        int itemProcessed){
        ContentValues contentValues = new ContentValues();
        contentValues.put("project", project); // String
        contentValues.put("framework", framework); // String
        contentValues.put("device", device); // String
        contentValues.put("system", system);
        contentValues.put("platform", platform); // String
        contentValues.put("nntype", nntype); // String
        contentValues.put("width", width); // int
        contentValues.put("depth", depth); // int
        contentValues.put("starttime", startTime.toString()); // LocalDateTime
        contentValues.put("endtime", endTime.toString()); // LocalDateTime
        contentValues.put("enduredtime", enduredTime); // int
        contentValues.put("itemprocessed", itemProcessed); // int

        Uri uri = Uri.parse("content://com.example.android.experimentdb.provider/insert");
        getContentResolver().insert(uri, contentValues);
        //Toast.makeText(getApplicationContext(), "Record inserted", Toast.LENGTH_SHORT).show();
        Log.i(TAG, "Record inserted.");
    }

}
