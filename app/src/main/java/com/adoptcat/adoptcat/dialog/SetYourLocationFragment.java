package com.adoptcat.adoptcat.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.adoptcat.adoptcat.R;
import com.adoptcat.adoptcat.fragments.MapsFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


public class SetYourLocationFragment extends DialogFragment implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener, View.OnClickListener, LocationListener {

    private GoogleMap mMap;
    private Button dialogCancelButton, dialogOkButton;
    private double latitude, longitude;
    private LocationManager locationManager;

    private FragmentManager fragmentManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_set_my_location, null);

        dialogOkButton = (Button) view.findViewById(R.id.dialogOkButton);
        dialogCancelButton = (Button) view.findViewById(R.id.dialogCancelButton);

        fragmentManager = getActivity().getSupportFragmentManager();



        /*SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync( this );
    */
        return view;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        dialogCancelButton.setOnClickListener( this );
        dialogOkButton.setOnClickListener( this );

        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //TODO Pede permissão para mostrar a localização do usuário
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        openMapsFragment();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //TODO Pedir novamente a permissão aqui de localização
        mMap.setOnMapClickListener( this );
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.dialogCancelButton:
                dismiss();
                break;
            case R.id.dialogOkButton:
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        startActivity( new Intent(Settings.ACTION_LOCALE_SETTINGS) );//Liga o GPS
    }

    @Override
    public void onStop() {
        super.onStop();
        locationManager.removeUpdates( this );
    }

    private void openMapsFragment() {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace( R.id.contentDialog, new MapsFragment());
        ft.commit();
    }
}
