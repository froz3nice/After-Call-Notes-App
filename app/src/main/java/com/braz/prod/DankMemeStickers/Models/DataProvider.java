package com.braz.prod.DankMemeStickers.Models;

import com.braz.prod.DankMemeStickers.R;

import java.util.ArrayList;

/**
 * Created by Juozas on 2018.02.19.
 */

public class DataProvider {

    public static ArrayList<ImageModel> getImageDrawables() {
        ArrayList<ImageModel> imageList = new ArrayList<>();

        imageList.add(new ImageModel(R.drawable.joint, ImageModel.Type.image, "joint"));
        imageList.add(new ImageModel(R.drawable.glasses, ImageModel.Type.image, "glasses"));
        imageList.add(new ImageModel(R.drawable.joint2, ImageModel.Type.gif, "joint2"));
        imageList.add(new ImageModel(R.drawable.thug_life_text, ImageModel.Type.image, "thug_life_text"));
        imageList.add(new ImageModel(R.drawable.obey, ImageModel.Type.image, "obey"));
        imageList.add(new ImageModel(R.drawable.thug2, ImageModel.Type.image, "thug2"));
        imageList.add(new ImageModel(R.drawable.chain, ImageModel.Type.image, "chain"));
        imageList.add(new ImageModel(R.drawable.thug3, ImageModel.Type.image, "thug3"));
        imageList.add(new ImageModel(R.drawable.chain2, ImageModel.Type.image, "chain2"));
        imageList.add(new ImageModel(R.drawable.thug_hat2, ImageModel.Type.image, "thug_hat2"));
        imageList.add(new ImageModel(R.drawable.cj, ImageModel.Type.image, "cj"));
        imageList.add(new ImageModel(R.drawable.big_smoke, ImageModel.Type.image, "big_smoke"));
        imageList.add(new ImageModel(R.drawable.cannabis, ImageModel.Type.image, "cannabis"));
        imageList.add(new ImageModel(R.drawable.can420, ImageModel.Type.image, "can420"));
        imageList.add(new ImageModel(R.drawable.cig, ImageModel.Type.image, "cig"));
        imageList.add(new ImageModel(R.drawable.obama_gif, ImageModel.Type.gif, "obama_gif"));
        imageList.add(new ImageModel(R.drawable.thunder_gif, ImageModel.Type.gif, "thunder_gif"));
        imageList.add(new ImageModel(R.drawable.travolta_gif, ImageModel.Type.gif, "travlota_gif"));
        imageList.add(new ImageModel(R.drawable.pikachu_pixel, ImageModel.Type.image, "pikachu_pixel"));
        imageList.add(new ImageModel(R.drawable.thug_life_hat, ImageModel.Type.image, "thug_life_hat"));
        imageList.add(new ImageModel(R.drawable.pepe_crocodile, ImageModel.Type.image, "pepe_crocodile"));
        imageList.add(new ImageModel(R.drawable.pepe_dickbutt, ImageModel.Type.image, "pepe_dickbutt"));
        imageList.add(new ImageModel(R.drawable.pepe, ImageModel.Type.image, "pepe"));
        imageList.add(new ImageModel(R.drawable.alone, ImageModel.Type.image, "alone"));
        imageList.add(new ImageModel(R.drawable.angryf, ImageModel.Type.image, "angryf"));
        imageList.add(new ImageModel(R.drawable.happy_pepe, ImageModel.Type.image, "happy_pepe"));
        imageList.add(new ImageModel(R.drawable.trollface, ImageModel.Type.image, "trollFace"));
        imageList.add(new ImageModel(R.drawable.megusta, ImageModel.Type.image, "megusta"));
        imageList.add(new ImageModel(R.drawable.peter_parker, ImageModel.Type.image, "peter_parker"));
        imageList.add(new ImageModel(R.drawable.savage_sponge, ImageModel.Type.image, "savage_sponge"));
        imageList.add(new ImageModel(R.drawable.cage, ImageModel.Type.image, "cage"));
        imageList.add(new ImageModel(R.drawable.doge, ImageModel.Type.image, "doge"));
        imageList.add(new ImageModel(R.drawable.crying_jordan, ImageModel.Type.image, "crying_jordan"));
        imageList.add(new ImageModel(R.drawable.dickbutt, ImageModel.Type.image, "dickbutt"));
        imageList.add(new ImageModel(R.drawable.feels_bad, ImageModel.Type.image, "feels_bad"));
        imageList.add(new ImageModel(R.drawable.feels_why_u_no, ImageModel.Type.image, "feels_why_u_no"));
        imageList.add(new ImageModel(R.drawable.spaidermem, ImageModel.Type.image, "spaidermem"));
        imageList.add(new ImageModel(R.drawable.squid, ImageModel.Type.image, "squid"));
        imageList.add(new ImageModel(R.drawable.suit_pepe, ImageModel.Type.image, "suit_pepe"));
        imageList.add(new ImageModel(R.drawable.baby_face, ImageModel.Type.image, "baby_face"));
        imageList.add(new ImageModel(R.drawable.guy_with_hat, ImageModel.Type.image, "guy_with_hat"));
        imageList.add(new ImageModel(R.drawable.kim_chon_un, ImageModel.Type.image, "kim_chon_un"));
        imageList.add(new ImageModel(R.drawable.cone_head, ImageModel.Type.image, "cone_head"));
        imageList.add(new ImageModel(R.drawable.doge2, ImageModel.Type.image, "doge2"));
        imageList.add(new ImageModel(R.drawable.donald, ImageModel.Type.image, "donald"));
        imageList.add(new ImageModel(R.drawable.illuminati_eye, ImageModel.Type.image, "illuminati_eye"));
        imageList.add(new ImageModel(R.drawable.jap_feels_guy, ImageModel.Type.image, "jap_feels_guy"));
        imageList.add(new ImageModel(R.drawable.red__memes_pepe, ImageModel.Type.image, "red__memes_pepe"));
        imageList.add(new ImageModel(R.drawable.feels_bad_man_pepe, ImageModel.Type.image, "feels_bad_man_pepe"));
        imageList.add(new ImageModel(R.drawable.lemme_smash, ImageModel.Type.image, "lemme_smash"));
        imageList.add(new ImageModel(R.drawable.pepe_gun, ImageModel.Type.image, "pepe_gun"));
        imageList.add(new ImageModel(R.drawable.pikachu, ImageModel.Type.image, "pikachu"));
        imageList.add(new ImageModel(R.drawable.screaming_pepe, ImageModel.Type.image, "screaming_pepe"));
        imageList.add(new ImageModel(R.drawable.tap_head, ImageModel.Type.image, "tap_head"));
        imageList.add(new ImageModel(R.drawable.becky_profile, ImageModel.Type.image, "becky_profile"));

        return imageList;
    }

    public static ArrayList<Song> getSongs() {
        ArrayList<Song> songs = new ArrayList<>();
        songs.add(new Song(R.raw.fuck_da_police, "F the police"));
        songs.add(new Song(R.raw.damn_gangsta, "Damn gangsta"));
        songs.add(new Song(R.raw.gta_sa, "gta"));
        songs.add(new Song(R.raw.move_bitch, "Move beach"));
        songs.add(new Song(R.raw.serial_killer, "Serial killer"));
        songs.add(new Song(R.raw.madafaka, "Madafaka"));
        songs.add(new Song(R.raw.where_the_hood_at, "Where the hood at"));
        songs.add(new Song(R.raw.silence, "No song"));
        return songs;
    }
}
