package com.moonfrog.cyf.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;

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

import org.json.JSONObject;

import java.io.File;

/**
 * Created by srinath on 30/03/15.
 */
public class CategoryWordChallengeActivity extends FragmentActivity {
    public static CategoryWordChallengeActivity static_instance = null;
    public static String selectedWord = "";
    public static String selectedTopic = "";

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

        final String gifPath = path + "challenge.gif";
        Runnable callback = new Runnable() {
            @Override
            public void run() {
                String metadata = "";
                try {
                    JSONObject metadataJson = new JSONObject();
                    metadataJson.put("word", Globals.encrypt(selectedWord));
                    metadataJson.put("name", Globals.name);
                    metadata = metadataJson.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Uri contentUri = ((new Uri.Builder()).scheme("file").appendPath(gifPath)).build();
                ShareToMessengerParams shareToMessengerParams = ShareToMessengerParams.newBuilder(contentUri, "image/gif").setMetaData(metadata).build();

                MessengerUtils.shareToMessenger(static_instance, 10, shareToMessengerParams);
            }
        };
        Globals.makeGIF(static_instance, challenge_layouts, getViewChanges(), gifPath, callback);
    }

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
