package com.moonfrog.cyf;

import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import com.facebook.messenger.MessengerThreadParams;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
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
    private String challengerName = "";
    private int num_wrong_choices = 0;
    private boolean completed = false;

    private String[] participantIds;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hangman_solve);
        static_instance = this;

        Intent intent = getIntent();
        Bundle extras = getIntent().getExtras();
        challengerName = extras.getString("challengerName", "");
        final_word = extras.getString("final_word", "");
        mPicking = extras.getBoolean("mPicking", false);
        participantIds = extras.getStringArray("participantIds");

/*        String word = "";
        if (Intent.ACTION_PICK.equals(intent.getAction())) {
            mPicking = true;
            MessengerThreadParams mThreadParams = MessengerUtils.getMessengerThreadParamsForIntent(intent);

            String metadata = mThreadParams.metadata;
            try {
                JSONObject jsonObj = new JSONObject(metadata);
                word = jsonObj.get("word").toString();
                challengerName = jsonObj.get("name").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final_word = Globals.decrypt(word);
            List<String> participantIds = mThreadParams.participants;
            final_word.toUpperCase();
        }*/

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
                completed = true;
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

        if(completed) {
            // Win condition
            GenericPopup winPopup = new GenericPopup(this, "You completed the challenge in " + num_wrong_choices + " turns!", true);
            winPopup.setOnPopupCloseListener(new GenericPopup.OnPopupCloseListener() {
                @Override
                public void OnClose(GenericPopup popup) {
                    // NISHANT: Add share Code here...
                    LayoutInflater layoutInflater = (LayoutInflater) static_instance.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View v = layoutInflater.inflate(R.layout.win_hangman, null);

                    ImageView imageView = (ImageView) v.findViewById(R.id.imageView);

                    int id = R.drawable.hangman_0;
                    switch(num_wrong_choices) {
                        case 1:
                            id = R.drawable.hangman_1;
                            break;
                        case 2:
                            id = R.drawable.hangman_2;
                            break;
                        case 3:
                            id = R.drawable.hangman_3;
                            break;
                        case 4:
                            id = R.drawable.hangman_4;
                            break;
                        case 5:
                            id = R.drawable.hangman_5;
                            break;
                        case 6:
                            id = R.drawable.hangman_6;
                            break;
                        case 7:
                            id = R.drawable.hangman_7;
                            break;
                    }


                    imageView.setImageDrawable(getResources().getDrawable(id));

                    TextView tv = new TextView(static_instance);
                    String text = "I cracked " + challengerName + "'s challenge in " + num_wrong_choices+1 + " turns!";
                    tv.setPadding(0, 10, 0, 0);
                    tv.setText(text);
                    tv.setTextSize(40);
                    tv.setGravity(Gravity.CENTER);

                    LinearLayout ll = (LinearLayout) v.findViewById(R.id.win_view);
                    ll.addView(tv);

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
                                metadataJson.put("word", Globals.encrypt(final_word));
                                metadataJson.put("name", challengerName);
                                metadata = metadataJson.toString();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            ShareToMessengerParams shareToMessengerParams = ShareToMessengerParams.newBuilder(contentUri, "image/png").setMetaData(metadata).build();
                            if( mPicking ) {
                                MessengerUtils.finishShareToMessenger(static_instance, shareToMessengerParams);
                            }
                        }
                    });

                    // On completion do this:
                    // Intent intent = new Intent(HangmanSolveActivity.this, ChallengeChooseActivity.class);
                    // startActivity(intent);
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
                            // NISHANT: Add share Code here...

                            // ShareToMessengerParams shareToMessengerParams =
                            //         ShareToMessengerParams.newBuilder(Uri.fromFile(new File("/sdcard/Downloads/img.jpg")), "image/jpeg")
                            //                 .build();

                            // if( mPicking ) {
                            //     MessengerUtils.finishShareToMessenger(static_instance, shareToMessengerParams);
                            // }
                            
                            Intent intent = new Intent(HangmanSolveActivity.this, ChallengeChooseActivity.class);
                            startActivity(intent);
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
