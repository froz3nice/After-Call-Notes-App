package com.braz.prod.DankMemeStickers;

import com.braz.prod.DankMemeStickers.R;

import java.util.ArrayList;

/**
 * Created by Juozas on 2018.02.19.
 */

public class PicTags {

    private boolean isPremium;

    public PicTags(boolean isPremium) {
        this.isPremium = isPremium;
    }

    public ArrayList<String> setOwnerIds(){
        ArrayList<String> ownerIds = new ArrayList<>();

        ownerIds.add("joint");
        ownerIds.add("glasses");
        ownerIds.add("thug_life_hat");
        if(isPremium) {
            ownerIds.add("pepe_crocodile");
            ownerIds.add("pepe_dickbutt");
            ownerIds.add("pepe");
            ownerIds.add("alone");
            ownerIds.add("angryf");
            ownerIds.add("happy_pepe");
            ownerIds.add("trollFace");
            ownerIds.add("megusta");
            ownerIds.add("peter_parker");
            ownerIds.add("savage_sponge");
            ownerIds.add("cj");
            ownerIds.add("big_smoke");
            ownerIds.add("cannabis");
            ownerIds.add("can420");
            ownerIds.add("cig");
            ownerIds.add("cage");
            ownerIds.add("doge");
            ownerIds.add("pink_guy");
            ownerIds.add("vsauce");
            ownerIds.add("crying_jordan");
            ownerIds.add("dickbutt");
            ownerIds.add("feels_bad");
            ownerIds.add("feels_why_u_no");
            ownerIds.add("spaidermem");
            ownerIds.add("squid");
            ownerIds.add("suit_pepe");
            ownerIds.add("baby_face");
            ownerIds.add("guy_with_hat");
            ownerIds.add("kim_chon_un");
            ownerIds.add("bush");
            ownerIds.add("graphic");
            ownerIds.add("wtf2");
            ownerIds.add("wtf3");
            ownerIds.add("cone_head");
            ownerIds.add("doge2");
            ownerIds.add("donald");
            ownerIds.add("hide_the_pain_harold");
            ownerIds.add("illuminati_eye");
            ownerIds.add("jap_feels_guy");
            ownerIds.add("red__memes_pepe");
            ownerIds.add("feels_bad_man_pepe");
            ownerIds.add("lemme_smash");
            ownerIds.add("pathetic");
            ownerIds.add("pepe_gun");
            ownerIds.add("pikachu");
            ownerIds.add("screaming_pepe");
            ownerIds.add("tap_head");
            ownerIds.add("becky_profile");
            ownerIds.add("chain");
        }
        return ownerIds;
    }

    public ArrayList<Integer> setImageDrawables(){
        ArrayList<Integer> imageList = new ArrayList<>();

        imageList.add(R.drawable.joint);
        imageList.add(R.drawable.right);
        imageList.add(R.drawable.thug_life_hat);
        if(isPremium) {
            imageList.add(R.drawable.pepe_crocodile);
            imageList.add(R.drawable.pepe_dickbutt);
            imageList.add(R.drawable.pepe);
            imageList.add(R.drawable.alone);
            imageList.add(R.drawable.angryf);
            imageList.add(R.drawable.happy_pepe);
            imageList.add(R.drawable.trollface);
            imageList.add(R.drawable.megusta);
            imageList.add(R.drawable.peter_parker);
            imageList.add(R.drawable.savage_sponge);
            imageList.add(R.drawable.cj);
            imageList.add(R.drawable.big_smoke);
            imageList.add(R.drawable.cannabis);
            imageList.add(R.drawable.can420);
            imageList.add(R.drawable.cig);
            imageList.add(R.drawable.cage);
            imageList.add(R.drawable.doge);
            imageList.add(R.drawable.pink_guy);
            imageList.add(R.drawable.vsauce);
            imageList.add(R.drawable.crying_jordan);
            imageList.add(R.drawable.dickbutt);
            imageList.add(R.drawable.feels_bad);
            imageList.add(R.drawable.feels_why_u_no);
            imageList.add(R.drawable.spaidermem);
            imageList.add(R.drawable.squid);
            imageList.add(R.drawable.suit_pepe);
            imageList.add(R.drawable.baby_face);
            imageList.add(R.drawable.guy_with_hat);
            imageList.add(R.drawable.kim_chon_un);
            imageList.add(R.drawable.bush);
            imageList.add(R.drawable.graphic);
            imageList.add(R.drawable.wtf2);
            imageList.add(R.drawable.wtf3);
            imageList.add(R.drawable.cone_head);
            imageList.add(R.drawable.doge2);
            imageList.add(R.drawable.donald);
            imageList.add(R.drawable.hide_the_pain_harold);
            imageList.add(R.drawable.illuminati_eye);
            imageList.add(R.drawable.jap_feels_guy);
            imageList.add(R.drawable.red__memes_pepe);
            imageList.add(R.drawable.feels_bad_man_pepe);
            imageList.add(R.drawable.lemme_smash);
            imageList.add(R.drawable.pathetic);
            imageList.add(R.drawable.pepe_gun);
            imageList.add(R.drawable.pikachu);
            imageList.add(R.drawable.screaming_pepe);
            imageList.add(R.drawable.tap_head);
            imageList.add(R.drawable.becky_profile);
            imageList.add(R.drawable.chain);
        }
        return imageList;
    }
}
