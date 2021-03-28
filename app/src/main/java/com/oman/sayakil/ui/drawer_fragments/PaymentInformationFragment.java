package com.oman.sayakil.ui.drawer_fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import com.oman.sayakil.databinding.FragmentPaymentInformationBinding;
import com.oman.sayakil.pref.SharedPreferencesManager;
import com.oman.sayakil.ui.activities.ProfileActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaymentInformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentInformationFragment extends Fragment {
    private static final String TAG = "PaymentInformationFragm";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String KEY_CARD_HOLDER_NAME = "card_holder_name";
    private static final String KEY_CARD_NUMBER = "card_number";
    private static final String KEY_EXPIRY_DATE = "exiry_date";
    private static final String KEY_SECURITY_CODE = "security_code";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentPaymentInformationBinding binding;
    private DocumentReference document;
    Calendar myCalendar = null;

    public PaymentInformationFragment() {
        // Required empty public constructor

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaymentInformationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentInformationFragment newInstance(String param1, String param2) {
        PaymentInformationFragment fragment = new PaymentInformationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPaymentInformationBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        createAccountOnFireStore();
        getDataFromFireStore();

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataOnFirstore();
            }
        });


        binding.cardNumberEditText.addTextChangedListener(new TextWatcher() {

            private static final int TOTAL_SYMBOLS = 19; // size of pattern 0000-0000-0000-0000
            private static final int TOTAL_DIGITS = 16; // max numbers of digits in pattern: 0000 x 4
            private static final int DIVIDER_MODULO = 5; // means divider position is every 5th symbol beginning with 1
            private static final int DIVIDER_POSITION = DIVIDER_MODULO - 1; // means divider position is every 4th symbol beginning with 0
            private static final char DIVIDER = '-';

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // noop
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // noop
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!isInputCorrect(s, TOTAL_SYMBOLS, DIVIDER_MODULO, DIVIDER)) {
                    s.replace(0, s.length(), buildCorrectString(getDigitArray(s, TOTAL_DIGITS), DIVIDER_POSITION, DIVIDER));
                }
            }

            private boolean isInputCorrect(Editable s, int totalSymbols, int dividerModulo, char divider) {
                boolean isCorrect = s.length() <= totalSymbols; // check size of entered string
                for (int i = 0; i < s.length(); i++) { // check that every element is right
                    if (i > 0 && (i + 1) % dividerModulo == 0) {
                        isCorrect &= divider == s.charAt(i);
                    } else {
                        isCorrect &= Character.isDigit(s.charAt(i));
                    }
                }
                return isCorrect;
            }

            private String buildCorrectString(char[] digits, int dividerPosition, char divider) {
                final StringBuilder formatted = new StringBuilder();

                for (int i = 0; i < digits.length; i++) {
                    if (digits[i] != 0) {
                        formatted.append(digits[i]);
                        if ((i > 0) && (i < (digits.length - 1)) && (((i + 1) % dividerPosition) == 0)) {
                            formatted.append(divider);
                        }
                    }
                }

                return formatted.toString();
            }

            private char[] getDigitArray(final Editable s, final int size) {
                char[] digits = new char[size];
                int index = 0;
                for (int i = 0; i < s.length() && index < size; i++) {
                    char current = s.charAt(i);
                    if (Character.isDigit(current)) {
                        digits[index] = current;
                        index++;
                    }
                }
                return digits;
            }
        });


        myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                updateLabel();
            }

        };

        binding.cardDateEditText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        return view;
    }

    private void updateLabel() {
        String myFormat = "MM/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        binding.cardDateEditText.setText(sdf.format(myCalendar.getTime()));
    }


//    @Override
//    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
//
//        inflater.inflate(R.menu.main_menu, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        Toast.makeText(getContext(), "click", Toast.LENGTH_SHORT).show();
//        if (item.getItemId() == R.id.menu_save){
//            saveDataOnFirstore();
//            Toast.makeText(getContext(), "click", Toast.LENGTH_SHORT).show();
//
//        }
//        return super.onOptionsItemSelected(item);
//    }

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

    private void saveDataOnFirstore() {

        HashMap<String, Object> data = new HashMap<>();

        data.put(KEY_CARD_HOLDER_NAME, binding.etCardHolderName.getText().toString());
        data.put(KEY_CARD_NUMBER, binding.cardNumberEditText.getText().toString());
        data.put(KEY_EXPIRY_DATE, binding.cardDateEditText.getText().toString());
        data.put(KEY_SECURITY_CODE, binding.cardCVCEditText.getText().toString());


        document.set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "successfully inserted data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getDataFromFireStore() {
        document.get(Source.DEFAULT).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
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
        binding.etCardHolderName.setText(String.valueOf(result.get(KEY_CARD_HOLDER_NAME)));
        binding.cardNumberEditText.setText(String.valueOf(result.get(KEY_CARD_NUMBER)));
        binding.cardDateEditText.setText(String.valueOf(result.get(KEY_EXPIRY_DATE)));
        binding.cardCVCEditText.setText(String.valueOf(result.get(KEY_SECURITY_CODE)));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}