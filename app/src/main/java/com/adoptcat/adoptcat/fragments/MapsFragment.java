package com.adoptcat.adoptcat.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.activities.RegisterActivity;
import com.adoptcat.adoptcat.connection.Connection;
import com.adoptcat.adoptcat.model.Announcement;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        ChildEventListener {

    private GoogleMap mMap;
    private static LatLng clickedLocation;
    private Marker mMarker;
    private LocationManager locationManager;
    private DatabaseReference databaseReference;

    public static boolean isDefineUserPosition;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_maps, null,false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync( this );
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        databaseReference = Connection.getAnnouncementsDatabaseReference();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onResume() {
        super.onResume();

        databaseReference.addChildEventListener( this );
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener( this );
        mMap.setMyLocationEnabled( true );
    }

    @Override
    public void onMapClick(LatLng latLng) {
        this.clickedLocation = latLng;
        if( isDefineUserPosition ) {
            if( mMarker != null ) mMarker.remove();
            mMarker = mMap.addMarker( new MarkerOptions().position(latLng).title(getString(R.string.mapfrag_imhere)));
            mMap.moveCamera( CameraUpdateFactory.newLatLng( latLng ));
            RegisterCatFragment.setVisibleConfirmFloatingActionButton();
        }
    }

    public static void setIsDefineUserPositionTrue() {
        isDefineUserPosition = true;
    }
    public static void setIsDefineUserPositionFalse() {
        isDefineUserPosition = false;
    }

    public static LatLng getClickedLocation() {
        return clickedLocation;
    }




    //ChildEventListener
    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Announcement announcement = dataSnapshot.getValue( Announcement.class );
        mMap.addMarker( new MarkerOptions().position(
                new LatLng( announcement.getLatitude(), announcement.getLongitude())).
                title( announcement.getTitle() ) );
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }


}
