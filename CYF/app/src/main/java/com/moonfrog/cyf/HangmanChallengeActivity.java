package com.moonfrog.cyf;

import android.app.ActionBar;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RemoteViews;
import android.widget.SearchView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.moonfrog.cyf.Globals;
import com.moonfrog.cyf.view.FixedSizeSquareLayout;

import android.widget.AdapterView;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Created by srinath on 30/03/15.
 */
public class HangmanChallengeActivity extends FragmentActivity {
    public static HangmanChallengeActivity static_instance = null;
    public static String selectedWord = "";
    public static String selectedTopic = "";
    private CallbackManager callbackManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hangman_challenge);
        static_instance = this;
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
                                        static_instance.challengeFriends();
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

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            HangmanChallengeFragment fragment = new HangmanChallengeFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void challengeFriends() {
        final String shaHash = Globals.encrypt(selectedWord);

        // final String path = Environment.getExternalStorageState() + "/cyfTemp/";
        final String path = "/sdcard/cyfTemp/";
        File folder = new File(path);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if( !success ) {
            Log.e("Couldn't create folder ", path);
            return;
        }

        int layouts[] = {R.layout.challenge_hangman_gif_1, R.layout.challenge_hangman_gif_2, R.layout.challenge_hangman_gif_3};

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
        final String gifPath = path + "challenge.gif";
        Runnable callback = new Runnable() {
            @Override
            public void run() {
                String metadata = "";
                try {
                    JSONObject metadataJson = new JSONObject();
                    metadataJson.put("word", shaHash);
                    metadataJson.put("name", Globals.name);
                    metadata = metadataJson.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Uri.Builder b = new Uri.Builder();
                b.scheme("file").appendPath(gifPath);
                Uri contentUri = b.build();
                Log.e("uri ", "built");
                ShareToMessengerParams shareToMessengerParams =
                        ShareToMessengerParams.newBuilder(contentUri, "image/gif")
                                .setMetaData(metadata)
                                .build();

                MessengerUtils.shareToMessenger(
                        static_instance,
                        10,
                        shareToMessengerParams
                );
            }
        };
        Globals.makeGIF(static_instance, layouts, viewChanges, gifPath, callback);
    }
}
