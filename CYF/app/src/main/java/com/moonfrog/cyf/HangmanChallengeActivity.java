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

    String[][] challenges_icons = {
            {"Word 1", ""},
            {"Word 2", ""},
            {"Word 3", ""},
            {"Word 4", ""},
            {"Word U", ""},
            {"Word V", ""},
            {"Word W", ""},
            {"Word X", ""},
            {"Word Y", ""},
            {"Word Z", ""}
    };
    ArrayList<ListAdapter.ListElement> sliderMenu = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hangman_challenge);
        static_instance = this;

        ListView listView = (ListView) findViewById(R.id.hangman_category_select);

        for (String s[] : challenges_icons) {
            sliderMenu.add(new ListAdapter.ListElement(s[0], s[1]));
        }

        ListAdapter mAdapter = new ListAdapter(this, sliderMenu);
        listView.setAdapter(mAdapter);
    }
}
