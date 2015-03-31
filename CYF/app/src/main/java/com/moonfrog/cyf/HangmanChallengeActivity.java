package com.moonfrog.cyf;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;

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

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doSearch(query);
        }
    }

/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        // Configure the search info and add any event listeners

        return super.onCreateOptionsMenu(menu);
    }*/

    public void doSearch(String query) {
        int q = 3;
        q *= 4;
        query += "" + q;
        Log.e("Rads", query);
        int b = q;
    }
}
