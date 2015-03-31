package com.moonfrog.cyf;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import com.facebook.messenger.MessengerThreadParams;
import com.facebook.messenger.MessengerUtils;
import com.moonfrog.cyf.view.GenericPopup;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by srinath on 31/03/15.
 */
public class HangmanSolveActivity extends Activity {
    public static HangmanSolveActivity static_instance = null;
    private boolean mPicking = false;

    private String current_status = "";
    private String final_word = "";
    private String tried_characters = "";
    private int num_wrong_choices = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hangman_solve);
        static_instance = this;

        Intent intent = getIntent();

        String word = "";
        if (Intent.ACTION_PICK.equals(intent.getAction())) {
            mPicking = true;
            MessengerThreadParams mThreadParams = MessengerUtils.getMessengerThreadParamsForIntent(intent);

            String metadata = mThreadParams.metadata;
            try {
                JSONObject jsonObj = new JSONObject(metadata);
                word = jsonObj.get("word").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final_word = Globals.decrypt(word);
            List<String> participantIds = mThreadParams.participants;
            final_word.toUpperCase();
        }

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
            } else if(current_status.equals(final_word)) {
                num_wrong_choices = -1;
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

        ImageView iv = (ImageView) findViewById(R.id.hangman_image);

        switch(num_wrong_choices) {
            case -1:
                // Win condition

                break;
            case 0:
                iv.setImageResource(R.drawable.hangman_0);
                break;
            case 1:
                iv.setImageResource(R.drawable.hangman_1);
                break;
            case 2:
                iv.setImageResource(R.drawable.hangman_2);
                break;
            case 3:
                iv.setImageResource(R.drawable.hangman_3);
                break;
            case 4:
                iv.setImageResource(R.drawable.hangman_4);
                break;
            case 5:
                iv.setImageResource(R.drawable.hangman_5);
                break;
            case 6:
                iv.setImageResource(R.drawable.hangman_6);
                break;
            case 7:
                iv.setImageResource(R.drawable.hangman_7);
                break;
            case 8:
            default:
                // Game over
                iv.setImageResource(R.drawable.hangman_8);

                // custom dialog
                final GenericPopup losePopup = new GenericPopup(this, "You lost! Now get Lost!!", false, "Okay");
                losePopup.setContentView(R.layout.generic_popup);
                //dialog.setTitle("You lost!");
                losePopup.show();

                losePopup.setOnPopupCloseListener(new GenericPopup.OnPopupCloseListener() {
                    @Override
                    public void OnClose(GenericPopup popup) {
                        Intent intent = new Intent(HangmanSolveActivity.this, ChallengeChooseActivity.class);
                        startActivity(intent);
                    }
                });
                break;
        }

        ll.addView(tv);
        ll.setGravity(Gravity.CENTER);
        tv.setGravity(Gravity.CENTER);
    }
}
