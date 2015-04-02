package com.moonfrog.cyf;

/**
 * Created by srinath on 31/03/15.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.facebook.login.LoginManager;
import com.moonfrog.cyf.view.SlidingTabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;

public class HangmanChallengeFragment extends Fragment {
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private String searchText = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hangman_challenge_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        final HangmanChallengePageAdapter adp = new HangmanChallengePageAdapter();
        adp.listViewStore = new ListView[Globals.hangman_challenge_categories.length];
        mViewPager.setAdapter(adp);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        final HangmanChallengeFragment _this = this;
        class CustomPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                adp.currentPosition = position;
                adp.updateWithSearchText(searchText);
            }
        }
        mSlidingTabLayout.setOnPageChangeListener(new CustomPageChangeListener());

        EditText searchBox = (EditText) view.findViewById(R.id.inputSearch);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                searchText = cs.toString().toLowerCase();
                adp.updateWithSearchText(searchText);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }


    class HangmanChallengePageAdapter extends PagerAdapter {
        public ListView[] listViewStore;
        int currentPosition = 0;
        @Override
        public int getCount() {
            return Globals.hangman_challenge_categories.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Globals.hangman_challenge_categories[position].toUpperCase();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // Inflate a new layout from our resources
            final HangmanChallengeActivity currentActivity = (HangmanChallengeActivity) getActivity();
            View view = currentActivity.getLayoutInflater().inflate(R.layout.hangman_challenge_pager_item, container, false);
            container.addView(view);

            final ListView listView = (ListView) view.findViewById(R.id.hangman_category_select);
            ListAdapter mAdapter = new ListAdapter(view.getContext(), Globals.getFilteredList(Globals.hangman_challenge_category_word_list[position], ""));
            listView.setAdapter(mAdapter);
            listViewStore[position] = listView;


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int inner_position, long id) {
                    currentActivity.selectedWord = Globals.hangman_challenge_category_word_list[position][inner_position];
                    currentActivity.selectedTopic = Globals.hangman_challenge_categories_singular[position];

                    if( Globals.name == "" ) {
                        ArrayList<String> permissions = new ArrayList<String>();
                        permissions.add("public_profile");
                        LoginManager.getInstance().logInWithReadPermissions(currentActivity, permissions);
                    } else {
                        currentActivity.challengeFriends();
                    }
                }
            });

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void updateWithSearchText(String text) {
            ListView listView = listViewStore[currentPosition];
            ListAdapter mAdapter = new ListAdapter(getActivity().getBaseContext(), Globals.getFilteredList(Globals.hangman_challenge_category_word_list[currentPosition], searchText));
            listView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }
}
