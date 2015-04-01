package com.moonfrog.cyf;

/**
 * Created by srinath on 31/03/15.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.messenger.MessengerUtils;
import com.facebook.messenger.ShareToMessengerParams;
import com.moonfrog.cyf.view.SlidingTabLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class HangmanChallengeFragment extends Fragment {

    public String[] categories = {
            "Countries",
            "Animals",
            "Cities",
            "Movies",
            "Sports",
            "Games"
    };

    public String[] topics = {
        "Country",
        "Animal",
        "City",
        "Movie",
        "Sports",
        "Game"
    };

    public String[][] category_word_list = {
            {},
            {},
            {},
            {},
            {},
            {}
    };

    public JSONObject word_list = null;

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private String searchText = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.hangman_challenge_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        try {
            InputStream is = getResources().openRawResource(R.raw.word_list);
            Writer writer = new StringWriter();
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            word_list = new JSONObject(writer.toString());

            for(int i = 0 ; i < categories.length; i++) {
                JSONArray list = word_list.getJSONArray(categories[i]);
                ArrayList<String> strList = new ArrayList<>();
                for(int j = 0, count = list.length(); j < count; j++) {
                    if( list.getString(j).length() < 15 ) {
                        strList.add(list.getString(j).toUpperCase());
                    }
                }
                category_word_list[i] = strList.toArray(new String[strList.size()]);
                Arrays.sort(category_word_list[i]);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        final HangmanChallengePageAdapter adp = new HangmanChallengePageAdapter();
        adp.listViewStore = new ListView[categories.length];
        mViewPager.setAdapter(adp);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        final HangmanChallengeFragment _this = this;
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                adp.currentPosition = position;
                adp.updateWithSearchText(searchText);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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
            return categories.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return categories[position].toUpperCase();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // Inflate a new layout from our resources
            final HangmanChallengeActivity currentActivity = (HangmanChallengeActivity) getActivity();
            View view = currentActivity.getLayoutInflater().inflate(R.layout.hangman_challenge_pager_item, container, false);
            container.addView(view);

            final ListView listView = (ListView) view.findViewById(R.id.hangman_category_select);
            ListAdapter mAdapter = new ListAdapter(view.getContext(), Globals.getFilteredList(category_word_list[position], ""));
            listView.setAdapter(mAdapter);
            listViewStore[position] = listView;


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int inner_position, long id) {
                    currentActivity.selectedWord = category_word_list[position][inner_position];
                    currentActivity.selectedTopic = topics[position];

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
            ListAdapter mAdapter = new ListAdapter(getActivity().getBaseContext(), Globals.getFilteredList(category_word_list[currentPosition], searchText));
            listView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }
}
