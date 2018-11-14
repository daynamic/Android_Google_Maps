package com.example.atzz.mapapplicationgooglelatest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements ReverseGeo.OnTaskComplete,OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private Button button;
    private TextView textview;
    private boolean addressRequest;

    //Create a member variable of the FusedLocationProviderClient type//

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        button = findViewById(R.id.button);
        textview = findViewById(R.id.textview);
        //Obtain the SupportMapFragment//

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);
        //Initialize mFusedLocationClient//

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(
                this);
        //Create the onClickListener//
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Call getAddress, in response to onClick events//
                if (!addressRequest) {
                    getAddress();
                }
            }
        });

        //Create a LocationCallback object//

        mLocationCallback = new LocationCallback() {

            @Override

//Override the onLocationResult() method,
//which is where this app receives its location updates//

            public void onLocationResult(LocationResult locationResult) {
                if (addressRequest) {

//Execute ReverseGeo in response to addressRequest//

                    new ReverseGeo(MapsActivity.this, MapsActivity.this)

//Obtain the device's last known location from the FusedLocationProviderClient//

                            .execute(locationResult.getLastLocation());
                }
            }
        };
    }


    //Implement getAddress//
    private void getAddress() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            addressRequest = true;
            //Request location updates//
            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(),
                            mLocationCallback,
                            null);
            //If the geocoder retrieves an address, then display this address in the TextView//
            textview.setText(getString(R.string.address_text));
        }
    }

    //Specify the requirements for your application's location requests//
    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        //Specify how often the app should receive location updates, in milliseconds//
        locationRequest.setInterval(10000);
        return locationRequest;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //If the permission request has been granted, then call getAddress//
                    getAddress();
                } else {
                    Toast.makeText(this,
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onTaskComplete(String result) {
        if (addressRequest) {
            //Update the TextView with the reverse geocoded address//
            textview.setText(getString(R.string.address_text,
                    result));
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    //Override the onCreateOptionsMenu() method//

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the maps_menu resource//
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_menu, menu);
        return true;
    }

   //Override the onOptionsItemSelected() method//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal:
                //Use setMapType to change the map style based on the user’s selection//
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    public void onConnected(Bundle bundle) {
//To do//
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
