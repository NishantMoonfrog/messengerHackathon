package com.moonfrog.cyf;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
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
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.moonfrog.cyf.Globals;
import android.widget.AdapterView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by srinath on 30/03/15.
 */
public class HangmanChallengeActivity extends FragmentActivity {
    public static HangmanChallengeActivity static_instance = null;
    public static String selectedWord = "";
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
                        Profile profile = Profile.getCurrentProfile();
                        Globals.name = profile.getFirstName() + " " + profile.getLastName();
                        static_instance.challengeFriends();
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.e("facebooK", "login cancelled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
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
        Log.e("encrypted key ", shaHash);

        LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View v = layoutInflater.inflate(R.layout.challenge_hangman_gif_1, null);
        final ViewGroup current = (ViewGroup) getWindow().getDecorView().getRootView();
        current.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));

        v.post(new Runnable() {
            @Override
            public void run() {
                v.findViewById(1);
                Bitmap image = Globals.getBitmapFromView(v);
                String path = MediaStore.Images.Media.insertImage(static_instance.getContentResolver(), image, "Title", null);
                Uri contentUri = Uri.parse(path);

                String metadata = "";
                try {
                    JSONObject metadataJson = new JSONObject();
                    metadataJson.put("word", shaHash);
                    metadata = metadataJson.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                ShareToMessengerParams shareToMessengerParams =
                        ShareToMessengerParams.newBuilder(contentUri, "image/*")
                                .setMetaData(metadata)
                                .build();

                // Sharing from an Activity
                MessengerUtils.shareToMessenger(
                    static_instance,
                    10,
                    shareToMessengerParams
                );
                current.removeViewAt(0);
            }
        });
    }
}
