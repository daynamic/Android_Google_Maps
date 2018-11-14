package com.example.atzz.mapapplicationgooglelatest;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ReverseGeo extends AsyncTask<Location, Void, String> {

    private Context mContext;

    //Add a parameter for the onTaskComplete interface that we’ll be creating shortly//

    private OnTaskComplete mListener;

    ReverseGeo(Context applicationContext, OnTaskComplete listener) {
        mListener = listener;
        mContext = applicationContext;

    }

    //Publish the results of our AsyncTask; in this instance that’s the returned address//

    @Override
    //Override the onPostExecute() method//
    protected void onPostExecute(String address) {

    //Once the AsyncTask has finished,
    //call onTaskComplete and update your UI with the returned address//

        mListener.onTaskComplete(address);
        super.onPostExecute(address);
    }

//Implement AsyncTask’s doInBackground() method,
//where we’ll convert the Location object into an address//

    @Override
    protected String doInBackground(Location... params) {

        //Create a Geocoder object, which is a class that can perform geocoding operations//
        Geocoder mGeocoder = new Geocoder(mContext, Locale.getDefault());
        //Obtain a Location object//
        Location location = params[0];
        //Create an empty List of Address objects, which will eventually contain the returned address//
        List<Address> addresses = null;
        //Create a String to hold the formatted address//
        String printAddress = "";
        //Obtain the list of addresses for the current location, using getFromLocation//
        try {
            addresses = mGeocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
        } catch (IOException ioException) {
            printAddress = mContext.getString(R.string.no_address);

        }
        //If the geocoder can't match the coordinates to an address, then return an empty list//

        if (addresses.size() == 0) {
            if (printAddress.isEmpty()) {
                //If the address list is empty, then display the no_address string//
                printAddress = mContext.getString(R.string.no_address);
            }
        } else {
            //If the list isn’t empty, then create an ArrayList of strings//
            Address address = addresses.get(0);
            ArrayList<String> addressList = new ArrayList<>();
            //Fetch the address lines, using getMaxAddressLineIndex,
            //and then and combine them into a String//
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressList.add(address.getAddressLine(i));
            }

            printAddress = TextUtils.join(
                    ",",
                    addressList);
        }

        //Return the printAddress object//
        return printAddress;
    }

    //Create the OnTaskComplete interface, which takes a String as an argument//

    interface OnTaskComplete {
        void onTaskComplete(String result);
    }
}
