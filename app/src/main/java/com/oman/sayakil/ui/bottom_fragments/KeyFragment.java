package com.oman.sayakil.ui.bottom_fragments;

import android.os.Bundle;
import android.text.format.DateFormat;
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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.oman.sayakil.databinding.FragmentKeyBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class KeyFragment extends Fragment {

    private static final String TAG = "KeyFragment";
    private FragmentKeyBinding binding;
    private DocumentReference document;
    private static final String KEY_TRANSCATION_ID = "t_id";
    private static final String KEY_START_TIME = "s_time";
    private static final String KEY_END_TIME = "end_time";
    private static final String KEY_RETURN_TIME = "return_time";


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentKeyBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        createAccountOnFireStore();
        getDataFromFireStore();

        binding.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStartEndTime();
//                checkOut();
            }
        });
        binding.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> data = new HashMap<>();

                data.put(KEY_TRANSCATION_ID, "null");
                data.put(KEY_END_TIME, "null");
                data.put(KEY_RETURN_TIME, "null");
                data.put(KEY_START_TIME, "null");


                document.set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "successfully return cycle", Toast.LENGTH_SHORT).show();
                            binding.btnReturn.setVisibility(View.INVISIBLE);
                            binding.btnCheckout.setVisibility(View.INVISIBLE);
                            setReturnTime();
                        }
                    }
                });
            }
        });

        return view;
    }

    private void checkOut() {
        document.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    setStartEndTime();
                }

            }
        });
    }

    private void setStartEndTime() {
        HashMap<String, String> map = new HashMap<>();
        map.put(KEY_START_TIME, formateDate(Calendar.getInstance().getTime()));
        map.put(KEY_END_TIME, formateDate(nextHourTime()));
        document.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Start Rent cycle", Toast.LENGTH_SHORT).show();
                    binding.btnCheckout.setVisibility(View.GONE);
                    getStartEndTime();
                }
            }
        });
    }

    private void setReturnTime() {
        HashMap<String, String> map = new HashMap<>();
        map.put(KEY_RETURN_TIME, formateDate(Calendar.getInstance().getTime()));

        document.set(map, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "Start Rent cycle", Toast.LENGTH_SHORT).show();
                    binding.btnCheckout.setVisibility(View.GONE);
                    getReturnTime();

                }
            }
        });
    }

    private void getReturnTime() {
        document.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {

                    binding.tvKey.setText("Return Time : \n" + String.valueOf(task.getResult().get(KEY_RETURN_TIME))
                            + "\n Start Time: \n" + task.getResult().get(KEY_START_TIME)
                            + "\n End Time: \n" + task.getResult().get(KEY_END_TIME));
                    binding.btnReturn.setVisibility(View.INVISIBLE);
                    binding.btnCheckout.setVisibility(View.INVISIBLE);

                }
            }
        });
    }

    private void getStartEndTime() {
        document.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    String startTime = String.valueOf(task.getResult().get(KEY_START_TIME));
                    String endTime = String.valueOf(task.getResult().get(KEY_END_TIME));
                    if (startTime.equals("null") && endTime.equals("null")) {
                        setStartEndTime();
                    } else {
                        binding.tvKey.setText("Star Time: \n "+String.valueOf(task.getResult().get(KEY_START_TIME))
                                + "\n End Time: \n" + String.valueOf(task.getResult().get(KEY_END_TIME)));
                        binding.btnReturn.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }


    private String formateDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyy.MMMM.dd GGG hh:mm aaa");

        String dateString = formatter.format(date);
        return dateString;
    }

    private Date nextHourTime() {

        Date date = new Date();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, 1);
        return calendar.getTime();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void getDataFromFireStore() {
        document.get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();

                    if (result.exists()) {


                        if (!String.valueOf(result.get(KEY_TRANSCATION_ID)).equals("null")) {

                            if (!String.valueOf(result.get(KEY_START_TIME)).equals("null")){

                                if (!String.valueOf(result.get(KEY_RETURN_TIME)).equals("null")){

                                }

                                binding.tvKey.setText(" Start Time: \n" + task.getResult().get(KEY_START_TIME)
                                        + "\n End Time: \n" + task.getResult().get(KEY_END_TIME));                                binding.btnCheckout.setVisibility(View.INVISIBLE);
                                binding.btnReturn.setVisibility(View.VISIBLE);
                            }else {

                                binding.btnCheckout.setVisibility(View.VISIBLE);
                            }

                        } else {
                            binding.btnCheckout.setVisibility(View.INVISIBLE);
                            binding.btnReturn.setVisibility(View.INVISIBLE);
                        }


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