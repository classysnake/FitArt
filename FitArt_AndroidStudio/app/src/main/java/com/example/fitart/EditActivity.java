package com.example.fitart;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.example.fitart.GetLocationService;
import com.example.fitart.MapStateManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import yuku.ambilwarna.AmbilWarnaDialog;

public class EditActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String usersFileName;
    private Button saveButton;
    private Set<String> userFileSet = new HashSet<String>();

    ImageButton colorButton;
    ImageView colorSwatchImage;
    int defaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_route);

        colorButton = findViewById(R.id.button_colorEditing);
        colorButton.setOnClickListener(colorButtonOnClickListener);
        colorSwatchImage = findViewById(R.id.image_colorSwatchEditing);
        defaultColor = ContextCompat.getColor(EditActivity.this, R.color.colorPrimaryDark);

        Intent intent = getIntent();
        usersFileName = intent.getStringExtra(MapRecordingActivity.EXTRA_MESSAGE);
        saveButton = findViewById(R.id.button_save);

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        saveButton.setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

                SharedPreferences pref = getSharedPreferences("SAVED_ART", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                Set<String> set = pref.getStringSet("FILE_NAMES", null);
                if(set == null)
                   set = new HashSet<String>();
                else if(set.contains(usersFileName)){
                    Intent intent = new Intent(EditActivity.this, GalleryActivity.class);
                    startActivity(intent);
                }

                set.add(usersFileName);

                userFileSet.addAll(set);

                editor.putStringSet("FILE_NAMES", userFileSet);
                editor.commit();
                Intent intent = new Intent(EditActivity.this, GalleryActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        SharedPreferences savedState = PreferenceManager.getDefaultSharedPreferences(this);
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
            for(int i = 0; i < newLines.size(); i++){
                newline =  newLines.get(i);
                startLatLng = newline.getStartlocation();
                endLatLng = newline.getEndlocation();
                mMap.addPolyline(new PolylineOptions().add(endLatLng, startLatLng));
            }
        }

    }

    private View.OnClickListener colorButtonOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            openColorPicker();
        }
    };

    public void openColorPicker() {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                defaultColor = color;

                //Attempt to update Color Swatch...Failed
                PorterDuff.Mode mMode = PorterDuff.Mode.SRC_ATOP;
                colorSwatchImage.setBackgroundColor(defaultColor);

                //Debug purposes
                Toast.makeText(EditActivity.this, "color:" + defaultColor, Toast.LENGTH_SHORT).show();

            }
        });
        ambilWarnaDialog.show();

    }

}
