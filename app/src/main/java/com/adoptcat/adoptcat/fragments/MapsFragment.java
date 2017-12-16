package com.adoptcat.adoptcat.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adoptcat.adoptcat.Manifest;
import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.connection.Connection;
import com.adoptcat.adoptcat.model.Announcement;
import com.adoptcat.adoptcat.utilities.Utility;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.HashMap;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        ChildEventListener,GoogleMap.OnMarkerClickListener, LocationListener {

    private GoogleMap mMap;
    private static LatLng clickedLocation;
    private Marker mMarker;
    private LocationManager locationManager;
    private DatabaseReference databaseReference;
    private ArrayList<Announcement> a;
    private HashMap<Marker,Announcement> hashMap;
    private CoordinatorLayout fCat;


    public static boolean isDefineUserPosition;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_maps, null,false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync( this );
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        databaseReference = Connection.getAnnouncementsDatabaseReference();
        hashMap = new HashMap<>();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Utility.READ_EXTERNAL_STORAGE_REQUEST_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    mMap.setMyLocationEnabled(true);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, this );
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener( this );
        if(ActivityCompat.checkSelfPermission( getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions( getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        Utility.ACCESS_FINE_LOCATION_REQUEST_PERMISSION);
            } else {
                ActivityCompat.requestPermissions( getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        Utility.ACCESS_FINE_LOCATION_REQUEST_PERMISSION);
            }
        }
        else {
            mMap.setMyLocationEnabled( true );
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, this );
        }
        mMap.setOnMarkerClickListener( this );
        mMap.moveCamera(CameraUpdateFactory.zoomBy(15));

        MapStyleOptions styleOptions = MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.googlemap_style);
        mMap.setMapStyle(styleOptions);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        this.clickedLocation = latLng;
        if( isDefineUserPosition ) {
            if( mMarker != null ) mMarker.remove();
            mMarker = mMap.addMarker( new MarkerOptions().position(latLng).title(getString(R.string.mapfrag_imhere)).
                    icon( BitmapDescriptorFactory.fromResource(R.drawable.gpsyellow)));
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
        Marker marker = mMap.addMarker( new MarkerOptions().position(
                new LatLng( announcement.getLatitude(), announcement.getLongitude() )).
                title( announcement.getTitle() ).icon( BitmapDescriptorFactory.fromResource(R.drawable.gpsblue) ));
        hashMap.put( marker, announcement );
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


    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.i("MAKER", "Marker clicado!");
        Announcement a = hashMap.get(marker);
        Bundle bundle = new Bundle();
        AdoptCatsFragment fragment = new AdoptCatsFragment();

        bundle.putSerializable("announcement", a);
        fragment.setArguments(bundle);

        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace( R.id.main_content, fragment);
        ft.commit();

        return true;
    }


    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
        locationManager.removeUpdates( this );
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
