package com.moonfrog.cyf.view;

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

import com.facebook.messenger.ShareToMessengerParams;
import com.moonfrog.cyf.ChallengeChooseActivity;
import com.moonfrog.cyf.Globals;
import com.moonfrog.cyf.R;
import com.moonfrog.cyf.main;

import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.Arrays;

/**
 * Created by srinath on 31/03/15.
 */
abstract public class CategoryWordSolveActivity extends Activity {
    public static CategoryWordSolveActivity static_instance = null;

    protected int solve_layout = -1;

    protected String current_status = "";
    protected String final_word = "";
    protected String tried_characters = "";
    protected String challengerName = "";
    protected int num_wrong_choices = 0;
    protected boolean completed = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        static_instance = this;
        super.onCreate(savedInstanceState);

        if(solve_layout == -1) {
            solve_layout = R.layout.category_word_solve;
        }
        setContentView(solve_layout);

        challengerName = getIntent().getExtras().getString("challengerName", "");
        final_word = getIntent().getExtras().getString("challenge", "");

        if(current_status.equals("")) {
            current_status = final_word.replaceAll("[A-Z]", "_");
        }

        updateStatus();
    }

    abstract public void onCharButtonClick(View v);

    abstract public void updateStatus();
}
