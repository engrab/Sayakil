package com.oman.sayakil.ui.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.oman.sayakil.databinding.ActivityBottomSheetMapBinding;

public class BottomSheetMapActivity extends AppCompatActivity {

    private ActivityBottomSheetMapBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBottomSheetMapBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
    }
}