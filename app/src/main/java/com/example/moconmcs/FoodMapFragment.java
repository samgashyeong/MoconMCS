package com.example.moconmcs;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

public class FoodMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private MapView mapView;
    private static List<Placemark> placemarks;
    private Marker selectedMarker;
    private TextView placeTitle, placeDesc, placeRate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_map, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.getMapAsync(this);

        placeTitle = view.findViewById(R.id.map_title);
        placeDesc = view.findViewById(R.id.map_desc);
        placeRate = view.findViewById(R.id.map_rate);

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
    public void onSaveInstanceState(@NotNull Bundle outState) {
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
        if (mapView != null) {
            mapView.onCreate(savedInstanceState);
        }
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) requireActivity().getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        if (ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                requireActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(requireActivity().getApplicationContext(),
                    "권한이 거부되었습니다. 권한을 허용해주세요.", Toast.LENGTH_LONG).show();
            setPermisson();
            return null;
        }
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    private void addMarker(Placemark placemark) {
        LatLng position = new LatLng(placemark.getPos().get(0), placemark.getPos().get(1));

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(placemark.getName());
        markerOptions.position(position);
        markerOptions.snippet(placemark.getDesc());

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_0));

        googleMap.addMarker(markerOptions);
    }

    private Marker changeMarker(Marker origin, boolean isSelected) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(origin.getTitle());
        markerOptions.position(origin.getPosition());
        markerOptions.snippet(origin.getSnippet());

        if(isSelected)
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_1));
        else
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.map_marker_0));

        origin.remove();
        return googleMap.addMarker(markerOptions);
    }

    private void addMarkers() {
        for (Placemark placemark : placemarks) {
            addMarker(placemark);
        }
    }

    private void changeSelectedMarker(Marker marker) {
        if (selectedMarker != null) {
            changeMarker(selectedMarker, false);
        }

        if (marker != null) {
            selectedMarker = changeMarker(marker, true);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
        googleMap.animateCamera(center);

        changeSelectedMarker(marker);

        placeDesc.setText(marker.getSnippet());
        placeTitle.setText(marker.getTitle());
        placeRate.setText("★★★★☆");

        return true;
    }

    private void updateCameraToCurrentLocation() {
        Location location = getLastKnownLocation();
        LatLng curPos = new LatLng(37.56, 126.97);
        if (location != null) {
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            curPos = new LatLng(latitude, longitude);
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, 12));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.googleMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        if (placemarks == null) placemarks = getPlacemarkList();
        addMarkers();

        LatLng curPos = new LatLng(37.56, 126.97);
        if (getActivity() != null) {
            Location location = getLastKnownLocation();
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                curPos = new LatLng(latitude, longitude);
            } else {
                Toast.makeText(getActivity().getApplicationContext(), "위치 정보를 가져오는 데에 실패했습니다.", Toast.LENGTH_SHORT).show();
            }
            if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            Handler handler = new Handler();
            LatLng finalCurPos = curPos;
            googleMap.setMinZoomPreference(8);
            handler.postDelayed(() -> googleMap
                    .animateCamera(CameraUpdateFactory.newLatLngZoom(finalCurPos, 12))
                    , 500);
        }

        selectedMarker = null;
        placeTitle.setText("       ");
        placeRate.setText("☆☆☆☆☆");
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

    private void setPermisson(){
        Log.d(TAG, "setPermisson: 퍼미션 띄어짐");
        int permisson1 = ContextCompat.checkSelfPermission(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);

        if(permisson1 != PackageManager.PERMISSION_GRANTED){
            makeRequest();
        }

    }


    private void makeRequest(){
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1002);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1002:{
                if(grantResults.length==0 || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(requireContext(), "허용을 해주셔야지만 앱이 정상적으로 실행이됩니다.", Toast.LENGTH_SHORT).show();
                    setPermisson();
                }
                onMapReady(googleMap);
            }
        }
    }
}