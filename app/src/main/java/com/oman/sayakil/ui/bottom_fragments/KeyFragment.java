package com.oman.sayakil.ui.bottom_fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;
import com.oman.sayakil.databinding.FragmentKeyBinding;

public class KeyFragment extends Fragment {

    private static final String TAG = "KeyFragment";
    private FragmentKeyBinding binding;
    private DocumentReference document;
    private static final String KEY_TRANSCATION_ID="t_id";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentKeyBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        createAccountOnFireStore();
        getDataFromFireStore();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
    private void getDataFromFireStore(){
        document.get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    DocumentSnapshot result = task.getResult();
                    if (result.exists()){
                        Log.d(TAG, "DocumentSnapshot data: " + result.getData());
                        binding.tvKey.setText(String.valueOf(result.get(KEY_TRANSCATION_ID)));


                    }
                }
            }
        });
    }

    private void createAccountOnFireStore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = null;
        String phoneNumber = null;


        if (currentUser != null) {
            email = currentUser.getEmail();
            phoneNumber = currentUser.getPhoneNumber();
        }
        if (email != null && !email.isEmpty()) {

            document = FirebaseFirestore.getInstance().collection(email).document(currentUser.getUid());
        } else {
            if (phoneNumber != null) {

                document = FirebaseFirestore.getInstance().collection(phoneNumber).document(currentUser.getUid());
            } else {
                Toast.makeText(getContext(), "Please Authenticate your self", Toast.LENGTH_SHORT).show();
            }

        }
    }
}