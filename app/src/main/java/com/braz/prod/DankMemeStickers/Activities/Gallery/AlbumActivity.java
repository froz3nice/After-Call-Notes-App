package com.braz.prod.DankMemeStickers.Activities.Gallery;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.braz.prod.DankMemeStickers.R;
import com.braz.prod.DankMemeStickers.util.Utils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class AlbumActivity extends AppCompatActivity {
    GridView galleryGridView;
    ArrayList<HashMap<String, String>> mediaList = new ArrayList<HashMap<String, String>>();
    String album_name = "";
    LoadAlbumImages loadAlbumTask;
    private ImageView message;
    SingleAlbumAdapter adapter;
    VideoRequestHandler videoRequestHandler;
    Picasso picasso;
    private int code = 55;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        Intent intent = getIntent();
        album_name = intent.getStringExtra("name");
        setTitle(album_name);

        message = findViewById(R.id.imageView);
        galleryGridView = (GridView) findViewById(R.id.galleryGridView);
        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if (dp < 360) {
            dp = (dp - 17) / 2;
            float px = Function.convertDpToPixel(dp, getApplicationContext());
            galleryGridView.setColumnWidth(Math.round(px));
        }
        videoRequestHandler = new VideoRequestHandler();
        picasso = new Picasso.Builder(this)
                .addRequestHandler(videoRequestHandler)
                .build();
        loadAlbumTask = new LoadAlbumImages();
        loadAlbumTask.execute();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == code && resultCode == Activity.RESULT_OK){
            Log.d("deletionnn","yess");
            adapter.deleteItem(openedPosition);
            galleryGridView.invalidate();
        }
    }

    class LoadAlbumImages extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mediaList.clear();
        }

        protected String doInBackground(String... args) {
            String xml = "";
            File[] media = new File(Utils.getPath(AlbumActivity.this)).listFiles();
            for(File file : media){
                String extension = file.getPath().substring(file.getPath().lastIndexOf("."));
                if(extension.equals(".jpg")){
                    Log.d("jpg",file.getPath()+ "   "+ extension);
                    mediaList.add(Function.mappingInbox("", file.getPath(), Function.converToTime(file.lastModified()), null, "image"));
                }else if(extension.equals(".mp4")){
                    Log.d("mp4",file.getPath()+ "   "+ extension);
                    mediaList.add(Function.mappingInbox("", file.getPath(), Function.converToTime(file.lastModified()), null, "video"));
                }
            }
           /* String path = "";
            String album = "";
            String timestamp = "";
            Uri imagesUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri urivideo = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.MediaColumns.DATE_MODIFIED};
            // Android/data/com.braz.prod.DankMemeStickers/files/ThugLifeCreator
           // Cursor videoCursor = getContentResolver().query(urivideo, projection, "bucket_display_name = \"" + album_name + "\"", null, null);
            Cursor videoCursor = getContentResolver().query(urivideo, projection,    MediaStore.Video.Media.DATA + " like ? ",
                    new String[] {"%Android\"data\"com.braz.prod.DankMemeStickers\"files\"ThugLifeCreator%"}, null, null);

            Cursor imagesCursor = getContentResolver().query(imagesUri, projection, "bucket_display_name = \"" + album_name + "\"", null, null);

            while (imagesCursor.moveToNext()) {
                path = imagesCursor.getString(imagesCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                Log.d("images_path",path);
                timestamp = imagesCursor.getString(imagesCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                mediaList.add(Function.mappingInbox(album, path, timestamp, Function.converToTime(timestamp), null, "image"));
            }
            while (videoCursor.moveToNext()) {

                path = videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                Log.d("images_path",path);
                timestamp = videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                mediaList.add(Function.mappingInbox(album, path, timestamp, Function.converToTime(timestamp), null, "video"));
            }
            imagesCursor.close();
            videoCursor.close();
*/
            Collections.sort(mediaList, new MapComparator(Function.KEY_TIMESTAMP, "dsc")); // Arranging photo album by timestamp decending
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {

            if (mediaList.isEmpty()) {
                message.setVisibility(View.VISIBLE);
            } else message.setVisibility(View.INVISIBLE);

            adapter = new SingleAlbumAdapter(AlbumActivity.this, mediaList);
            galleryGridView.setAdapter(adapter);
            galleryGridView.setOnItemClickListener((parent, view, position, id) -> {
                openedPosition = position;
                Intent intent = new Intent(AlbumActivity.this, GaleryPreview.class);
                intent.putExtra("path", mediaList.get(+position).get(Function.KEY_PATH));
                intent.putExtra("media_type", mediaList.get(+position).get(Function.TYPE));
                startActivityForResult(intent, code);
            });
        }
    }
    int openedPosition = 0;


    class SingleAlbumAdapter extends BaseAdapter {
        private Activity activity;
        private ArrayList<HashMap<String, String>> data;


        public SingleAlbumAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
            activity = a;
            data = d;
        }

        public void deleteItem(int pos){
            data.remove(pos);
            notifyDataSetChanged();
        }

        public int getCount() {
            return data.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            SingleAlbumViewHolder holder = null;
            if (convertView == null) {
                holder = new SingleAlbumViewHolder();
                convertView = LayoutInflater.from(activity).inflate(
                        R.layout.single_album_row, parent, false);

                holder.galleryImage = (ImageView) convertView.findViewById(R.id.galleryImage);
                holder.play_button = (ImageView) convertView.findViewById(R.id.play_button);

                convertView.setTag(holder);
            } else {
                holder = (SingleAlbumViewHolder) convertView.getTag();
            }
            holder.galleryImage.setId(position);

            final HashMap<String, String> song = data.get(position);
            if (song.get(Function.TYPE).equals("video")) {
                picasso.load("video" + ":" + song.get(Function.KEY_PATH))
                        .fit()
                        .centerCrop().into(holder.galleryImage);
                holder.play_button.setVisibility(View.VISIBLE);
            } else {
                picasso.load(new File(song.get(Function.KEY_PATH))) // Uri of the picture
                        .into(holder.galleryImage);
                holder.play_button.setVisibility(View.INVISIBLE);
            }
            return convertView;
        }
    }

}

class VideoRequestHandler extends RequestHandler {
    public static String SCHEME_VIDEO = "video";

    @Override
    public boolean canHandleRequest(Request data) {
        String scheme = data.uri.getScheme();
        return (SCHEME_VIDEO.equals(scheme));
    }

    @Override
    public Result load(Request data, int arg1) throws IOException {
        Bitmap bm = ThumbnailUtils.createVideoThumbnail(data.uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
        return new Result(bm, Picasso.LoadedFrom.DISK);
    }
}


class SingleAlbumViewHolder {
    ImageView galleryImage;
    ImageView play_button;
}
