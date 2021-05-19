package com.example.moconmcs;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.databinding.ObservableMap;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static android.content.Context.LOCATION_SERVICE;

public class FoodMapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private static List<Placemark> placemarks;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_map, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
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
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //액티비티가 처음 생성될 때 실행되는 함수

        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity().getApplicationContext(),
                    "권한이 거부되었습니다. 권한을 허용해주세요.", Toast.LENGTH_LONG).show();
            return null;
        }
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (placemarks == null) placemarks = getPlacemarkList();
        for (Placemark placemark : placemarks) {
            Log.d("place", placemark.toString());
            LatLng place = new LatLng(placemark.getPos().get(0), placemark.getPos().get(1));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(place);
            markerOptions.title(placemark.getName());
            markerOptions.snippet(placemark.getDesc());
            googleMap.addMarker(markerOptions);
        }
        LatLng curPos = new LatLng(37.56, 126.97);
        if (getActivity() != null) {
            Location location = getLastKnownLocation();
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                curPos = new LatLng(latitude, longitude);
                Toast.makeText(getActivity().getApplicationContext(), "위치 정보를 가져오는 데에 성공했습니다."
                        + latitude + "," + longitude, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "위치 정보를 가져오는 데에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
            if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPos, 13));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, 13));
        }
    }

    private List<Placemark> getPlacemarkList() {
        LinkedList<Placemark> placemarks = new LinkedList<>();
        Gson gson = new Gson();
        AssetManager assetManager = getResources().getAssets();
        try {
            InputStream is = assetManager.open("places.json");
            StringBuilder builder = new StringBuilder();
            Scanner scanner = new Scanner(is);
            while(scanner.hasNextLine()) {
                builder.append(scanner.nextLine());
                builder.append('\n');
            }
            String jsonStr = builder.toString();
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONArray jsonArray = jsonObject.getJSONArray("list");

            int len = jsonArray.length();
            int index = 0;
            while(index < len) {
                Placemark placemark = gson.fromJson(jsonArray.get(index).toString(), Placemark.class);
                placemarks.add(placemark);
                index++;
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return placemarks;
    }
}