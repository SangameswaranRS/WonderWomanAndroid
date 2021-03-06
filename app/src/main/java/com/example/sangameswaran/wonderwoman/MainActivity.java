package com.example.sangameswaran.wonderwoman;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.example.sangameswaran.wonderwoman.Entities.CrimeReportEntity;
import com.example.sangameswaran.wonderwoman.Entities.GetCrimesApiEntity;
import com.example.sangameswaran.wonderwoman.Fragments.MyProfileFragment;
import com.example.sangameswaran.wonderwoman.RestCalls.RestClientImplementation;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleMap.OnMapClickListener{
    GoogleMap map;
    FloatingActionButton fab,relativeZoomFab;
    public List<LatLng> bounds;
    RelativeLayout mapContainerRl,fragmentContainer;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mapContainerRl.setVisibility(View.VISIBLE);
                    relativeZoomFab.setVisibility(View.VISIBLE);
                    fragmentContainer.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_dashboard:
                    mapContainerRl.setVisibility(View.GONE);
                    relativeZoomFab.setVisibility(View.GONE);
                    MyProfileFragment fragment=new MyProfileFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,fragment).commit();
                    fragmentContainer.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bounds=new ArrayList<>();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        fab=(FloatingActionButton)findViewById(R.id.fab);
        relativeZoomFab=(FloatingActionButton)findViewById(R.id.relativeZoomFab);
        mapContainerRl=(RelativeLayout)findViewById(R.id.mapContainerRl);
        fragmentContainer=(RelativeLayout)findViewById(R.id.fragmentContainer);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,BtConnectionActivity.class);
                startActivity(intent);
            }
        });
        relativeZoomFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relativeZoom(bounds);
            }
        });
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapClick(LatLng latLng) {
        if(map!=null){

        }
    }
    private void relativeZoom(List<LatLng> boundse){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(LatLng iterator: boundse){
            builder.include(iterator);
            LatLngBounds bounds = builder.build();
            int padding = 200;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            map.moveCamera(cu);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
        RestClientImplementation.getAllCrimeApi(new GetCrimesApiEntity(), new GetCrimesApiEntity.WonderWomanRestClientInterface() {
            @Override
            public void onGetAllInfo(GetCrimesApiEntity entity, VolleyError error) {
              for(CrimeReportEntity iterator : entity.getMessage()){
                  map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(iterator.getLatitude()),Double.parseDouble(iterator.getLongitude()))));
                  bounds.add(new LatLng(Double.parseDouble(iterator.getLatitude()),Double.parseDouble(iterator.getLongitude())));
              }
                relativeZoom(bounds);
            }
        },this);
    }

    @Override
    public void onBackPressed() {
        finishAffinity();
    }
}
