package com.braz.prod.DankMemeStickers.Activities.Play;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.braz.prod.DankMemeStickers.Models.ImageModel;
import com.braz.prod.DankMemeStickers.R;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.braz.prod.DankMemeStickers.Activities.Play.PlayFunctions.getDrawableUri;

public class StickersAdapter extends ArrayAdapter<ImageModel> {

    private ArrayList<ImageModel> dataSet;
    Context context;

    public class ViewHolder {
        ImageView image;
        SimpleDraweeView gif;
    }

    Picasso picasso;

    public StickersAdapter(ArrayList<ImageModel> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.context = context;
        picasso = Picasso.get();
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
        ImageModel data = dataSet.get(position);
        viewHolder.image = convertView.findViewById(R.id.image);
        viewHolder.gif = convertView.findViewById(R.id.gif);

        if (data.getType() == ImageModel.Type.gif) {
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setUri(getDrawableUri(data.getRes())).setAutoPlayAnimations(true).build();
            viewHolder.gif.setController(controller);
            viewHolder.gif.setVisibility(View.VISIBLE);
            viewHolder.image.setVisibility(View.GONE);
        } else {
            picasso.load(data.getRes()).fit()
                    .error(data.getRes()).into(viewHolder.image);
            viewHolder.gif.setVisibility(View.GONE);
            viewHolder.image.setVisibility(View.VISIBLE);
        }
        convertView.setTag(viewHolder);

        return convertView;
    }
}