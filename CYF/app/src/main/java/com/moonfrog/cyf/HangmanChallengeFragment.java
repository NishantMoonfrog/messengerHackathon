package com.moonfrog.cyf;

/**
 * Created by srinath on 31/03/15.
 */

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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

public class HangmanChallengeFragment extends Fragment {

    public String[] categories = {
            "Countries",
            "Animals",
            "Cities",
            "Movies",
            "Sports",
            "Games"
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
                String[] strList = new String[list.length()];
                for(int j = 0, count = list.length(); j < count; j++) {
                    strList[j] = list.getString(j);
                }
                category_word_list[i] = strList;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new HangmanChallengePageAdapter());

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    class HangmanChallengePageAdapter extends PagerAdapter {
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
            return categories[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            // Inflate a new layout from our resources
            View view = getActivity().getLayoutInflater().inflate(R.layout.hangman_challenge_pager_item, container, false);
            container.addView(view);

            ListView listView = (ListView) view.findViewById(R.id.hangman_category_select);

//            final String[][] challenges_icons = {
//                    {"Word 1", ""},
//                    {"Word 2", ""},
//                    {"Word 3", ""},
//                    {"Word 4", ""},
//                    {"Word U", ""},
//                    {"Word V", ""},
//                    {"Word W", ""},
//                    {"Word X", ""},
//                    {"Word Y", ""},
//                    {"Word Z", ""}
//            };
            ArrayList<ListAdapter.ListElement> sliderMenu = new ArrayList<>();

            for (String s : category_word_list[position]) {
                sliderMenu.add(new ListAdapter.ListElement(s, ""));
            }

            ListAdapter mAdapter = new ListAdapter(view.getContext(), sliderMenu);
            listView.setAdapter(mAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int inner_position, long id) {
                    String shaHash = AESEncryption.encrypt(category_word_list[position][inner_position]);
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
                    MessengerUtils.shareToMessenger(getActivity(), 10, shareToMessengerParams);
                }
            });

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}