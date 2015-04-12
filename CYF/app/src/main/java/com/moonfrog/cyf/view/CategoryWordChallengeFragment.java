package com.moonfrog.cyf.view;

/**
 * Created by srinath on 31/03/15.
 */

import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.facebook.login.LoginManager;
import com.moonfrog.cyf.Globals;
import com.moonfrog.cyf.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CategoryWordChallengeFragment extends Fragment {
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private String searchText = "";

    protected String[] challenge_categories = {
            "Default Category 1",
            "Default Category 2"
    };

    protected String[] challenge_categories_singular = {
            "Def Cat 1",
            "Def Cat 2"
    };

    protected JSONObject challenge_word_list = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(challenge_word_list == null) {
            try {
                challenge_word_list = Globals.word_list.getJSONObject("default");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return inflater.inflate(R.layout.category_word_challenge_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        final CategoryWordChallengePageAdapter adp = new CategoryWordChallengePageAdapter();
        adp.listViewStore = new ListView[challenge_categories.length];
        adp.listContext = getActivity().getBaseContext();
        mViewPager.setAdapter(adp);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);

        final CategoryWordChallengeFragment _this = this;
        class CustomPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                adp.currentPosition = position;
                try {
                    adp.updateWithSearchText(searchText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        mSlidingTabLayout.setOnPageChangeListener(new CustomPageChangeListener());

        EditText searchBox = (EditText) view.findViewById(R.id.inputSearch);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                searchText = cs.toString().toLowerCase();
                try {
                    adp.updateWithSearchText(searchText);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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


    class CategoryWordChallengePageAdapter extends PagerAdapter {
        public ListView[] listViewStore;
        Context listContext;
        int currentPosition = 0;
        @Override
        public int getCount() {
            return challenge_categories.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return challenge_categories[position].toUpperCase();
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // Inflate a new layout from our resources
            final CategoryWordChallengeActivity currentActivity = (CategoryWordChallengeActivity) getActivity();
            View view = currentActivity.getLayoutInflater().inflate(R.layout.category_word_challenge_pager_item, container, false);
            container.addView(view);

            final ListView listView = (ListView) view.findViewById(R.id.category_word_category_select);

            ArrayAdapter<String> mAdapter = null;
            try {
                mAdapter = new ArrayAdapter<>(getActivity().getBaseContext(), R.layout.simplerow, Globals.getFilteredList(challenge_word_list.getJSONArray(challenge_categories[position]), searchText));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            listView.setAdapter(mAdapter);
            listViewStore[position] = listView;


            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int inner_position, long id) {
                    currentActivity.selectedPosition = position;
                    try {
                        currentActivity.selectedWord = challenge_word_list.getJSONArray(challenge_categories[position]).getString(inner_position);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    currentActivity.selectedTopic = challenge_categories_singular[position];

                    if( Globals.name == "" ) {
                        ArrayList<String> permissions = new ArrayList<>();
                        permissions.add("public_profile");
                        LoginManager.getInstance().logInWithReadPermissions(currentActivity, permissions);
                    } else {
                        Globals.challengeFriends(currentActivity.getBaseContext(), currentActivity.challenge_layouts, currentActivity.getViewChanges(), currentActivity.shareChallenge);
                    }
                }
            });

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public void updateWithSearchText(String text) throws JSONException {
            ListView listView = listViewStore[currentPosition];
            Log.e("updating ", text + " " + (getActivity().getBaseContext() == null ? 1 : 0));
            ArrayAdapter<String> mAdapter = new ArrayAdapter<>(listContext, R.layout.simplerow, Globals.getFilteredList(challenge_word_list.getJSONArray(challenge_categories[currentPosition]), searchText));
            listView.setAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
        }
    }
}
