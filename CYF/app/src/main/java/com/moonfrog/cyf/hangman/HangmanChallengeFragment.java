package com.moonfrog.cyf.hangman;

/**
 * Created by srinath on 31/03/15.
 */

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moonfrog.cyf.Globals;
import com.moonfrog.cyf.R;
import com.moonfrog.cyf.view.CategoryWordChallengeFragment;

import org.json.JSONException;

public class HangmanChallengeFragment extends CategoryWordChallengeFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        challenge_categories = new String[] {
                "Countries",
                "Animals",
                "Cities",
                "Movies",
                "Sports",
                "Games"
        };
        challenge_categories_singular = new String[] {
                "Country",
                "Animal",
                "City",
                "Movie",
                "Sports",
                "Game"
        };
        try {
            challenge_word_list = Globals.word_list.getJSONObject("hangman");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
