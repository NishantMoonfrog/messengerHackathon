package com.moonfrog.cyf;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by srinath on 30/03/15.
 */
public class HangmanChallengeActivity extends Activity {
    public static HangmanChallengeActivity static_instance = null;

    String[] numbers = { "Ace", "2", "3", "4", "5", "6", "7", "8", "9", "10", "Jack", "Queen", "King" };
    ArrayList<String> QuestionForSliderMenu = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hangman_challenge);
        static_instance = this;

        ListView listView = (ListView) findViewById(R.id.hangman_category_select);

        for (String s : numbers) {
            QuestionForSliderMenu.add(s);
        }

        ListAdapter mAdapter = new ListAdapter(this, QuestionForSliderMenu);
        listView.setAdapter(mAdapter);
    }
}
