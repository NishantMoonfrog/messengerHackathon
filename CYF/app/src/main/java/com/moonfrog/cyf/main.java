package com.moonfrog.cyf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.content.ContentUris;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;

import com.facebook.appevents.AppEventsLogger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.MessengerThreadParams;
import com.facebook.messenger.ShareToMessengerParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class main extends Activity {

    public static main static_instance = null;
    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        static_instance = this;

        boolean game_from_challenge_accept = true;
        game_from_challenge_accept = false;

        if(game_from_challenge_accept) {
            Intent intent = new Intent(main.this, HangmanSolveActivity.class);
            intent.putExtra("word", "MY COUNTRY");
            startActivity(intent);
        } else {
            Runnable task = new Runnable() {
                public void run() {
                    Intent intent = new Intent(main.this, ChallengeChooseActivity.class);
                    startActivity(intent);
                }
            };
            worker.schedule(task, 1500, TimeUnit.MILLISECONDS);
        }
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

