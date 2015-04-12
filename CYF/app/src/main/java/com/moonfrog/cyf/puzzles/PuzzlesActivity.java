package com.moonfrog.cyf.puzzles;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.moonfrog.cyf.Globals;
import com.moonfrog.cyf.R;
import com.moonfrog.cyf.view.ListAdapter;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by srinath on 30/03/15.
 */
public class PuzzlesActivity extends Activity {

    public static PuzzlesActivity static_instance = null;

    public ArrayList<String> puzzles;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_choose);
        // setContentView(R.layout.challenge_hangman_gif_1);
        
        static_instance = this;

        ListView listView = (ListView) findViewById(R.id.challenge_select);

        puzzles = Globals.getFilesFromDir(getBaseContext(), Globals.puzzleDirectory);

        ArrayAdapter<String> mAdapter = null;
        try {
            mAdapter = new ArrayAdapter<>(getBaseContext(), R.layout.simplerow, puzzles);
        } catch ( Exception e ) {
            e.printStackTrace();
        }

        listView.setAdapter(mAdapter);

        // Click event for single list row
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PuzzlesActivity.this,  PuzzleSolveActivity.class);
                if(intent != null) {
                    Bundle params = new Bundle();
                    params.putString("type", "puzzle_challenge");
                    params.putString("challenge", static_instance.puzzles.get(position));
                    intent.putExtras(params);
                    startActivity(intent);
                }
            }
        });
    }

}
