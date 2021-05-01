package com.oman.sayakil.ui.activities;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.oman.sayakil.R;
import com.oman.sayakil.utils.Tools;
import com.razorpay.PaymentResultListener;

import java.util.HashMap;


public class PaymentCardDetailsActivity extends AppCompatActivity {
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_card_details);
        createAccountOnFireStore();

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
                    Toast.makeText(PaymentCardDetailsActivity.this, "pay successfully", Toast.LENGTH_SHORT).show();
                    saveDataOnFirstore(cardnumber, cvv, expire, name, price);
                }else {
                    Toast.makeText(PaymentCardDetailsActivity.this, "fill all field", Toast.LENGTH_SHORT).show();

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
                    Toast.makeText(PaymentCardDetailsActivity.this, "successfully", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


}