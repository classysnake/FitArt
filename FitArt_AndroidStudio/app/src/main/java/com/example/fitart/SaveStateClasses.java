package com.example.fitart;


import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

class PolyLineData implements Serializable {

    private double startlat;
    private double startlong;
    private double endlat;
    private double endlong;


    public PolyLineData() {
    }

    public PolyLineData(LatLng startlocation, LatLng endlocation) {
        this.startlat = startlocation.latitude;
        this.startlong = startlocation.longitude;
        this.endlat = endlocation.latitude;
        this.endlong = endlocation.longitude;
    }

    public LatLng getStartlocation() {
        return new LatLng(startlat, startlong);
    }

    public void setStartlocation(LatLng startlocation) {
        this.startlat = startlocation.latitude;
        this.startlong = startlocation.longitude;
    }

    public LatLng getEndlocation() {
        return new LatLng(endlat, endlong);
    }

    public void setEndlocation(LatLng endlocation) {
        this.endlat = endlocation.latitude;
        this.endlong = endlocation.longitude;
    }
}

class MapStateManager {

    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String ZOOM = "zoom";
    private static final String BEARING = "bearing";
    private static final String TILT = "tilt";
    private static final String MAPTYPE = "MAPTYPE";
    private static final String POLYLINES = "polylines";
    private static final String DISTANCE = "distance";
    private static final String TIME = "time";
    private static final String COLOR = "color";
    private static final String PLAYBUTTON = "playbutton";
    private static final String LINESIZE = "linesize";

    private ArrayList<PolyLineData> polyLineList;
    private double milesTravled;
    private int color;
    private long timeTravled;
    private boolean playButton;
    private SharedPreferences mapStatePrefs;
    private float lineSize;

    public ArrayList<PolyLineData> getPolyLineList() {
        return polyLineList;
    }
    public void setColor(int col){
        color =  col;
    }
    public void setColorState(int col) {
        SharedPreferences.Editor editor = mapStatePrefs.edit();
        editor.putInt(COLOR, col);
        editor.commit();
    }
    public void setLineSize(float size){
        lineSize =  size;
    }
    public void setSizeState(float size) {
        SharedPreferences.Editor editor = mapStatePrefs.edit();
        editor.putFloat(LINESIZE, size);
        editor.commit();
    }
    public void setPlaybutton(boolean value){
        playButton = value;
    }
    public boolean getPlaybutton(){
        return mapStatePrefs.getBoolean(PLAYBUTTON, playButton);
    }
    public void addToPolyLineList(PolyLineData value) {
        polyLineList.add(value);
    }

    public void addMilesToSaveState(double miles){ milesTravled = miles;}

    public void addTimeToSaveState(long time){ timeTravled = time;}
    public void setPolylinesList(ArrayList<PolyLineData> list){
        polyLineList = list;
    }
    public void deletePolylineData(){
        SharedPreferences.Editor editor = mapStatePrefs.edit();
        Gson gson = new Gson();
        polyLineList = new ArrayList<>();
        String json = gson.toJson(polyLineList);
        editor.putString(POLYLINES, json);
        editor.commit();
    }

    public void loadPolyListFromState() {
        Gson gson = new Gson();
        String json = mapStatePrefs.getString(POLYLINES, null);
        Type type = new TypeToken<ArrayList<PolyLineData>>() {
        }.getType();
        polyLineList = gson.fromJson(json, type);
        if (polyLineList == null)
            polyLineList = new ArrayList<>();


    }

    public MapStateManager(Context context, String name) {
        mapStatePrefs = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        loadPolyListFromState();
    }

    public void saveMapState(GoogleMap mapMie) {
        SharedPreferences.Editor editor = mapStatePrefs.edit();
        CameraPosition position = mapMie.getCameraPosition();

        Gson gson = new Gson();
        String json = gson.toJson(polyLineList);
        editor.putString(POLYLINES, json);

        editor.putFloat(LATITUDE, (float) position.target.latitude);
        editor.putFloat(LONGITUDE, (float) position.target.longitude);
        editor.putFloat(ZOOM, position.zoom);
        editor.putFloat(TILT, position.tilt);
        editor.putFloat(BEARING, position.bearing);
        editor.putInt(MAPTYPE, mapMie.getMapType());
        editor.putFloat(DISTANCE, (float) milesTravled);
        editor.putLong(TIME, timeTravled);
        editor.putBoolean(PLAYBUTTON, playButton);
        editor.putInt(COLOR, color);
        editor.putFloat(LINESIZE, lineSize);
        editor.commit();
    }

    public CameraPosition getSavedCameraPosition() {
        double latitude = mapStatePrefs.getFloat(LATITUDE, 0);
        if (latitude == 0) {
            return null;
        }
        double longitude = mapStatePrefs.getFloat(LONGITUDE, 0);
        LatLng target = new LatLng(latitude, longitude);

        float zoom = mapStatePrefs.getFloat(ZOOM, 0);
        float bearing = mapStatePrefs.getFloat(BEARING, 0);
        float tilt = mapStatePrefs.getFloat(TILT, 0);

        CameraPosition position = new CameraPosition(target, zoom, tilt, bearing);
        return position;
    }
    public  void savePolylineData(){
        SharedPreferences.Editor editor = mapStatePrefs.edit();
        Gson gson = new Gson();

        String json = gson.toJson(polyLineList);
        editor.putString(POLYLINES, json);
        editor.commit();

    }
    public double getMilesTravled(){
        double miles = mapStatePrefs.getFloat(DISTANCE, 0);
        return miles;
    }
    public long getTimeTravled(){
        long time = mapStatePrefs.getLong(TIME, 0);
        return time;
    }

    public int getSavedMapType() {
        return mapStatePrefs.getInt(MAPTYPE, GoogleMap.MAP_TYPE_NORMAL);
    }
    public int getColor(){
        int color = mapStatePrefs.getInt(COLOR, 0);
        return color;
    }
    public float getLineSize(){
        float lineSize = mapStatePrefs.getFloat(LINESIZE, 0);
        return lineSize;
    }
}


