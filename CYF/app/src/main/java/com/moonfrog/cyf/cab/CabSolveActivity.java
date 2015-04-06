package com.moonfrog.cyf.cab;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.messenger.ShareToMessengerParams;
import com.moonfrog.cyf.ChallengeChooseActivity;
import com.moonfrog.cyf.Globals;
import com.moonfrog.cyf.R;
import com.moonfrog.cyf.main;
import com.moonfrog.cyf.view.CategoryWordSolveActivity;
import com.moonfrog.cyf.view.GenericPopup;

import org.json.JSONObject;

import java.io.FileOutputStream;

/**
 * Created by srinath on 05/04/15.
 */
public class CabSolveActivity extends CategoryWordSolveActivity {

    private String current_guess_status = "";
    private int current_challenge_length = 5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        solve_layout = R.layout.cab_solve;
        super.onCreate(savedInstanceState);
        findViewById(R.id.guess_button).setEnabled(false);
        current_challenge_length = getIntent().getExtras().getInt("n_char", 5);
    }

    public void onGuessButtonClick(View v) {
        Button button = (Button)v;
        v.setEnabled(false);
        ((Button)v).setText("GUESS");
        //re-enable all buttons again :)
        if(current_guess_status.equals(final_word)) {
            completed = true;
        } else {
            num_wrong_choices++;
        }

        disableCharButtons();

        updateStatus();
    }

    @Override
    public void onCharButtonClick(View v) {
        // till go button is pressed,
        Button button = (Button)v;
        String button_text = (button.getText()).toString();
        current_guess_status = current_guess_status + button_text;
        button.setEnabled(false);

        updateButtons();
    }

    public void onBackButtonPressed(View v) {
        String new_guess = "";
        //remove last word from
        for(int i = 0; i < current_guess_status.length() - 1; i++) {
            new_guess += current_guess_status.charAt(i);
        }
        setButtonEnabledStateForString("" + current_guess_status.charAt(current_guess_status.length() - 1), true);
        current_guess_status = new_guess;
        updateButtons();
    }

    public void updateButtons() {
        if(current_guess_status.length() < 1 ) {
            (this.findViewById(R.id.button_back)).setEnabled(false);
        }

        if(current_guess_status.length() == current_challenge_length) {
            (this.findViewById(R.id.guess_button)).setEnabled(true);
            disableCharButtons();
        }

        if(current_guess_status.length() >=1 ) {
            (this.findViewById(R.id.button_back)).setEnabled(true);
        }

        ((Button)findViewById(R.id.guess_button)).setText("GUESS " + current_guess_status);
    }

    public void disableCharButtons() {
        LinearLayout button_group_main = (LinearLayout)this.findViewById(R.id.char_button_layout);
        int childcount = button_group_main.getChildCount();
        for (int i=0; i < childcount; i++){
            View child = button_group_main.getChildAt(i);
            if(child instanceof LinearLayout) {
                LinearLayout button_group_sub = (LinearLayout)child;

                int childchildcount = button_group_sub.getChildCount();
                for(int j=0; j < childchildcount; j++) {
                    View childchild = button_group_sub.getChildAt(j);
                    if(childchild instanceof Button) {
                        String button_content = (((Button)childchild).getText()).toString();
                        if((childchild).hasOnClickListeners() && !button_content.contains("←")) {
                            (childchild).setEnabled(false);
                        }
                    }
                }
            }
        }
    }

    public void setButtonEnabledStateForString(String button_string, boolean enable) {
        LinearLayout button_group_main = (LinearLayout)this.findViewById(R.id.char_button_layout);
        int childcount = button_group_main.getChildCount();
        for (int i=0; i < childcount; i++){
            View child = button_group_main.getChildAt(i);
            if(child instanceof LinearLayout) {
                LinearLayout button_group_sub = (LinearLayout)child;

                int childchildcount = button_group_sub.getChildCount();
                for(int j=0; j < childchildcount; j++) {
                    View childchild = button_group_sub.getChildAt(j);
                    if(childchild instanceof Button) {
                        String button_content = (((Button)childchild).getText()).toString();
                        if((childchild).hasOnClickListeners() && button_content.equals(button_string)) {
                            (childchild).setEnabled(enable);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void updateStatus() {
        if(current_guess_status.equals("")) return;

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View guess_row = inflater.inflate(R.layout.cab_guess, (ViewGroup)(this.findViewById(android.R.id.content).getRootView()), false);

        TextView guess_word = (TextView)guess_row.findViewById(R.id.guessed_word_title);
        guess_word.setText(current_guess_status);

        int num_bulls = 0, num_cows = 0;
        for(int i = 0; i < current_guess_status.length(); i++) {
            if(final_word.indexOf(current_guess_status.charAt(i)) >= 0) {
                if(final_word.charAt(i) == current_guess_status.charAt(i)) {
                    num_bulls++;
                } else {
                    num_cows++;
                }
            }
        }

        TextView cows = (TextView)guess_row.findViewById(R.id.cows_title);
        cows.setText("" + num_cows);
        TextView bulls = (TextView)guess_row.findViewById(R.id.bulls_title);
        bulls.setText("" + num_bulls);

        LinearLayout ll = (LinearLayout)this.findViewById(R.id.cab_guess_layout);
        ll.addView(guess_row);
        //ll.setGravity(Gravity.CENTER);

        if(completed) {
            GenericPopup winPopup = new GenericPopup(this, "You completed the challenge in " + (num_wrong_choices +1) + " turns!", true);
            winPopup.setOnPopupCloseListener(new GenericPopup.OnPopupCloseListener() {
                @Override
                public void OnClose(GenericPopup popup) {
                    // NISHANT: Add share Code here...
                    LayoutInflater layoutInflater = (LayoutInflater) static_instance.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View v = layoutInflater.inflate(R.layout.win_cab, null);

                    TextView tv = (TextView) v.findViewById(R.id.bragText);
                    String text = "I cracked\n" + challengerName + "'s challenge\nin " + (num_wrong_choices+1) + " turns!";
                    tv.setText(text);

                    final ViewGroup current = (ViewGroup) static_instance.getWindow().getDecorView().getRootView();
                    current.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

                    v.post(new Runnable() {
                        @Override
                        public void run() {
                            Bitmap image = Globals.getBitmapFromView(v);
                            current.removeView(v);

                            String pngPath = "/sdcard/cyfTemp/" + "win_cab.png";
                            try{
                                FileOutputStream outStream = new FileOutputStream(pngPath);
                                image.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                                outStream.flush();
                                outStream.close();
                            } catch(Exception e){
                                e.printStackTrace();
                            }


                            Uri.Builder b = new Uri.Builder();
                            b.scheme("file").appendPath(pngPath);
                            Uri contentUri = b.build();
                            String metadata = "";
                            try {
                                JSONObject metadataJson = new JSONObject();
                                metadataJson.put("word", Globals.encrypt(final_word));
                                metadataJson.put("name", challengerName);
                                metadataJson.put("type", "caf");
                                metadata = metadataJson.toString();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            ShareToMessengerParams shareToMessengerParams = ShareToMessengerParams.newBuilder(contentUri, "image/png").setMetaData(metadata).build();
                            main.static_instance.postToMessenger(shareToMessengerParams);
                            finish();
                        }
                    });
                }
            });
            winPopup.show();
        }

        if(num_wrong_choices >= 6) {
            GenericPopup losePopup = new GenericPopup(this, "You lost.. :-(\nPost a new Challenge", false, "GO");
            losePopup.setOnPopupCloseListener(new GenericPopup.OnPopupCloseListener() {
                @Override
                public void OnClose(GenericPopup popup) {
                    Intent intent = new Intent(CabSolveActivity.this, ChallengeChooseActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            losePopup.show();
        }

        current_guess_status = "";
    }
}
