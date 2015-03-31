package com.moonfrog.cyf;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;

/**
 * Created by srinath on 31/03/15.
 */
public class HangmanSolveActivity extends Activity {
    public static HangmanSolveActivity static_instance = null;

    private String current_status = "";
    private String final_word = "";
    private String tried_characters = "";
    private int num_wrong_choices = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hangman_solve);
        static_instance = this;

        Bundle extras = getIntent().getExtras();
        final_word = extras.getString("word");
        final_word = final_word.toUpperCase();

        if(current_status.equals("")) {
            current_status = final_word.replaceAll("[A-Z]", "_");
        }

        updateStatus();
    }

    public void onCharButtonClick(View v) {
        Button button = (Button)v;
        String button_text = (button.getText()).toString();

        if(!tried_characters.contains(button_text)) {
            tried_characters = tried_characters + button_text;
            char[] chars = tried_characters.toCharArray();
            Arrays.sort(chars);
            tried_characters = new String(chars);
            String regex = "[A-Z]";
            if(tried_characters.length()>0) {
                regex = "[A-Z&&[^" + tried_characters + "]]";
            }
            current_status = final_word.replaceAll(regex, "_");
            button.setEnabled(false);

            if(!final_word.contains(button_text)) {
                num_wrong_choices++;
            }
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

        switch(num_wrong_choices) {
            case 0: break;
            case 1: break;
            case 2: break;
            case 3: break;
            case 4: break;
            case 5: break;
            case 6: break;
            case 7: break;
            case 8: break;
            case 9:
                // Game over
                break;
            default:
                // Game over
                break;
        }

        ll.addView(tv);
        ll.setGravity(Gravity.CENTER);
        tv.setGravity(Gravity.CENTER);
    }
}
