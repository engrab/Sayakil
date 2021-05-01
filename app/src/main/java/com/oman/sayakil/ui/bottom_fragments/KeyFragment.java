package com.oman.sayakil.ui.bottom_fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
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
import com.oman.sayakil.R;
import com.oman.sayakil.databinding.FragmentKeyBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class KeyFragment extends Fragment {

    private static final String TAG = "KeyFragment";
    private FragmentKeyBinding binding;
    private DocumentReference document;
    private static final String KEY_TRANSCATION_ID = "t_id";
    private static final String KEY_START_TIME = "s_time";
    private static final String KEY_END_TIME = "end_time";
    private static final String KEY_RETURN_TIME = "return_time";
    private static final String KEY_BIKE_TYPE = "bike_type";
    private static final String KEY_AREA = "area";
    private static final String KEY_USER_NAME = "user_name";


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

            }
        });
        binding.btnReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInfoDialoge();


            }
        });

        return view;
    }

    private void showInfoDialoge() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_return_cycle);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        EditText bike = dialog.findViewById(R.id.type);
        EditText area = dialog.findViewById(R.id.area);
        EditText name = dialog.findViewById(R.id.user);


        dialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bikeinfo = bike.getText().toString().trim();
                String areainfo = area.getText().toString().trim();
                String username = name.getText().toString().trim();


                if (bikeinfo.isEmpty() && areainfo.isEmpty() && username.trim().isEmpty()) {
                    Toast.makeText(getContext(), "Please fill all field...", Toast.LENGTH_SHORT).show();
                } else {
                    resetCycleInfo(areainfo,bikeinfo,username);
                    dialog.dismiss();
                }

            }
        });
        dialog.findViewById(R.id.btn_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void resetCycleInfo(String area, String bike, String username) {

        HashMap<String, Object> data = new HashMap<>();

        data.put(KEY_TRANSCATION_ID, "null");
        data.put(KEY_END_TIME, "null");
        data.put(KEY_RETURN_TIME, "null");
        data.put(KEY_START_TIME, "null");
        data.put(KEY_AREA, area);
        data.put(KEY_USER_NAME, username);
        data.put(KEY_BIKE_TYPE, bike);


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

                    binding.tvKey.setText("Transcation ID: \n "+task.getResult().get(KEY_TRANSCATION_ID)+"\n Return Time : \n" + String.valueOf(task.getResult().get(KEY_RETURN_TIME))
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
                        binding.tvKey.setText("Transcation ID: \n "+task.getResult().get(KEY_TRANSCATION_ID)+"\nStar Time: \n " + String.valueOf(task.getResult().get(KEY_START_TIME))
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

                            binding.tvKey.setText(task.getResult().getString(KEY_TRANSCATION_ID));


                            if (!String.valueOf(result.get(KEY_START_TIME)).equals("null")) {
                                binding.tvKey.setText(task.getResult().getString(KEY_TRANSCATION_ID)+"\n"+task.getResult().getString(KEY_START_TIME));

                                if (!String.valueOf(result.get(KEY_RETURN_TIME)).equals("null")) {
                                    binding.tvKey.setText(task.getResult().getString(KEY_TRANSCATION_ID)+"\n"+task.getResult().getString(KEY_START_TIME)+"\n"
                                    +task.getResult().getString(KEY_RETURN_TIME));

                                }

                                binding.btnCheckout.setVisibility(View.INVISIBLE);
                                binding.btnReturn.setVisibility(View.VISIBLE);
                            } else {

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

            document = FirebaseFirestore.getInstance().collection(email).document(currentUser.getUid()).collection("rent").document("cycle");
        } else {
            if (phoneNumber != null) {

                document = FirebaseFirestore.getInstance().collection(phoneNumber).document(currentUser.getUid()).collection("rent").document("cycle");
            } else {
                Toast.makeText(getContext(), "Please Authenticate your self", Toast.LENGTH_SHORT).show();
            }

        }
    }
}