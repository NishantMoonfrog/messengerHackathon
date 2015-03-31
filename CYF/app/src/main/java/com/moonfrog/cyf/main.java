package com.moonfrog.cyf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.facebook.appevents.AppEventsLogger;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class main extends Activity {

    public static main static_instance = null;
    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        static_instance = this;

        boolean game_from_challenge_accept = true;

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
            worker.schedule(task, 5, TimeUnit.SECONDS);
        }
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

