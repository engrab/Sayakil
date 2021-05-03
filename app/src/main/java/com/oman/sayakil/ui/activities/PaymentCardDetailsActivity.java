package com.oman.sayakil.ui.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.oman.sayakil.R;
import com.oman.sayakil.dialoge.DialogPaymentSuccessFragment;
import com.oman.sayakil.utils.Tools;

import java.util.HashMap;
import java.util.Random;


public class PaymentCardDetailsActivity extends AppCompatActivity {
    private static final String TAG = "PaymentCardDetailsActiv";
    private TextView card_number;
    private TextView card_expire;
    private TextView card_cvv;
    private TextView card_name;
    private TextView tvPrice;

    private TextInputEditText et_card_number;
    private TextInputEditText et_expire;
    private TextInputEditText et_cvv;
    private TextInputEditText et_name;

    private Button pay;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference document;
    int price;
    private ActionBar actionBar;
    Toolbar toolbar;

    private static final String KEY_CARD_HOLDER_NAME = "card_holder_name";
    private static final String KEY_CARD_NUMBER = "card_number";
    private static final String KEY_EXPIRY_DATE = "exiry_date";
    private static final String KEY_SECURITY_CODE = "security_code";

    public void initToolbar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        Tools.setSystemBarColorInt(this, Color.parseColor("#0A7099"));


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_card_details);
        createAccountOnFireStore();
        getDataFromFireStore();
        initToolbar();
        price = getIntent().getIntExtra("price_key",10);

        tvPrice = findViewById(R.id.tv_price);
        tvPrice.setText(""+price+" OMR");
        card_number = findViewById(R.id.card_number);
        card_expire = findViewById(R.id.card_expire);
        card_cvv = findViewById(R.id.card_cvv);
        card_name = findViewById(R.id.card_name);

        et_card_number = findViewById(R.id.et_card_number);
        et_expire = findViewById(R.id.et_expire);
        et_cvv = findViewById(R.id.et_cvv);
        et_name = findViewById(R.id.et_name);
        pay = findViewById(R.id.btn_pay);

        et_card_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (charSequence.toString().trim().length() == 0) {
                    card_number.setText("**** **** **** ****");
                } else {
                    String number = Tools.insertPeriodically(charSequence.toString().trim(), " ", 4);
                    card_number.setText(number);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_expire.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (charSequence.toString().trim().length() == 0) {
                    card_expire.setText("MM/YY");
                } else {
                    String exp = Tools.insertPeriodically(charSequence.toString().trim(), "/", 2);
                    card_expire.setText(exp);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_cvv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (charSequence.toString().trim().length() == 0) {
                    card_cvv.setText("***");
                } else {
                    card_cvv.setText(charSequence.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
                if (charSequence.toString().trim().length() == 0) {
                    card_name.setText("Your Name");
                } else {
                    card_name.setText(charSequence.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String cardnumber = et_card_number.getText().toString().trim();
                String cvv = et_cvv.getText().toString().trim();
                String expire = et_expire.getText().toString().trim();
                String name = et_name.getText().toString().trim();

                if (cardnumber.length()==16 && cvv.length()==3 && expire.length()==4 && !name.isEmpty()){
                    saveDataOnFirstore(cardnumber, cvv, expire, name, price);

                }else {
                    Toast.makeText(PaymentCardDetailsActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }

    private void showDialogPaymentSuccess() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        DialogPaymentSuccessFragment newFragment = new DialogPaymentSuccessFragment(price);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, newFragment).addToBackStack(null).commit();
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

            document = FirebaseFirestore.getInstance().collection(email).document(currentUser.getUid()).collection("carddetail").document();
        } else {
            if (phoneNumber != null) {

                document = FirebaseFirestore.getInstance().collection(phoneNumber).document(currentUser.getUid()).collection("carddetail").document();
            } else {
                Toast.makeText(this, "Please Authenticate your self", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void saveDataOnFirstore(String cardnumber, String cvv, String expire, String name, int rent) {

        HashMap<String, Object> data = new HashMap<>();

        data.put("cardnumber", cardnumber);
        data.put("cardname", name);
        data.put("cvv", cvv);
        data.put("expire", expire);
        data.put("price", rent);


        document.set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Random r = new Random();
                    saveSaveTranscation(r.nextInt(1000 - 100) + 100);

                }
            }
        });
    }

    private void saveSaveTranscation(int transation) {

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = null;
        String phoneNumber = null;
        DocumentReference doc = null;

        if (currentUser != null) {
            email = currentUser.getEmail();
            phoneNumber = currentUser.getPhoneNumber();
        }
        if (email != null && !email.isEmpty()) {


            doc = FirebaseFirestore.getInstance().collection(email).document(currentUser.getUid()).collection("rent").document("cycle");
        } else {
            if (phoneNumber != null) {

                doc = FirebaseFirestore.getInstance().collection(phoneNumber).document(currentUser.getUid()).collection("rent").document("cycle");
            } else {
                Toast.makeText(this, "Please Authenticate your self", Toast.LENGTH_SHORT).show();
            }

        }

        HashMap<String, String> data = new HashMap<>();

        data.put("t_id", String.valueOf(transation));


        doc.set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    showDialogPaymentSuccess();

                }
            }
        });
    }


    private void getDataFromFireStore() {
        DocumentReference doc = null;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = null;
        String phoneNumber = null;


        if (currentUser != null) {
            email = currentUser.getEmail();
            phoneNumber = currentUser.getPhoneNumber();
        }
        if (email != null && !email.isEmpty()) {

            doc = FirebaseFirestore.getInstance().collection(email).document(currentUser.getUid());
        } else {
            if (phoneNumber != null) {

                doc = FirebaseFirestore.getInstance().collection(phoneNumber).document(currentUser.getUid());
            } else {
                Toast.makeText(this, "Please Authenticate your self", Toast.LENGTH_SHORT).show();
            }

        }

        doc.get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot result = task.getResult();
                    if (result.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + result.getData());
                        insertDataInView(result);


                    }
                }
            }
        });
    }

    private void insertDataInView(DocumentSnapshot result) {
        card_name.setText(String.valueOf(result.get(KEY_CARD_HOLDER_NAME)));
        et_name.setText(String.valueOf(result.get(KEY_CARD_HOLDER_NAME)));
        card_number.setText(String.valueOf(result.get(KEY_CARD_NUMBER)));
        et_card_number.setText(String.valueOf(result.get(KEY_CARD_NUMBER)));
        card_expire.setText(String.valueOf(result.get(KEY_EXPIRY_DATE)));
        et_expire.setText(String.valueOf(result.get(KEY_EXPIRY_DATE)));
        card_cvv.setText(String.valueOf(result.get(KEY_SECURITY_CODE)));
        et_cvv.setText(String.valueOf(result.get(KEY_SECURITY_CODE)));

    }

}