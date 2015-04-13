package com.moonfrog.cyf.puzzles;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.moonfrog.cyf.Globals;
import com.moonfrog.cyf.R;
import com.moonfrog.cyf.view.ViewUpdateCall;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by srinath on 13/04/15.
 */
public class PuzzlesChallengeActivity extends Activity {
    public static PuzzlesChallengeActivity static_instance = null;

    private String puzzle_name;

    protected int[] challenge_layouts = new int[] {
        R.layout.challenge_hangman_gif_1,
        R.layout.challenge_cab_gif_2,
        R.layout.challenge_hangman_gif_3
    };

    protected Runnable shareChallenge;

    private CallbackManager callbackManager = null;

    public void onCreate(Bundle savedInstanceState) {
        static_instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.puzzle_challenge);

        puzzle_name = getIntent().getExtras().getString("challenge", "");

        String[] puzzleText = Globals.getDataFromFile(getBaseContext(), Globals.puzzleDirectory + '/' + puzzle_name).split("<!!Separator!!>\n");
        if( puzzleText.length < 3 ) {
            throw new IllegalArgumentException("The File " + puzzle_name + " should have at question, answer and explanation separated by <!!Separator!!>");
        }

        ((TextView) findViewById(R.id.puzzle_question_title)).setText(puzzle_name);
        ((TextView) findViewById(R.id.puzzle_text)).setText(puzzleText[0]);
        ((TextView) findViewById(R.id.explaination)).setText(puzzleText[2]);
        ((TextView) findViewById(R.id.puzzle_answer)).setText("Answer: " + puzzleText[1]);


        shareChallenge = new Runnable() {
            @Override
            public void run() {
                String metadata = "";
                try {
                    JSONObject metadataJson = new JSONObject();
                    metadataJson.put("challenge", Globals.encrypt(puzzle_name));
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


        if( Globals.name == "" ) {
            FacebookSdk.sdkInitialize(this.getApplicationContext());

            callbackManager = CallbackManager.Factory.create();
            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.e("facebook", "Logged In");
                            GraphRequest request = GraphRequest.newMeRequest(
                                    loginResult.getAccessToken(),
                                    new GraphRequest.GraphJSONObjectCallback() {
                                        @Override
                                        public void onCompleted(JSONObject object, GraphResponse response) {
                                            String name = null;
                                            try {
                                                JSONObject jsonObj = response.getJSONObject();
                                                name = jsonObj.get("name").toString();
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            if( name == null) {
                                                Log.e("facebook", "couldn't fetch graph object");
                                                return;
                                            }
                                            name = name.split(" ")[0];

                                            Globals.name = name;
                                            Globals.challengeFriends(static_instance.getBaseContext(), challenge_layouts, static_instance.getViewChanges(), shareChallenge);
                                        }
                                    });
                            Bundle parameters = new Bundle();
                            parameters.putString("fields", "id,name,link");
                            request.setParameters(parameters);
                            request.executeAsync();

                        }

                        @Override
                        public void onCancel() {
                            Log.e("facebooK", "login cancelled");
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Log.e("error while login", exception.toString());
                        }
                    });
        }
    }

    public void onChallengeClicked(View v) {
        if( Globals.name == "" ) {
            ArrayList<String> permissions = new ArrayList<>();
            permissions.add("public_profile");
            LoginManager.getInstance().logInWithReadPermissions(static_instance, permissions);
        } else {
            Globals.challengeFriends(static_instance.getBaseContext(), challenge_layouts, static_instance.getViewChanges(), shareChallenge);
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
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
                        String text = "Solve\n" + puzzle_name + "!";
                        tv.setText(text);
                    }
                }
        };
        return viewChanges;
    }
}
