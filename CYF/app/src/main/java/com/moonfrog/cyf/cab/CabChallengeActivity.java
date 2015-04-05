package com.moonfrog.cyf.cab;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moonfrog.cyf.Globals;
import com.moonfrog.cyf.R;
import com.moonfrog.cyf.hangman.HangmanChallengeFragment;
import com.moonfrog.cyf.view.CategoryWordChallengeActivity;
import com.moonfrog.cyf.view.LetterSpacingTextView;
import com.moonfrog.cyf.view.ViewUpdateCall;

/**
 * Created by srinath on 05/04/15.
 */
public class CabChallengeActivity extends CategoryWordChallengeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        challenge_layouts = new int[] {
                R.layout.challenge_hangman_gif_1,
                R.layout.challenge_cab_gif_2,
                R.layout.challenge_hangman_gif_3
        };
        challenge_fragment_class = CabChallengeFragment.class;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected ViewUpdateCall[] getViewChanges() {
        ViewUpdateCall[] viewChanges = {
                new ViewUpdateCall() {
                    @Override
                    public void updateView(View v) {
                        TextView vTemp = (TextView) v.findViewById(R.id.name);
                        vTemp.setText(Globals.name);
                    }
                },
                new ViewUpdateCall() {
                    @Override
                    public void updateView(View v) {

                    }
                },
                new ViewUpdateCall() {
                    @Override
                    public void updateView(View v) {
                        LinearLayout ll = (LinearLayout) v.findViewById(R.id.dashLayout);

                        LetterSpacingTextView tv = new LetterSpacingTextView(static_instance);
                        tv.setLetterSpacing_(12); //Or any float. To reset to normal, use 0 or LetterSpacingTextView.Spacing.NORMAL
                        String current_status = selectedWord.replaceAll("[A-Z]", "_");
                        tv.setText(current_status);
                        tv.setTextSize(30);
                        tv.setTypeface(Typeface.DEFAULT_BOLD);
                        tv.setGravity(Gravity.CENTER);
                        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                        TextView tv2 = new TextView(static_instance);
                        String text = "Guess the " + selectedTopic + "?";
                        tv2.setPadding(0, 10, 0, 0);
                        tv2.setText(text);
                        tv2.setTextSize(30);
                        tv2.setGravity(Gravity.CENTER);
                        tv2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));

                        ll.addView(tv);
                        ll.addView(tv2);
                    }
                }
        };
        return viewChanges;
    }
}
