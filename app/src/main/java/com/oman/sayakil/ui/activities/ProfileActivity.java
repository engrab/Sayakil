package com.oman.sayakil.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

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
import com.oman.sayakil.databinding.ActivityProfileBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_DOB = "dob";
    private static final String KEY_PHONE_NUMBER = "phone_number";
    private static final String KEY_PHONE_NUMBER2 = "phone_number2";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CONFORM_EMAIL = "conform_email";
    private static final String KEY_ADDRESS1 = "address";
    private static final String KEY_ADDRESS2 = "address2";
    private static final String KEY_ZIP_CODE = "zip_code";
    private static final String KEY_PROVINCE = "province";
    private static final String KEY_GENDER = "gender";

    private ActivityProfileBinding binding;
    Calendar myCalendar;
    ArrayAdapter<CharSequence> adapter;
    private DocumentReference document;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);

        createAccountOnFireStore();

        getDataFromFireStore();

        adapter = ArrayAdapter.createFromResource(
                this, R.array.gender_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spGender.setAdapter(adapter);



        myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };
        binding.etDateBirth.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(ProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

    }



    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        binding.etDateBirth.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==R.id.menu_save){
            saveDataOnFirstore();
        }
        return super.onOptionsItemSelected(item);
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
        }
        else {
            if (phoneNumber != null) {

                document = FirebaseFirestore.getInstance().collection(phoneNumber).document(currentUser.getUid());
            }
            else {
                Toast.makeText(this, "Please Authenticate your self", Toast.LENGTH_SHORT).show();
            }

        }
    }
    private void saveDataOnFirstore(){

        HashMap<String , Object> data = new HashMap<>();

        data.put(KEY_FIRST_NAME, binding.etFirstName.getText().toString());
        data.put(KEY_LAST_NAME, binding.etLastName.getText().toString());
        data.put(KEY_DOB, binding.etDateBirth.getText().toString());
        data.put(KEY_PHONE_NUMBER, binding.etPhoneNumber.getText().toString());
        data.put(KEY_PHONE_NUMBER2, binding.etSecondPhoneNumber.getText().toString());
        data.put(KEY_EMAIL, binding.etEmail.getText().toString());
        data.put(KEY_CONFORM_EMAIL, binding.etConformEmail.getText().toString());
        data.put(KEY_ADDRESS1, binding.etLine1.getText().toString());
        data.put(KEY_ADDRESS2, binding.etLine2.getText().toString());
        data.put(KEY_ZIP_CODE, binding.etZip.getText().toString());
        data.put(KEY_PROVINCE, binding.etProvince.getText().toString());
        data.put(KEY_GENDER, binding.spGender.getSelectedItemPosition());


        document.set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ProfileActivity.this, "successfully inserted data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void getDataFromFireStore(){
        document.get(Source.DEFAULT).addOnCompleteListener(this,new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()){
                    DocumentSnapshot result = task.getResult();
                    if (result.exists()){
                        Log.d(TAG, "DocumentSnapshot data: " + result.getData());
                        insertDataInView(result);


                    }
                }

            }
        });
    }

    private void insertDataInView(DocumentSnapshot result) {
        binding.etFirstName.setText(String.valueOf(result.get(KEY_FIRST_NAME)));
        binding.etLastName.setText(String.valueOf(result.get(KEY_LAST_NAME)));
        binding.etDateBirth.setText(String.valueOf(result.get(KEY_DOB)));
        binding.etPhoneNumber.setText(String.valueOf(result.get(KEY_PHONE_NUMBER)));
        binding.etSecondPhoneNumber.setText(String.valueOf(result.get(KEY_PHONE_NUMBER2)));
        binding.etEmail.setText(String.valueOf(result.get(KEY_EMAIL)));
        binding.etConformEmail.setText(String.valueOf(result.get(KEY_CONFORM_EMAIL)));
        binding.etLine1.setText(String.valueOf(result.get(KEY_ADDRESS1)));
        binding.etLine2.setText(String.valueOf(result.get(KEY_ADDRESS2)));
        binding.etZip.setText(String.valueOf(result.get(KEY_ZIP_CODE)));
        binding.etProvince.setText(String.valueOf(result.get(KEY_PROVINCE)));

    }



//    private void setPref() {
//        SharedPreferencesManager.getInstance(this).setFirstName(binding.etFirstName.getText().toString());
//        SharedPreferencesManager.getInstance(this).setLastName(binding.etLastName.getText().toString());
//        SharedPreferencesManager.getInstance(this).setDateBirth(binding.etDateBirth.getText().toString());
//        SharedPreferencesManager.getInstance(this).setPhoneNumber(binding.etPhoneNumber.getText().toString());
//        SharedPreferencesManager.getInstance(this).setSecondPhoneNumber(binding.etSecondPhoneNumber.getText().toString());
//        SharedPreferencesManager.getInstance(this).setEmail(binding.etEmail.getText().toString());
//        SharedPreferencesManager.getInstance(this).setConformEmail(binding.etConformEmail.getText().toString());
//        SharedPreferencesManager.getInstance(this).setL1(binding.etLine1.getText().toString());
//        SharedPreferencesManager.getInstance(this).setL2(binding.etLine2.getText().toString());
//        SharedPreferencesManager.getInstance(this).setZipcode(binding.etZip.getText().toString());
//        SharedPreferencesManager.getInstance(this).setProvincce(binding.etProvince.getText().toString());
//        SharedPreferencesManager.getInstance(this).setGender(binding.spGender.getSelectedItemPosition());
//
//    }

//    private void getPref() {
//        binding.etFirstName.setText(SharedPreferencesManager.getInstance(this).getFirstName());
//        binding.etLastName.setText(SharedPreferencesManager.getInstance(this).getLastName());
//        binding.etDateBirth.setText(SharedPreferencesManager.getInstance(this).getDateBirth());
//        binding.etPhoneNumber.setText(SharedPreferencesManager.getInstance(this).getPhoneNumber());
//        binding.etSecondPhoneNumber.setText(SharedPreferencesManager.getInstance(this).getSecondPhoneNumber());
//        binding.etEmail.setText(SharedPreferencesManager.getInstance(this).getEmail());
//        binding.etConformEmail.setText(SharedPreferencesManager.getInstance(this).getConformEmail());
//        binding.etLine1.setText(SharedPreferencesManager.getInstance(this).getL1());
//        binding.etLine2.setText(SharedPreferencesManager.getInstance(this).getL2());
//        binding.etZip.setText(SharedPreferencesManager.getInstance(this).getZipcode());
//        binding.etProvince.setText(SharedPreferencesManager.getInstance(this).getProvince());
////        binding.spGender.setSelection(SharedPreferencesManager.getInstance(this).getGender());
//        }
}