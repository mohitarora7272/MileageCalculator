package com.mileagecalculator.fuelefficiency;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mileagecalculator.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

@SuppressWarnings({"SameParameterValue", "CanBeFinal", "UnusedParameters"})
class RecordAdapter extends BaseAdapter {
    private Context c;
    private ArrayList<Utils> list;

    RecordAdapter(Context ctx, int listCustomValue,
                  ArrayList<Utils> arrayList2) {
        c = ctx;
        list = arrayList2;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertview, ViewGroup parent) {
        final holder h1;
        if (convertview == null) {
            LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            h1 = new holder();
            convertview = inflater.inflate(R.layout.list_custom_value, null);
            h1.tv_date = (TextView) convertview.findViewById(R.id.tv_list_date);
            h1.tv_avg = (TextView) convertview.findViewById(R.id.tv_list_avg);

            convertview.setTag(h1);

        } else {
            h1 = (holder) convertview.getTag();
        }

        try {
            h1.tv_date.setText(URLDecoder.decode(list.get(position).getDate(), "UTF-8"));
            h1.tv_avg.setText(URLDecoder.decode(list.get(position).getAverage(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return convertview;
    }

    private static class holder {
        TextView tv_date;
        TextView tv_avg;
    }
}