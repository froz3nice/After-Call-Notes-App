package com.braz.prod.DankMemeStickers.Activities.Play;

import java.util.ArrayList;

public class ZoomData {
    public ArrayList<Double> getScaleData() {
        return scaleData;
    }

    public void setScaleData(ArrayList<Double> scaleData) {
        this.scaleData = scaleData;
    }

    ArrayList<Double> scaleData;
    float startTime;
    float endTime;
    float midX;
    float midY;

    public ZoomData(){
        scaleData = new ArrayList<>();
    }

    public void addScaleData(double scale){
        scaleData.add(scale);
    }

    public float getStartTime() {
        return startTime;
    }

    public void setStartTime(float startTime) {
        this.startTime = startTime;
    }

    public float getEndTime() {
        return endTime;
    }

    public void setEndTime(float endTime) {
        this.endTime = endTime;
    }

    public float getMidX() {
        return midX;
    }

    public void setMidX(float midX) {
        this.midX = midX;
    }

    public float getMidY() {
        return midY;
    }

    public void setMidY(float midY) {
        this.midY = midY;
    }
}
