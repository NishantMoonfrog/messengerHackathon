package com.moonfrog.cyf;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.moonfrog.cyf.cab.CabChallengeActivity;
import com.moonfrog.cyf.hangman.HangmanChallengeActivity;
import com.moonfrog.cyf.view.ListAdapter;

import java.util.ArrayList;

/**
 * Created by srinath on 30/03/15.
 */
public class ChallengeChooseActivity extends Activity {

    public static ChallengeChooseActivity static_instance = null;

    String[][] modes_icons = {
        {"Hangman", "hangman"},
        {"Cows & Bulls", "cowsandbulls"},
        {"Trivia", "trivia"},
        {"Spot Me!", "spotme"},
        {"Coming Soon!", ""}
    };
    ArrayList<ListAdapter.ListElement> sliderMenu = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_choose);
        // setContentView(R.layout.challenge_hangman_gif_1);
        
        static_instance = this;


        ListView listView = (ListView) findViewById(R.id.challenge_select);

        for (String s[] : modes_icons) {
            sliderMenu.add(new ListAdapter.ListElement(s[0], s[1]));
        }

        ListAdapter mAdapter = new ListAdapter(this, sliderMenu);
        listView.setAdapter(mAdapter);

        // Click event for single list row
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                switch(position) {
                    case 0:
                        intent = new Intent(ChallengeChooseActivity.this,  HangmanChallengeActivity.class);
                        break;
                    case 1:
                        intent = new Intent(ChallengeChooseActivity.this,  CabChallengeActivity.class);
                        break;
                    default:
                        break;
                }
                if(intent != null) {
                    startActivity(intent);
                }
            }
        });
    }
}
