package com.moonfrog.cyf;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;

import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.moonfrog.cyf.AESEncryption;
import android.widget.AdapterView;

import org.json.JSONObject;

import java.io.File;
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String shaHash = AESEncryption.encrypt(challenges_icons[position][0]);
                Log.e("encrypted key ", shaHash);

                Uri contentUri = Uri.fromFile(new File("/sdcard/Downloads/img.jpg"));

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
                        shareToMessengerParams);
                }
        });

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
