package com.oman.sayakil.ui.bottom_fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.oman.sayakil.databinding.FragmentCycleBinding;

public class CycleFragment extends Fragment {

private FragmentCycleBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCycleBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}