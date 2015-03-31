package com.moonfrog.cyf;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by srinath on 31/03/15.
 */
public class HangmanSolveActivity extends Activity {
    public static HangmanSolveActivity static_instance = null;

    private String current_status = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hangman_solve);
        static_instance = this;

        Bundle extras = getIntent().getExtras();
        String word = extras.getString("word");
        word = word.toUpperCase();

        if(current_status.equals("")) {
            current_status = word.replaceAll("[A-Z]", "_");
        }

        updateStatus();
    }

    public void updateStatus() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.current_status);
        ll.removeAllViewsInLayout();

        LetterSpacingTextView tv = new LetterSpacingTextView(this);
        tv.setLetterSpacing_(10); //Or any float. To reset to normal, use 0 or LetterSpacingTextView.Spacing.NORMAL
        tv.setText(current_status);
        tv.setTextSize(30);

        ll.addView(tv);
        ll.setGravity(Gravity.CENTER);
        tv.setGravity(Gravity.CENTER);
    }
}
