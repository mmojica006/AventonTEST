package com.moisesmojica.myaventon.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import com.moisesmojica.myaventon.R;
import com.moisesmojica.myaventon.api.Api;
import com.moisesmojica.myaventon.api.RequestHandler;
import com.moisesmojica.myaventon.helper.Constant;
import com.moisesmojica.myaventon.models.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InicioActivity extends MainActivity  implements OnMapReadyCallback, LocationEngineListener, PermissionsListener {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private  String TAG ="TAG";


    List<Result> resultList;


    private LocationEngine locationEngine;
    private LocationLayerPlugin locationLayerPlugin;
    private PermissionsManager permissionsManager;
    private Location originLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_inicio, null, false);

        loadData();

        drawerLayout.addView(contentView, 0);
        navigationView.setCheckedItem(R.id.menu_inicio);

        navigationView = (NavigationView) findViewById(R.id.navview);
        navigationView.setNavigationItemSelectedListener(this);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        if(isOnLine())
        mapView.getMapAsync(this );
        else
            Toast.makeText(getBaseContext(),getString(R.string.smgNotOnline),Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_inicio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_menu_inicio) {

            if(isOnLine()){
                HashMap<String,String> map = new HashMap<String, String>();
                map.put("name", Constant.USER_NOMBRE);

                map.put("lat",Double.toString(originLocation.getLatitude()));
                map.put("long",Double.toString(originLocation.getLongitude()));

                sendPost(map);
                return true;
            }else{
                Toast.makeText(getBaseContext(),getString(R.string.smgNotOnline),Toast.LENGTH_LONG).show();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        enableLocation();
    }

    private void   enableLocation(){
        if (PermissionsManager.areLocationPermissionsGranted(this)){

            InitLocationEngine();
            initLocationLayer();
        }else{
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void InitLocationEngine(){
        locationEngine = new LocationEngineProvider(this).obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();

        Double latitude = lastLocation.getLatitude();
        Double longitud = lastLocation.getLongitude();
        Log.i(TAG,latitude.toString() +" "+longitud.toString());

        if (lastLocation != null){
            originLocation = lastLocation;
           cameraPosition(lastLocation);

        }else{
            locationEngine.addLocationEngineListener(this);
        }
    }

    private void cameraPosition(Location location){
        mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),location.getLongitude()),10.0));
    }

    @SuppressWarnings("MissingPermission")
    private void initLocationLayer(){

        locationLayerPlugin = new LocationLayerPlugin(mapView,mapboxMap,locationEngine);
        locationLayerPlugin.setLocationLayerEnabled(true);
        locationLayerPlugin.setCameraMode(CameraMode.TRACKING);
        locationLayerPlugin.setRenderMode(RenderMode.NORMAL);
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
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this,getString(R.string.smsPermissionGPS),Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPermissionResult(boolean granted) {
      if(granted){
          enableLocation();
      }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode,permissions,grantResults);
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
    @Override
    public void onBackPressed() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            } else {
                super.onBackPressed();
            }
        }
    }

    private void sendPost( HashMap<String, String> params){
        PerformNetworkRequest request = new PerformNetworkRequest(Api.URL_TEST, params, Api.CODE_POST_REQUEST);
        request.execute();
    }

    private class PerformNetworkRequest extends AsyncTask<Void, Void, String> {

        String url;
        HashMap<String, String> params;
        int requestCode;

        PerformNetworkRequest(String url, HashMap<String, String> params, int requestCode){
            this.url = url;
            this.params = params;
            this.requestCode = requestCode;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getBaseContext(),getString(R.string.smgExecutePost),Toast.LENGTH_LONG).show();
        }
        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            try {
                JSONObject object = new JSONObject(s);
                if(object.getInt("error")== 0 ){
                    Toast.makeText(getBaseContext(),getString(R.string.smgResultPost),Toast.LENGTH_LONG).show();
                    refreshContenidoList(object.getJSONArray("result"));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {

            RequestHandler requestHandler = new RequestHandler();
            if(requestCode == Api.CODE_POST_REQUEST)
                return requestHandler.sendPostRequest(url, params);

            if ((requestCode == Api.CODE_GET_REQUEST))
                return requestHandler.sendGetRequest(url);

            return null;
        }
    }

    private void refreshContenidoList(JSONArray contenido) throws JSONException{

        resultList.clear();

        for(int i = 0; i < contenido.length(); i++){

            JSONObject obj = contenido.getJSONObject(i);

            resultList.add(new Result(
                    obj.getInt("id"),
                    obj.getString("lt"),
                    obj.getString("lg"),
                    obj.getString("name")
            ));

        }
        addToMap();
        saveData();
    }

    private void saveData() {
        SharedPreferences sharedPreferences= getSharedPreferences("shared preferences",MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(resultList);
        editor.putString("Result list",json);
        editor.apply();
    }
    private void loadData(){
        SharedPreferences sharedPreferences= getSharedPreferences("shared preferences",MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("Result list",null);
        Type type = new TypeToken<ArrayList<Result>>() {}.getType();
        resultList = gson.fromJson(json,type);

        if (resultList ==null){
            resultList = new ArrayList<>();
        }

       }

    private void addToMap() {

        if (resultList.size() > 0) {
            for (int i = 0; i < resultList.size(); i++) {

                mapboxMap.addMarker(new MarkerOptions()
                        .title(resultList.get(i).getName())
                        .snippet(resultList.get(i).getName())
                        .position(new LatLng(Double.valueOf(resultList.get(i).getLt()), Double.valueOf(resultList.get(i).getLg()))));
            }
        }
    }
    public boolean isOnLine(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnectedOrConnecting()){
            return  true;
        }else{
            return  false;
        }

    }
}
