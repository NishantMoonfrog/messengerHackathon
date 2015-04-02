package com.moonfrog.cyf;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ImageView;

import com.facebook.appevents.AppEventsLogger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.MessengerThreadParams;
import com.facebook.messenger.ShareToMessengerParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class main extends Activity {

    public static main static_instance = null;
    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();

    private ProgressBar mProgress;
    private int mProgressStatus = 0;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        static_instance = this;

        TextView tv = (TextView) findViewById(R.id.loading_text);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/brady.ttf");
        tv.setTypeface(tf);

        preloadStart();
    }

    private void preloadStart() {

        mProgress = (ProgressBar) findViewById(R.id.preload_bar);

        // Start lengthy operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject word_list = null;
                    InputStream is = getResources().openRawResource(R.raw.word_list);
                    Writer writer = new StringWriter();
                    char[] buffer = new char[1024];
                    try {
                        Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                        int n;
                        while ((n = reader.read(buffer)) != -1) {
                            writer.write(buffer, 0, n);
                        }
                    } finally {
                        is.close();
                    }
                    word_list = new JSONObject(writer.toString());

                    for(int i = 0 ; i < Globals.hangman_challenge_categories.length; i++) {
                        Thread.sleep(500);
                        mProgressStatus = (int)(((float)i/(float)Globals.hangman_challenge_categories.length)*100);
                        mHandler.post(new Runnable() {
                            public void run() {
                                mProgress.setProgress(mProgressStatus);
                            }
                        });

                        JSONArray list = word_list.getJSONArray(Globals.hangman_challenge_categories[i]);
                        ArrayList<String> strList = new ArrayList<>();
                        for(int j = 0, count = list.length(); j < count; j++) {
                            if( list.getString(j).length() < 15 ) {
                                strList.add(list.getString(j).toUpperCase());
                            }
                        }
                        Globals.hangman_challenge_category_word_list[i] = strList.toArray(new String[strList.size()]);
                        Arrays.sort(Globals.hangman_challenge_category_word_list[i]);
                    }

                    Intent intent = getIntent();
                    Intent targetIntent;
                    String word = "";
                    if (Intent.ACTION_PICK.equals(intent.getAction())) {
                        MessengerThreadParams mThreadParams = MessengerUtils.getMessengerThreadParamsForIntent(intent);
                        String challengerName = "";
                        String metadata = mThreadParams.metadata;
                        try {
                            JSONObject jsonObj = new JSONObject(metadata);
                            word = jsonObj.get("word").toString();
                            challengerName = jsonObj.get("name").toString();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String final_word = Globals.decrypt(word);
                        List<String> participantIds = mThreadParams.participants;
                        final_word.toUpperCase();

                        Bundle params = new Bundle();
                        params.putString("final_word", final_word);
                        params.putBoolean("mPicking", true);
                        params.putString("challengerName", challengerName);
                        params.putStringArray("participantIds", (String[])participantIds.toArray());

                        targetIntent = new Intent(main.this, HangmanSolveActivity.class);
                        targetIntent.putExtras(params);
                    } else {
                        targetIntent = new Intent(main.this, ChallengeChooseActivity.class);
                    }
                    startActivity(targetIntent);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onHangmanClick(View v) {
        setContentView(R.layout.hangman_challenge);

        // image naming and path  to include sd card  appending name you choose for file
        String mPath = Environment.getExternalStorageDirectory().toString() + "/" + "img.bmp";

        // create bitmap screen capture
        Bitmap bitmap;
        View v1 = getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);

        OutputStream fout = null;
        File imageFile = new File(mPath);

        try {
            fout = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fout);
            fout.flush();
            fout.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String mimeType = "image/*";

        // contentUri points to the content being shared to Messenger
        Uri contentUri = Uri.fromFile(new File("/sdcard/Downloads/img.jpg"));
        ShareToMessengerParams shareToMessengerParams =
                ShareToMessengerParams.newBuilder(contentUri, mimeType)
                    .build();

        // Sharing from an Activity
        MessengerUtils.shareToMessenger(
            this,
            10,
            shareToMessengerParams); 
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }
}

