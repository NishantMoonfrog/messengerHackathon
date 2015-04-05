package com.moonfrog.cyf.cab;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moonfrog.cyf.Globals;
import com.moonfrog.cyf.view.CategoryWordChallengeFragment;

import org.json.JSONException;

/**
 * Created by srinath on 05/04/15.
 */
public class CabChallengeFragment extends CategoryWordChallengeFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        challenge_categories = new String[] {
                "4 Letter",
                "5 Letter",
                "6 Letter",
                "7 Letter"
        };
        challenge_categories_singular = new String[] {
                "4 Letter word",
                "5 Letter word",
                "6 Letter word",
                "7 Letter word"
        };
        try {
            challenge_word_list = Globals.word_list.getJSONObject("cab");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
