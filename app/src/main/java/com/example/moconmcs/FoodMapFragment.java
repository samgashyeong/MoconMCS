package com.example.moconmcs;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.DisplayMetrics;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
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

import static android.content.Context.LOCATION_SERVICE;

public class FoodMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private MapView mapView;
    private static List<Placemark> placemarks;
    private View marker_root_view;
    private TextView tv_marker;

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

    private void setCustomMarkerView() {
        marker_root_view = LayoutInflater.from(requireContext()).inflate(R.layout.map_marker_layout, null);
        tv_marker = (TextView) marker_root_view.findViewById(R.id.tv_map_marker);
    }

    private void addMarker(Placemark placemark) {
        LatLng position = new LatLng(placemark.getPos().get(0), placemark.getPos().get(1));

        String title = placemark.getName();
        if(title.length() > 5) title = title.substring(0, 4) + "..";
        tv_marker.setText(title);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.title(placemark.getName());
        markerOptions.position(position);
        markerOptions.snippet(placemark.getDesc());

        Bitmap bitmap = createDrawableFromView(getActivity(), marker_root_view);
        if(bitmap != null)
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));

        googleMap.addMarker(markerOptions);
    }

    private Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
        googleMap.animateCamera(center);

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setCustomMarkerView();
        if (placemarks == null) placemarks = getPlacemarkList();
        for (Placemark placemark : placemarks) {
            addMarker(placemark);
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
            Handler handler = new Handler();
            LatLng finalCurPos = curPos;
            handler.postDelayed(() -> googleMap
                    .animateCamera(CameraUpdateFactory.newLatLngZoom(finalCurPos, 13))
                    , 1000);
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