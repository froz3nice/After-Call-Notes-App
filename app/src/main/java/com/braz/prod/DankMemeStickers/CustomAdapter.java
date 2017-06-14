package com.braz.prod.DankMemeStickers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<Integer> {

    private ArrayList<Integer> dataSet;
    Context context;

    public class ViewHolder {
        ImageView image;
    }

    public CustomAdapter(ArrayList<Integer> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.context=context;
    }

    @Override
    public int getCount() {
        return dataSet.size();
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        final ViewHolder viewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.row_item, parent, false);
        }
        viewHolder.image = (ImageView)convertView.findViewById(R.id.image);

        Picasso.with(getContext()).load(dataSet.get(position)).fit()
                .error(dataSet.get(position)).into(viewHolder.image);

        convertView.setTag(viewHolder);

        return convertView;
    }
}