package com.moonfrog.cyf.hangman;

import android.view.View;
import android.widget.Button;

import com.moonfrog.cyf.view.CategoryWordSolveActivity;

import java.util.Arrays;

/**
 * Created by srinath on 31/03/15.
 */
public class HangmanSolveActivity extends CategoryWordSolveActivity {

    @Override
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
            } else if(current_status.equals(final_word)) {
                completed = true;
            }
        }
        updateStatus();
    }

}
