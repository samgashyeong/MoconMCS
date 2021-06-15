package com.example.moconmcs.Main.FoodMap;

import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moconmcs.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;

public class FoodMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap googleMap;
    private MapView mapView;
    private static List<Placemark> placemarkList;
    private Marker selectedMarker;
    private TextView placeTitle, placeDesc;
    private RatingBar placeRate;
    private Button writeReviewBtn;

    private BitmapDescriptor recycleMarker, recycleSelectedMarker;

    private final ArrayList<ReviewInfo> arrayList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ProgressBar reviewLoading;
    private ReviewAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_food_map, container, false);
        mapView = view.findViewById(R.id.mapView);
        mapView.getMapAsync(this);

        placeTitle = view.findViewById(R.id.map_title);
        placeDesc = view.findViewById(R.id.map_desc);
        placeRate = view.findViewById(R.id.map_rate);
        reviewLoading = view.findViewById(R.id.map_review_loading);
        writeReviewBtn = view.findViewById(R.id.write_review_btn);
        writeReviewBtn.setVisibility(View.INVISIBLE);

        recyclerView = view.findViewById(R.id.reviews);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ReviewAdapter(arrayList);
        recyclerView.setAdapter(adapter);

        writeReviewBtn.setOnClickListener(v -> {
            openReviewWriteDialog();
        });

        return view;
    }

    private void openReviewWriteDialog() {
        Dialog dialog = new Dialog(requireActivity());
        dialog.setContentView(R.layout.layout_review_write);
        dialog.show();
        Button cancel = dialog.findViewById(R.id.btn_review_cancel);
        Button upload = dialog.findViewById(R.id.btn_review_upload);
        EditText content = dialog.findViewById(R.id.review_content);
        RatingBar ratingBar = dialog.findViewById(R.id.user_rating_bar_review);
        cancel.setOnClickListener(v -> dialog.dismiss());
        upload.setOnClickListener(v -> {
            String review_content = content.getText().toString();
            float rateNum = ratingBar.getRating();
            uploadReview(review_content, rateNum);
            dialog.dismiss();
        });
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mapView.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onLowMemory();
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

    private void modifyMarkerOptions(MarkerOptions markerOptions, String title, LatLng pos, String snippet, boolean isSelected) {
        markerOptions.title(title);
        markerOptions.position(pos);
        markerOptions.snippet(snippet);

        if(recycleMarker == null)
            recycleMarker = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(
                    R.drawable.ic_marker_icon
            ));
        if(recycleSelectedMarker == null)
            recycleSelectedMarker = BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(
                    R.drawable.ic_checkedmarker_icon
            ));

        if(isSelected)
            markerOptions.icon(recycleSelectedMarker);
        else
            markerOptions.icon(recycleMarker);
    }

    public Bitmap getBitmapFromVectorDrawable(int drawableId) {
        Drawable drawable = AppCompatResources.getDrawable(requireActivity().getApplicationContext(), drawableId);
        if(drawable == null) return null;
        Log.d(TAG, "getBitmapFromVectorDrawable: " + drawable.getIntrinsicWidth() + " / " + drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void addMarker(Placemark placemark) {
        LatLng position = new LatLng(placemark.getPos().get(0), placemark.getPos().get(1));
        MarkerOptions markerOptions = new MarkerOptions();
        modifyMarkerOptions(markerOptions, placemark.getName(), position, placemark.getDesc(), false);

        googleMap.addMarker(markerOptions);
    }

    private Marker changeMarker(Marker origin, boolean isSelected) {
        MarkerOptions markerOptions = new MarkerOptions();
        modifyMarkerOptions(markerOptions, origin.getTitle(), origin.getPosition(), origin.getSnippet(), isSelected);
        origin.remove();

        return googleMap.addMarker(markerOptions);
    }

    private void addMarkers() {
        for (Placemark placemark : placemarkList) {
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

    private void uploadReview(String content, float rate) {
        if(selectedMarker == null) return;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        db.collection("User").document(uid).get().addOnCompleteListener(userTask -> {
           if(userTask.isSuccessful()) {
               DocumentSnapshot userDoc = userTask.getResult();
               if(userDoc.get("name") != null) {
                   String name = userDoc.get("name") + "";
                   DocumentReference docRef = db.collection("Place").document(selectedMarker.getTitle());
                   docRef.get().addOnCompleteListener(task -> {
                       reviewLoading.setVisibility(View.GONE);
                       if(task.isSuccessful()) {
                           DocumentSnapshot placeDoc = task.getResult();
                           if(placeDoc.getData() == null) {
                               Map<String, Object> datas = new HashMap<>();
                               Map<String, ReviewInfo> reviewers = new HashMap<>();
                               datas.put("reviewers", reviewers);
                               docRef.set(datas);
                           }
                           else {
                               docRef.update("reviewers." + uid,
                                       new ReviewInfo(rate, content, name, Calendar.getInstance().getTimeInMillis()));
                               updateReviewList(selectedMarker.getTitle());
                           }
                       }
                   });
               }
           }
        });


    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
        googleMap.animateCamera(center);

        changeSelectedMarker(marker);

        placeDesc.setVisibility(View.VISIBLE);
        placeTitle.setVisibility(View.VISIBLE);
        placeRate.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
        writeReviewBtn.setVisibility(View.VISIBLE);
        placeDesc.setText(marker.getSnippet());
        placeTitle.setText(marker.getTitle());
        placeRate.setRating(0);
        placeRate.setIsIndicator(true);

        db.collection("Place").document(marker.getTitle()).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot placeDoc = task.getResult();
                if(placeDoc.get("reviewers." + uid) != null) writeReviewBtn.setText("리뷰 수정");
                else writeReviewBtn.setText("리뷰 작성");
            }
        });

        reviewLoading.setVisibility(View.VISIBLE);

        updateReviewList(marker.getTitle());

        return true;
    }

    @SuppressWarnings("unchecked")
    private void updateReviewList(String title) {
        arrayList.clear();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        DocumentReference docRef = db.collection("Place").document(title);
        docRef.get().addOnCompleteListener(task -> {
            reviewLoading.setVisibility(View.GONE);
            if(task.isSuccessful()) {
                int reviewerCount = 0;
                float averRate = 0;
                DocumentSnapshot document = task.getResult();
                if(document.getData() == null) {
                    Map<String, Object> datas = new HashMap<>();
                    Map<String, ReviewInfo> reviewers = new HashMap<>();
                    datas.put("reviewers", reviewers);
                    docRef.set(datas);
                }
                else {
                    Map<String, HashMap<String, Object>> reviews =
                            (Map<String, HashMap<String, Object>>) document.get("reviewers");
                    if (reviews != null) {
                        for(HashMap<String, Object> rv : reviews.values()) {
                            float rate = Float.parseFloat(rv.get("rate") + "");
                            String reviewText = rv.get("review") + "";
                            String name = rv.get("name") + "";
                            long timeStamp = Long.parseLong(rv.get("timestamp") + "");
                            arrayList.add(new ReviewInfo(rate, reviewText, name, timeStamp));
                            arrayList.sort((o1, o2) -> {
                                long dif = o2.getTimestamp() - o1.getTimestamp();
                                if (dif == 0) return 0;
                                if (dif > 0) return 1;
                                return -1;
                            });
                            adapter.notifyDataSetChanged();

                            reviewerCount++;
                            averRate += rate;
                        }
                    }
                }
                averRate /= reviewerCount;
                placeRate.setRating(averRate);
            }
        });
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
        if (placemarkList == null) placemarkList = getPlacemarkList();
        addMarkers();

        LatLng curPos = new LatLng(37.56, 126.97);
        if (getActivity() != null) {
            Location location = getLastKnownLocation();
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                curPos = new LatLng(latitude, longitude);
            } else {
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
        placeTitle.setVisibility(View.INVISIBLE);
        placeRate.setVisibility(View.INVISIBLE);
        placeDesc.setVisibility(View.INVISIBLE);
    }

    private List<Placemark> getPlacemarkList() {
        LinkedList<Placemark> placemarks = new LinkedList<>();
        Gson gson = new Gson();
        AssetManager assetManager = requireActivity().getResources().getAssets();
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
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1002);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1002) {

            if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(requireContext(), "허용을 해주셔야지만 앱이 정상적으로 실행이됩니다.", Toast.LENGTH_SHORT).show();
                setPermisson();
            }
            updateCameraToCurrentLocation();
        }
    }
}