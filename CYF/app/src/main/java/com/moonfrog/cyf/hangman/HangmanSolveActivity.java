package com.moonfrog.cyf.hangman;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.messenger.ShareToMessengerParams;
import com.moonfrog.cyf.ChallengeChooseActivity;
import com.moonfrog.cyf.Globals;
import com.moonfrog.cyf.R;
import com.moonfrog.cyf.main;
import com.moonfrog.cyf.view.CategoryWordSolveActivity;
import com.moonfrog.cyf.view.GenericPopup;
import com.moonfrog.cyf.view.LetterSpacingTextView;

import org.json.JSONObject;

import java.io.FileOutputStream;
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

    @Override
    public void updateStatus() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.current_status);
        ll.removeAllViewsInLayout();

        LetterSpacingTextView tv = new LetterSpacingTextView(this);
        tv.setLetterSpacing_(10); //Or any float. To reset to normal, use 0 or LetterSpacingTextView.Spacing.NORMAL
        tv.setText(current_status);
        tv.setTextSize(30);

        ImageView iv = (ImageView) findViewById(R.id.hangman_image);

        if(completed) {
            // Win condition
            GenericPopup winPopup = new GenericPopup(this, "You completed the challenge in " + (num_wrong_choices +1) + " turns!", true);
            winPopup.setOnPopupCloseListener(new GenericPopup.OnPopupCloseListener() {
                @Override
                public void OnClose(GenericPopup popup) {
                    // NISHANT: Add share Code here...
                    LayoutInflater layoutInflater = (LayoutInflater) static_instance.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View v = layoutInflater.inflate(R.layout.win_hangman, null);

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

                            String pngPath = "/sdcard/cyfTemp/" + "win_hangman.png";
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
                                metadataJson.put("challenge", Globals.encrypt(final_word));
                                metadataJson.put("name", challengerName);
                                metadataJson.put("type", "hangman");
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
        } else {
            switch(num_wrong_choices) {
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
                    GenericPopup losePopup = new GenericPopup(this, "You lost.. :-(\nPost a new Challenge", false, "GO");
                    losePopup.setOnPopupCloseListener(new GenericPopup.OnPopupCloseListener() {
                        @Override
                        public void OnClose(GenericPopup popup) {
                            Intent intent = new Intent(HangmanSolveActivity.this, ChallengeChooseActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    losePopup.show();
                    break;
            }
        }

        ll.addView(tv);
        ll.setGravity(Gravity.CENTER);
        tv.setGravity(Gravity.CENTER);
    }

}
