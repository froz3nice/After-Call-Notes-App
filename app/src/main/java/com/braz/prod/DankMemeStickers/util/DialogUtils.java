package com.braz.prod.DankMemeStickers.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AlertDialog;

import com.braz.prod.DankMemeStickers.Activities.Play.PlayActivity;
import com.braz.prod.DankMemeStickers.Interfaces.DialogCallback;
import com.braz.prod.DankMemeStickers.Interfaces.DialogListener;
import com.braz.prod.DankMemeStickers.Interfaces.MemeSelectCallback;
import com.braz.prod.DankMemeStickers.Interfaces.SongDialogCallback;
import com.braz.prod.DankMemeStickers.Models.DataProvider;
import com.braz.prod.DankMemeStickers.Models.Song;
import com.braz.prod.DankMemeStickers.R;
import com.braz.prod.DankMemeStickers.SoundPlayer;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class DialogUtils {
    public static void showSaveDialog(Context activity, DialogCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Do you want to save your dank creation?");
        builder.setPositiveButton("Yeah", (dialog, which) -> {
            dialog.cancel();
            callback.savePressed();
        });
        builder.setNegativeButton("nope", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public static void showMemeSelectDialog(PlayActivity activity, MemeSelectCallback callback) {

        String[] listItems = {"Coffin Dance", "Thug Life"};

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Choose meme");
        builder.setIcon(R.drawable.doge);

        builder.setItems(listItems, (d, which) -> {
            if(which == 0){
                callback.coffinDance();
            }
            if(which == 1) callback.thugLife();
            d.cancel();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void showRecordDialog(Context activity, DialogListener callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Record thug life video?");
        builder.setMessage("Tap on screen to stop recording :)");
        builder.setPositiveButton("Yeah", (dialog, which) -> {
            dialog.cancel();
            callback.savePressed();
        });
        builder.setNegativeButton("nope", (dialog, which) -> {
            dialog.cancel();
            callback.cancelPressed();
        });
        builder.show();
    }

    public static void showUpgradeDialog(Context context, DialogCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage("Do you want an app without ads? Then click Yeah m8 :)");

        builder.setPositiveButton("Yeah", (dialog, which) -> {
            dialog.cancel();
            callback.savePressed();
        });
        builder.setNegativeButton("Nope", (dialog, which) -> dialog.cancel());
        builder.show();
    }


    public static void showMakeVideoDialog(Context context, DialogCallback callback) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Save video? ");
        builder.setMessage("This might take some time depending on video length :)");

        builder.setPositiveButton("save", (dialog, which) -> {
            dialog.cancel();
            callback.savePressed();
        });
        builder.setNegativeButton("nope", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    public static void showSelectSong(Context context, SongDialogCallback callback) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.radiobutton_dialog);
        ArrayList<Song> songs = DataProvider.getSongs();
        RadioGroup rg = (RadioGroup) dialog.findViewById(R.id.radio_group);
        Button save = (Button) dialog.findViewById(R.id.btn_save);
        SoundPlayer player = new SoundPlayer(context);
        AtomicReference<Song> song = new AtomicReference<>(songs.get(0));
        dialog.setCanceledOnTouchOutside(true);
        for (int i = 0; i < songs.size(); i++) {
            RadioButton rb = new RadioButton(context);
            int finalI = i;
            rb.setOnClickListener(view -> {
                song.set(songs.get(finalI));
                player.playSound(songs.get(finalI).getRes());
            });
            rb.setText(songs.get(i).getName());
            rg.addView(rb);
            if(i == 0){
                rb.setChecked(true);
            }
        }

        dialog.setOnCancelListener(dialogInterface -> {
            player.stopPlayer();
            dialog.dismiss();
        });

        save.setOnClickListener(view -> {
            callback.onSaved(song.get());
            dialog.cancel();
            dialog.dismiss();
            player.stopPlayer();
        });


        dialog.show();

    }

}
