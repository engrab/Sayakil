package com.oman.sayakil.ui.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.oman.sayakil.R;
import com.oman.sayakil.adapter.AdapterChatBBM;
import com.oman.sayakil.model.CycleModel;
import com.oman.sayakil.model.Message;
import com.oman.sayakil.utils.Tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedBackActivity extends AppCompatActivity {
    private static final String TAG = "FeedBackActivity";
    private View btn_send;
    private EditText et_content;
    private AdapterChatBBM adapter;
    private RecyclerView recycler_view;
    private DocumentReference document;
    private List<Message> mList = new ArrayList<>();
    private ActionBar actionBar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);
        initToolbar();
        iniComponent();
        createAccountOnFireStore();
        getListItems();
    }

    // for toolbar navigation / backpress.
//    @Override
//    public boolean onSupportNavigateUp() {
//        onBackPressed();
//        return true;
//    }

    public void initToolbar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        Tools.setSystemBarColorInt(this, Color.parseColor("#0A7099"));


    }

    public void iniComponent() {
        recycler_view = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setHasFixedSize(true);

        adapter = new AdapterChatBBM(this, mList);
        recycler_view.setAdapter(adapter);

        btn_send = findViewById(R.id.btn_send);
        et_content = findViewById(R.id.text_content);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendChat();
            }
        });
        et_content.addTextChangedListener(contentWatcher);
    }

    private void createAccountOnFireStore() {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = null;
        String phoneNumber = null;


        if (currentUser != null) {
            email = currentUser.getEmail();
            phoneNumber = currentUser.getPhoneNumber();
            toolbar.setTitle(currentUser.getDisplayName());
        }
        if (email != null && !email.isEmpty()) {

            document = FirebaseFirestore.getInstance().collection("feedback").document(currentUser.getUid());
        } else {
            if (phoneNumber != null) {

                document = FirebaseFirestore.getInstance().collection("feedback").document(currentUser.getUid());
            } else {
                Toast.makeText(this, "Please Authenticate your self", Toast.LENGTH_SHORT).show();
            }

        }
    }


    private void getListItems() {
        document.collection("message").get()
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
                            List<Message> types = queryDocumentSnapshots.toObjects(Message.class);

                            // Add all to your list
                            mList.addAll(types);
                            adapter.notifyDataSetChanged();

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FeedBackActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendChat() {
        final String msg = et_content.getText().toString();
        adapter.insertItem(new Message(msg, true, Tools.getFormattedTimeEvent(System.currentTimeMillis())));
        insertItems(msg, true, Tools.getFormattedTimeEvent(System.currentTimeMillis()));
        et_content.setText("");
        recycler_view.scrollToPosition(adapter.getItemCount() - 1);

        if (et_content.length() == 0) {
            btn_send.setEnabled(false);
        }
    }

    private void insertItems(String message, boolean isFromMe, String date) {
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("content", message);
        user.put("fromMe", isFromMe);
        user.put("date", date);

        // Add a new document with a generated ID
        document.collection("message").document(mList.size() + "").set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(FeedBackActivity.this, "Send", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private TextWatcher contentWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable etd) {
            if (etd.toString().trim().length() == 0) {
                btn_send.setEnabled(false);
            } else {
                btn_send.setEnabled(true);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };


}