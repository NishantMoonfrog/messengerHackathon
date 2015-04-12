package com.moonfrog.cyf.puzzles;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.moonfrog.cyf.ChallengeChooseActivity;
import com.moonfrog.cyf.Globals;
import com.moonfrog.cyf.R;
import com.moonfrog.cyf.main;
import com.moonfrog.cyf.view.CategoryWordSolveActivity;
import com.moonfrog.cyf.view.GenericPopup;
import com.moonfrog.cyf.view.LetterSpacingTextView;
import com.moonfrog.cyf.view.ViewUpdateCall;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * Created by srinath on 31/03/15.
 */
public class PuzzleSolveActivity extends Activity {
    public static PuzzleSolveActivity static_instance = null;
    public static String puzzleName;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        static_instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle_solve);


        String type = getIntent().getExtras().getString("type", "");
        puzzleName = getIntent().getExtras().getString("challenge", "");
        setTitle(puzzleName);

        String[] puzzleText = Globals.getDataFromFile(getBaseContext(), Globals.puzzleDirectory + '/' + puzzleName).split("<!!Separator!!>\n");
        if( puzzleText.length < 3 ) {
            throw new IllegalArgumentException("The File " + puzzleName + " should have at question, answer and explanation separated by <!!Separator!!>");
        }

        TextView puzzleTextView = (TextView) findViewById(R.id.puzzle_text);
        puzzleTextView.setText(puzzleText[0]);
        TextView explanationView = (TextView) findViewById(R.id.explaination);
        explanationView.setText(puzzleText[2]);

        // explanationView.setVisibility(View.INVISIBLE);


        final int[] challenge_layouts = new int[] {
                R.layout.challenge_hangman_gif_1,
                R.layout.challenge_hangman_gif_2,
                R.layout.challenge_puzzles_3
        };

        final Runnable shareChallenge = new Runnable() {
            @Override
            public void run() {
                String metadata = "";
                try {
                    JSONObject metadataJson = new JSONObject();
                    metadataJson.put("challenge", Globals.encrypt(puzzleName));
                    metadataJson.put("name", Globals.name);
                    metadataJson.put("type", "puzzle_solve");
                    metadata = metadataJson.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Uri contentUri = ((new Uri.Builder()).scheme("file").appendPath(Globals.gifPath)).build();
                ShareToMessengerParams shareToMessengerParams = ShareToMessengerParams.newBuilder(contentUri, "image/gif").setMetaData(metadata).build();

                MessengerUtils.shareToMessenger(static_instance, 10, shareToMessengerParams);
            }
        };

        if( type.equals("puzzle_challenge") ) {
            final ViewGroup current = (ViewGroup) static_instance.getWindow().getDecorView().getRootView();
            current.removeView(findViewById(R.id.puzzle_guess));

            Button challengeButton = (Button) findViewById(R.id.button_section).findViewById(R.id.guess_button);
            challengeButton.setText("Challenge");
            challengeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Globals.challengeFriends(static_instance.getBaseContext(), challenge_layouts, static_instance.getViewChanges(), shareChallenge);
                }
            });
        }
    }

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
                        TextView tv = (TextView) v.findViewById(R.id.questionText);
                        String text = "Solve\n" + puzzleName + "!";
                        tv.setText(text);
                    }
                }
        };
        return viewChanges;
    }
}
