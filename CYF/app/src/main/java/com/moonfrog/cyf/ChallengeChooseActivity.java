package com.moonfrog.cyf;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by srinath on 30/03/15.
 */
public class ChallengeChooseActivity extends Activity {

    public static ChallengeChooseActivity static_instance = null;

    String[] numbers = { "Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King" };
    ArrayList<String> QuestionForSliderMenu = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        static_instance = this;
    }

    public void onHangmanClick(View v) {
        setContentView(R.layout.hangman_challenge);
        ListView listView = (ListView) findViewById(R.id.hangman_category_select);

        for (String s : numbers) {
            QuestionForSliderMenu.add(s);
        }

        ListAdapter mAdapter = new ListAdapter(this, QuestionForSliderMenu);
        listView.setAdapter(mAdapter);
    }
}
