package com.oman.sayakil.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.oman.sayakil.R;
import com.oman.sayakil.databinding.ItemCycleBinding;
import com.oman.sayakil.databinding.ItemSheetMapBinding;
import com.oman.sayakil.model.CycleModel;
import com.oman.sayakil.model.StoreInfo;
import com.oman.sayakil.ui.bottom_fragments.CycleFragment;
import com.oman.sayakil.utils.Tools;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetMapActivity extends AppCompatActivity {

    private static final String TAG = "BottomSheetMapActivity";
    private static GoogleMap mMap;
    private static BottomSheetBehavior bottomSheetBehavior;
    private StoreAdapter mAdapter;
    private RecyclerView recyclerView;
    private List<StoreInfo> mList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bottom_sheet_map);
        recyclerView = findViewById(R.id.rv_sheetmap);
        initList();
        initMapFragment();
        initComponent();
        startRecyclerView();
        Toast.makeText(this, "Swipe up bottom sheet", Toast.LENGTH_SHORT).show();
    }

    private void initList() {
        mList = new ArrayList<>();

        db.collection("storelocation").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            // Convert the whole Query Snapshot to a list
                            // of objects directly! No need to fetch each
                            // document.
                            List<StoreInfo> types = queryDocumentSnapshots.toObjects(StoreInfo.class);

                            // Add all to your list
                            mList.addAll(types);
                            mAdapter.notifyDataSetChanged();
                            Log.d(TAG, "onSuccess: " + mList);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void initComponent() {
        // get the bottom sheet view
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);

        // init the bottom sheet behavior
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);

        // change the state of the bottom sheet
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        // set callback for changes
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        findViewById(R.id.fab_directions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                try {
                    mMap.animateCamera(zoomingLocation());
                } catch (Exception e) {
                }
            }
        });
    }

    private void initMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = Tools.configActivityMaps(googleMap);
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(37.7610237, -122.4217785));
                mMap.addMarker(markerOptions);
                mMap.moveCamera(zoomingLocation());
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        try {
                            mMap.animateCamera(zoomingLocation());
                        } catch (Exception e) {
                        }
                        return true;
                    }
                });
            }
        });
    }

    private CameraUpdate zoomingLocation() {
        return CameraUpdateFactory.newLatLngZoom(new LatLng(37.76496792, -122.42206407), 13);
    }
    private static void storeLocation(String lat, String lng){

        MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
        mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)),15));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                try {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)),18));
                } catch (Exception e) {
                }
                return true;
            }
        });
    }



    private void startRecyclerView() {
        mAdapter = new StoreAdapter(getApplicationContext(), mList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(mAdapter);
    }

    private static class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

        private Context context;
        private List<StoreInfo> cycleList;

        public StoreAdapter(Context context, List<StoreInfo> cycleList) {
            this.context = context;
            this.cycleList = cycleList;
        }

        @NonNull
        @Override
        public StoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new StoreAdapter.ViewHolder(ItemSheetMapBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull StoreAdapter.ViewHolder holder, int position) {

            holder.bindingCycle.tvAddressTitle.setText(cycleList.get(position).getName());
            holder.bindingCycle.tvAddressDetail.setText(String.valueOf(cycleList.get(position).getAddress()));
            holder.bindingCycle.tvPhone.setText(String.valueOf(cycleList.get(position).getPhone()));
            holder.bindingCycle.tvOpenTime.setText(String.valueOf(cycleList.get(position).getTime()));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Item Click", Toast.LENGTH_SHORT).show();
                    String lat = cycleList.get(position).getLat();
                    String lng = cycleList.get(position).getLng();
                    storeLocation(lat,lng);


                }
            });

        }

        @Override
        public int getItemCount() {
            return cycleList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ItemSheetMapBinding bindingCycle;

            public ViewHolder(@NonNull ItemSheetMapBinding binding) {
                super(binding.getRoot());
                bindingCycle = binding;
            }

        }

        public interface getGeoLocation {
            void geoLocationClick(String lat, String lng);
        }
    }

}