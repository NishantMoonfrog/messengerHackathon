package com.moonfrog.cyf.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

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
import com.moonfrog.cyf.utils.GenerateGifFromBitmapsAsyncTask;
import com.moonfrog.cyf.utils.GenerateGifFromBitmapsAsyncTaskParams;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by srinath on 30/03/15.
 */
abstract public class CategoryWordChallengeActivity extends FragmentActivity {
    public static CategoryWordChallengeActivity static_instance = null;
    public static String selectedWord = "";
    public static String selectedTopic = "";
    public static int selectedPosition = 0;

    protected Class<? extends Fragment> challenge_fragment_class = null;

    protected int challenge_layouts[] = {
            R.layout.challenge_hangman_gif_3
    };

    private CallbackManager callbackManager = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_word_challenge);
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
            if(challenge_fragment_class == null) {
                challenge_fragment_class = CategoryWordChallengeFragment.class;
            }
            Fragment fragment = null;
            try {
                fragment = challenge_fragment_class.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
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
        final String path = "/sdcard/cyfTemp/";
        File folder = new File(path);
        if (!folder.exists()) {
            if( !folder.mkdirs() ) {
                Log.e("Couldn't create folder ", path);
                return;
            }
        }

        final ArrayList< Bitmap > bitmaps = new ArrayList<>();
        for(int i = 0 ; i < challenge_layouts.length ; i++) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View v = layoutInflater.inflate(challenge_layouts[i], null);
            getViewChanges()[i].updateView(v);
            if (v.getMeasuredHeight() <= 0) {
                v.measure(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            }
            Bitmap image = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(image);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            bitmaps.add(image);
        }

        final String gifPath = path + "challenge.gif";

        Runnable callback = new Runnable() {
            @Override
            public void run() {
                shareChallenge(gifPath);
            }
        };
        GenerateGifFromBitmapsAsyncTask generateGifFromBitmapsAsyncTask = new GenerateGifFromBitmapsAsyncTask();
        generateGifFromBitmapsAsyncTask.execute(new GenerateGifFromBitmapsAsyncTaskParams(bitmaps, gifPath, callback));
    }

    // Override!
    abstract protected void shareChallenge(String gifPath);

    protected ViewUpdateCall[] getViewChanges() {
        ViewUpdateCall[] viewChanges = {
                new ViewUpdateCall() {
                    @Override
                    public void updateView(View v) {}
                }
        };
        return viewChanges;
    }
}
