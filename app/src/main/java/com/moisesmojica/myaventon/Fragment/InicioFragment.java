package com.moisesmojica.myaventon.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mapbox.android.core.location.LocationEngine;

import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;

import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;

import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.moisesmojica.myaventon.Activities.MainActivity;
import com.moisesmojica.myaventon.R;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class InicioFragment extends Fragment implements OnMapReadyCallback, LocationEngineListener,PermissionsListener {
    private Context mContext;
    private MapView mapView;
    private MapboxMap mapboxMap;


   private LocationEngine locationEngine;
   private LocationLayerPlugin locationLayerPlugin;
   private PermissionsManager permissionsManager;
   private Location originLocation;

    public InicioFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = container.getContext();
        // Inflate the layout for this fragment
        // return inflater.inflate(R.layout.fragment_inicio, container, false);
        Mapbox.getInstance(requireActivity(), getString(R.string.access_token));
        View view = inflater.inflate(R.layout.fragment_inicio, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this );


//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull MapboxMap mapboxMap) {
//
//                //  fragment_inicio.this.mapboxMap = mapboxMap;
//
//
//
//
//                mapboxMap.addMarker(new MarkerOptions()
//                        .title("Intersection")
//                        .snippet("H St NW with 15th St NW")
//                        .position(new LatLng(40.73581, -73.99155)));
//
//
//
//                mapboxMap.setStyle(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
//                    @Override
//                    public void onStyleLoaded(@NonNull Style style) {
//
//// Map is set up and the style has loaded. Now you can add data or make other map adjustments
//
//
//                    }
//                });
//
//
//
//
//            }
//        });





        return view;
    }


    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        enableLocation();

    }


    private void enableLocation(){
        if (PermissionsManager.areLocationPermissionsGranted(mContext)){

            InitLocationEngine();
            initLocationLayer();
        }else{
            permissionsManager = new PermissionsManager((PermissionsListener) mContext);
            permissionsManager.requestLocationPermissions((Activity) mContext);

        }
    }

    @SuppressWarnings("MissingPermission")
    private void InitLocationEngine(){
        locationEngine = new  LocationEngineProvider(mContext).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null){
            originLocation = lastLocation;
            cameraPosition(lastLocation);
        }else{
            locationEngine.addLocationEngineListener((LocationEngineListener) mContext);
        }





    }
    @SuppressWarnings("MissingPermission")
    private void initLocationLayer(){

        locationLayerPlugin = new LocationLayerPlugin(mapView,mapboxMap,locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);

    }


    private void cameraPosition(Location location){
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),10.0));
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(mContext,"Por favor dar permisos GPS para que la app funcione correctamente",Toast.LENGTH_LONG).show();


    }

    @Override
    public void onPermissionResult(boolean granted) {

        if(granted){
            enableLocation();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    @SuppressWarnings("MissingPermission")
    public void onConnected() {
        locationEngine.requestLocationUpdates();


    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null){
            originLocation = location;
            cameraPosition(location);
        }

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
       // mContext = context;
    }

    @SuppressWarnings("MissingPermission")
    @Override
    public void onStart() {
        super.onStart();
        if (locationEngine !=null){
            locationEngine.requestLocationUpdates();
        }

        if(locationLayerPlugin !=null){
            locationLayerPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (locationEngine!=null){
            locationEngine.removeLocationUpdates();
        }

        if (locationLayerPlugin!=null){
            locationLayerPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationEngine != null){
            locationEngine.deactivate();
        }
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }



}
