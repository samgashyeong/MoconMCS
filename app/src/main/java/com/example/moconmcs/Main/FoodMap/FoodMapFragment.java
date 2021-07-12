package com.example.moconmcs.Main.FoodMap;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

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

public class FoodMapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    private GoogleMap googleMap;
    private MapView mapView;
    private static List<Placemark> placemarkList;
    public static Placemark searchedPlacemark;
    private final List<Marker> markers = new LinkedList<>();
    private Marker selectedMarker;
    private TextView placeTitle, placeDesc;
    private RatingBar placeRate;
    private ConstraintLayout titleWrap;
    private SlidingUpPanelLayout slideLayout;
    private Button writeReviewBtn;
    private FloatingActionButton moveToMyLoc;

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
        titleWrap = view.findViewById(R.id.titleWrap);
        slideLayout = view.findViewById(R.id.sliding_up_panel);
        reviewLoading = view.findViewById(R.id.map_review_loading);
        writeReviewBtn = view.findViewById(R.id.write_review_btn);
        moveToMyLoc = view.findViewById(R.id.moveToMyPos);
        writeReviewBtn.setVisibility(View.INVISIBLE);
        slideLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

        slideLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) { }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                            SlidingUpPanelLayout.PanelState newState) {
                if(newState == SlidingUpPanelLayout.PanelState.HIDDEN
                        || newState == SlidingUpPanelLayout.PanelState.COLLAPSED
                        || (previousState == SlidingUpPanelLayout.PanelState.HIDDEN && newState == SlidingUpPanelLayout.PanelState.DRAGGING)
                )
                    moveToMyLoc.show();
                else
                    moveToMyLoc.hide();
            }
        });

        moveToMyLoc.setOnClickListener(v -> {
            LatLng curPos = null;
            Location location = getLastKnownLocation();
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                curPos = new LatLng(latitude, longitude);
            }
            if (ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            if(curPos != null) googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, 12));
        });

        recyclerView = view.findViewById(R.id.reviews);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        adapter = new ReviewAdapter(arrayList);
        recyclerView.setAdapter(adapter);

        writeReviewBtn.setOnClickListener(v -> openReviewWriteDialog());

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

        markers.add(googleMap.addMarker(markerOptions));
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
        slideLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        if(selectedMarker != null) {
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
           if(userTask.isSuccessful() && selectedMarker.getTitle() != null) {
               DocumentReference docRef = db.collection("Place").document(selectedMarker.getTitle());
               docRef.get().addOnCompleteListener(task -> {
                   reviewLoading.setVisibility(View.GONE);
                   if(task.isSuccessful()) {
                       DocumentSnapshot placeDoc = task.getResult();
                       if(placeDoc.getData() == null) {
                           Map<String, Object> dataset = new HashMap<>();
                           Map<String, ReviewInfo> reviewers = new HashMap<>();
                           dataset.put("reviewers", reviewers);
                           docRef.set(dataset);
                       }
                       else {
                           docRef.update("reviewers." + uid,
                                   new FirebaseReview(rate, content, Calendar.getInstance().getTimeInMillis()));
                           updateReviewList(selectedMarker.getTitle());
                       }
                   }
               });
           }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 1235 && searchedPlacemark != null) {
            for(Marker marker : markers) {
                if(Objects.requireNonNull(marker.getTitle())
                        .equalsIgnoreCase(searchedPlacemark.getName())) {
                    onMarkerClick(marker);
                    break;
                }
            }
            searchedPlacemark = null;
        }
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        CameraUpdate center = CameraUpdateFactory.newLatLng(marker.getPosition());
        googleMap.animateCamera(center);

        changeSelectedMarker(marker);

        titleWrap.setVisibility(View.VISIBLE);
        placeTitle.setVisibility(View.VISIBLE);
        placeRate.setVisibility(View.VISIBLE);
        placeDesc.setVisibility(View.VISIBLE);
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
        adapter.notifyDataSetChanged();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        DocumentReference docRef = db.collection("Place").document(title);
        CollectionReference userRef = db.collection("User");
        userRef.get().addOnCompleteListener(userTask -> {
            reviewLoading.setVisibility(View.GONE);
            if(userTask.isSuccessful()) {
                HashMap<String, String> nameMap = new HashMap<>();
                for(DocumentSnapshot documentSnapshot : userTask.getResult().getDocuments()) {
                    nameMap.put(documentSnapshot.getId(), documentSnapshot.getString("name"));
                }
                docRef.get().addOnCompleteListener(task -> {
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
                                for(Map.Entry<String, HashMap<String, Object>> entry : reviews.entrySet()) {
                                    HashMap<String, Object> rv = entry.getValue();
                                    String rvUid = entry.getKey();
                                    float rate = Float.parseFloat(rv.get("rate") + "");
                                    String reviewText = rv.get("review") + "";
                                    String name = nameMap.get(rvUid); //어댑터 선에서 name이 null이면 탈퇴한 유저라고 뜨게 함
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
        googleMap.setOnMapClickListener(this);
        if (placemarkList == null) placemarkList = getPlacemarkList(requireActivity());
        addMarkers();

        LatLng curPos = new LatLng(37.56, 126.97);
        if (getActivity() != null) {
            Location location = getLastKnownLocation();
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                curPos = new LatLng(latitude, longitude);
            }
            if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            selectedMarker = null;
            placeTitle.setVisibility(View.INVISIBLE);
            placeRate.setVisibility(View.INVISIBLE);
            placeDesc.setVisibility(View.INVISIBLE);
            googleMap.setMinZoomPreference(8);

            if(searchedPlacemark != null) {
                for(Marker marker : markers) {
                    if(Objects.requireNonNull(marker.getTitle())
                            .equalsIgnoreCase(searchedPlacemark.getName())) {
                        onMarkerClick(marker);
                        slideLayout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
                        break;
                    }
                }
                searchedPlacemark = null;
            }
            else
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, 12));
        }
    }

    private static List<Placemark> getPlacemarkList(Activity activity) {
        LinkedList<Placemark> placemarks = new LinkedList<>();
        Gson gson = new Gson();
        AssetManager assetManager = activity.getResources().getAssets();
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
                Toast.makeText(requireContext(), "위치 권한을 허용해주셔야지만 앱이 정상적으로 실행됩니다.\n수동으로 허용해주세요.", Toast.LENGTH_SHORT).show();
                Context context = requireActivity().getApplicationContext();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + context.getPackageName()));
                startActivity(intent);
                requireActivity().finish();
            }
            updateCameraToCurrentLocation();
        }
    }

    public static List<Placemark> searchPlace(Activity activity, String keyword) {
        LinkedList<Placemark> result = new LinkedList<>();
        if(placemarkList == null) placemarkList = getPlacemarkList(activity);
        placemarkList.forEach(placemark -> {
            if(placemark.getName().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(placemark);
            }
        });
        return result;
    }

    @Override
    public void onMapClick(@NonNull @NotNull LatLng latLng) {
        changeSelectedMarker(null);
        if(slideLayout != null) {
            slideLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        }
    }
}