package com.moonfrog.cyf;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by srinath on 30/03/15.
 */
public class ListAdapter extends BaseAdapter {

    private ArrayList<String> mainList;
    ArrayList<String> QuestionForSliderMenu = new ArrayList<String>();

    public ListAdapter(Context applicationContext, ArrayList<String> questionForSliderMenu) {
        super();
        this.mainList = questionForSliderMenu;
    }

    public ListAdapter() {
        super();
        this.mainList = QuestionForSliderMenu;
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
            LayoutInflater inflater = (LayoutInflater) main.static_instance.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_row_stack, null);
        }

        TextView tv1 = (TextView) convertView.findViewById(R.id.row_textView1);
        ImageView imageIcon = (ImageView) convertView.findViewById(R.id.row_imageView1);

        try {
            tv1.setText(" List Item "+ " : " + position);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }
}
