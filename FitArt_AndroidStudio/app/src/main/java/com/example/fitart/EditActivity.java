package com.example.fitart;

import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.example.fitart.GetLocationService;
import com.example.fitart.MapStateManager;
import com.google.android.gms.maps.model.RoundCap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class EditActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String usersFileName;
    private Button saveButton;
    private Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_route);


        Intent intent = getIntent();
        usersFileName = intent.getStringExtra(MapRecordingActivity.EXTRA_MESSAGE);
        saveButton = findViewById(R.id.button_save);
        deleteButton = findViewById(R.id.button_delete);

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.setRetainInstance(true);
        mapFragment.getMapAsync(this);

        saveButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                SharedPreferences pref = getSharedPreferences("SAVED_ART", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.clear();
                Set<String> set = pref.getStringSet("FILE_NAMES", null);
                if(set == null)
                   set = new HashSet<String>();
                else if(set.contains(usersFileName)){
                    Intent intent = new Intent(EditActivity.this, GalleryActivity.class);
                    startActivity(intent);
                }
                set.add(usersFileName);
                editor.putStringSet("FILE_NAMES", set);
                editor.commit();
                Intent intent = new Intent(EditActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        });
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Context context = view.getContext();
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Are you sure you want to delete this art?").setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences prefs = getSharedPreferences("SAVED_ART", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        Set<String> set = prefs.getStringSet("FILE_NAMES", null);
                        set.remove(usersFileName);
                        editor.putStringSet("FILE_NAMES", set);
                        editor.commit();

                        Intent intent = new Intent(EditActivity.this, GalleryActivity.class);
                        startActivity(intent);

                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        MapStateManager mgr = new MapStateManager(this, usersFileName);
        CameraPosition position = mgr.getSavedCameraPosition();
        if (position != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            Toast.makeText(this, "entering Resume State", Toast.LENGTH_SHORT).show();
            mMap.moveCamera(update);

            mMap.setMapType(mgr.getSavedMapType());
            mgr.loadPolyListFromState();
            ArrayList<PolyLineData> newLines = mgr.getPolyLineList();
            PolyLineData newline;
            LatLng startLatLng;
            LatLng endLatLng;
            Cap roundCap = new RoundCap();
            for(int i = 0; i < newLines.size(); i++){
                newline =  newLines.get(i);
                startLatLng = newline.getStartlocation();
                endLatLng = newline.getEndlocation();
                mMap.addPolyline(new PolylineOptions().clickable(false).add(endLatLng, startLatLng).jointType(2).startCap(roundCap).endCap(roundCap));
            }
        }

    }

}
