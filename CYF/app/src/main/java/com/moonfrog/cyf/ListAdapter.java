package com.moonfrog.cyf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by srinath on 30/03/15.
 */
public class ListAdapter extends BaseAdapter {
    public static class ListElement {
        String name = "";
        String icon = "";
        public ListElement(String name_val, String icon_val) {
            this.name = name_val;
            this.icon = icon_val;
        }
    };

    private Context parentContext;
    private ArrayList<ListElement> mainList;

    public ListAdapter(Context applicationContext, ArrayList<ListElement> questionForSliderMenu) {
        super();
        this.mainList = questionForSliderMenu;
        this.parentContext = applicationContext;
    }

    @Override
    public int getCount() {
        return mainList.size();
    }

    @Override
    public Object getItem(int position) {
        return mainList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) parentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row_stack, parent, false);
            //convertView = inflater.inflate(R.layout.custom_row_stack, null);

            TextView tv1 = (TextView) convertView.findViewById(R.id.row_textView1);
            ImageView imageIcon = (ImageView) convertView.findViewById(R.id.row_imageView1);

            try {
                tv1.setText(((ListElement)getItem(position)).name);
                if(!((ListElement)getItem(position)).icon.equals("")) {
                    imageIcon.setImageResource(parentContext.getResources().getIdentifier(((ListElement)getItem(position)).icon, "drawable",  parentContext.getPackageName()));
                } else {
                    imageIcon.setVisibility(View.GONE);
                    //((ViewManager)imageIcon.getParent()).removeView(imageIcon);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return convertView;
    }
}
