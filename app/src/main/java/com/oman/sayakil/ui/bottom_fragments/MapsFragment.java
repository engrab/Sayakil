package com.oman.sayakil.ui.bottom_fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.oman.sayakil.R;
import com.oman.sayakil.databinding.FragmentMapsBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.oman.sayakil.databinding.ItemSheetMapBinding;
import com.oman.sayakil.model.LocationModel;
import com.oman.sayakil.model.StoreInfo;
import com.oman.sayakil.ui.activities.BottomSheetMapActivity;
import com.oman.sayakil.utils.Tools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.LOCATION_SERVICE;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class MapsFragment extends Fragment {
    private static final String TAG = "MapsFragment";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<StoreInfo> mList;

    public static final int DEFAULT_ZOOM = 10;
    private final double OMAN_LAT = 19.966245355307795;
    private final double OMAN_LNG = 56.28402662723767;


    private static GoogleMap mMap;
    private static BottomSheetBehavior bottomSheetBehavior;
    private StoreAdapter mAdapter;
    private RecyclerView recyclerView;


    LinearLayout llBottomSheet;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        view.findViewById(R.id.fab_directions).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                try {
                    mMap.animateCamera(zoomingLocation());
                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    mMap.setMyLocationEnabled(true);

                } catch (Exception e) {
                }
            }
        });
        llBottomSheet = view.findViewById(R.id.bottom_sheet);

        recyclerView = view.findViewById(R.id.rv_sheetmap);
        initList();

        initMapFragment();
        initComponent(view);
        startRecyclerView();
        Toast.makeText(getContext(), "Swipe up bottom sheet", Toast.LENGTH_SHORT).show();
        return view;
    }



//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        SupportMapFragment mapFragment =
//                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
//        if (mapFragment != null) {
//            initMapFragment();
//            mapFragment.getMapAsync(this);
//        }
//    }





    private static class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

        private final Context context;
        private final List<StoreInfo> cycleList;

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
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void initComponent(View view) {
        // get the bottom sheet view


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


    }

    private void initMapFragment() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                MarkerOptions markerOptions = new MarkerOptions().position(new LatLng(OMAN_LAT, OMAN_LNG));
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
        return CameraUpdateFactory.newLatLngZoom(new LatLng(OMAN_LAT, OMAN_LNG), 13);
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
        mAdapter = new StoreAdapter(getContext(), mList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mAdapter);
    }

}