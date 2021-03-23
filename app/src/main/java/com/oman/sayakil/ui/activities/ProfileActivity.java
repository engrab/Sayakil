package com.oman.sayakil.ui.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Toast;

import com.oman.sayakil.R;
import com.oman.sayakil.databinding.ActivityProfileBinding;
import com.oman.sayakil.pref.SharedPreferencesManager;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    Calendar myCalendar;
    ArrayAdapter<CharSequence> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.toolbar);

        getPref();

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
            setPref();
            Toast.makeText(this, "Save Information Successfully", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setPref() {
        SharedPreferencesManager.getInstance(this).setFirstName(binding.etFirstName.getText().toString());
        SharedPreferencesManager.getInstance(this).setLastName(binding.etLastName.getText().toString());
        SharedPreferencesManager.getInstance(this).setDateBirth(binding.etDateBirth.getText().toString());
        SharedPreferencesManager.getInstance(this).setPhoneNumber(binding.etPhoneNumber.getText().toString());
        SharedPreferencesManager.getInstance(this).setSecondPhoneNumber(binding.etSecondPhoneNumber.getText().toString());
        SharedPreferencesManager.getInstance(this).setEmail(binding.etEmail.getText().toString());
        SharedPreferencesManager.getInstance(this).setConformEmail(binding.etConformEmail.getText().toString());
        SharedPreferencesManager.getInstance(this).setL1(binding.etLine1.getText().toString());
        SharedPreferencesManager.getInstance(this).setL2(binding.etLine2.getText().toString());
        SharedPreferencesManager.getInstance(this).setZipcode(binding.etZip.getText().toString());
        SharedPreferencesManager.getInstance(this).setProvincce(binding.etProvince.getText().toString());
        SharedPreferencesManager.getInstance(this).setGender(binding.spGender.getSelectedItemPosition());

    }

    private void getPref() {
        binding.etFirstName.setText(SharedPreferencesManager.getInstance(this).getFirstName());
        binding.etLastName.setText(SharedPreferencesManager.getInstance(this).getLastName());
        binding.etDateBirth.setText(SharedPreferencesManager.getInstance(this).getDateBirth());
        binding.etPhoneNumber.setText(SharedPreferencesManager.getInstance(this).getPhoneNumber());
        binding.etSecondPhoneNumber.setText(SharedPreferencesManager.getInstance(this).getSecondPhoneNumber());
        binding.etEmail.setText(SharedPreferencesManager.getInstance(this).getEmail());
        binding.etConformEmail.setText(SharedPreferencesManager.getInstance(this).getConformEmail());
        binding.etLine1.setText(SharedPreferencesManager.getInstance(this).getL1());
        binding.etLine2.setText(SharedPreferencesManager.getInstance(this).getL2());
        binding.etZip.setText(SharedPreferencesManager.getInstance(this).getZipcode());
        binding.etProvince.setText(SharedPreferencesManager.getInstance(this).getProvince());
        binding.spGender.setSelection(SharedPreferencesManager.getInstance(this).getGender());
        }
}