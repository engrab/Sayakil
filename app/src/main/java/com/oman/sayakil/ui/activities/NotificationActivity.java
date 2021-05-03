package com.oman.sayakil.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.oman.sayakil.R;
import com.oman.sayakil.databinding.ActivityNotificationBinding;
import com.oman.sayakil.databinding.ItemNotificationBinding;
import com.oman.sayakil.model.NotificationModel;
import com.oman.sayakil.utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NotificationActivity extends AppCompatActivity {

    private static final String TAG = "MessageFragment";

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private ActivityNotificationBinding binding;
    private List<NotificationModel> mList;
    private NotificationAdapter mAdapter;
    private ActionBar actionBar;
    Toolbar toolbar;
    private String title = "";
    private String body = "";

    public void initToolbar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        Tools.setSystemBarColorInt(this, Color.parseColor("#0A7099"));


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
           title = bundle.get("title").toString();
           body = bundle.get("body").toString();
            setNotificationOnFirebase(title, body);

        }
        initToolbar();
        mList = new ArrayList<>();
        startRecyclerViewAdapter();
        // Read from the firestore database.
        getListItems();
    }

    private void setNotificationOnFirebase(String title, String body){

        Log.d(TAG, "setNotificationOnFirebase: "+title+" "+body);
        Map<String, Object> data = new HashMap<>();
        data.put("title", title);
        data.put("body", body);

        db.collection("notification").document().set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }


    private void getListItems() {
        db.collection("notification").get()
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
                            List<NotificationModel> types = queryDocumentSnapshots.toObjects(NotificationModel.class);

                            // Add all to your list
                            mList.addAll(types);
                            mAdapter.notifyDataSetChanged();
                            startRecyclerViewAdapter();
                            Log.d(TAG, "onSuccess: " + mList);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NotificationActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startRecyclerViewAdapter() {
        mAdapter = new NotificationAdapter(NotificationActivity.this, mList);
        binding.rvMessage.setLayoutManager(new LinearLayoutManager(NotificationActivity.this));
        binding.rvMessage.setAdapter(mAdapter);
    }


    private static class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

        private final Context context;
        private List<NotificationModel> messageList;

        public NotificationAdapter(Context context, List<NotificationModel> messageList) {
            this.context = context;
            this.messageList = messageList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ItemNotificationBinding.inflate(LayoutInflater.from(context), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.bindingmessage.tvTitle.setText(messageList.get(position).getTitle());
            holder.bindingmessage.tvBody.setText(messageList.get(position).getBody());
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public ItemNotificationBinding bindingmessage;

            public ViewHolder(@NonNull ItemNotificationBinding binding1) {
                super(binding1.getRoot());
                bindingmessage = binding1;
            }
        }
    }
}