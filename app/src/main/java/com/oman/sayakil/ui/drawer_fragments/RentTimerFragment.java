package com.oman.sayakil.ui.drawer_fragments;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oman.sayakil.R;
import com.oman.sayakil.databinding.FragmentRentTimerBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RentTimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RentTimerFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentRentTimerBinding binding;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private long startTime = 0L;
    private Handler customHandler = new Handler();



    public RentTimerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RentTimerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RentTimerFragment newInstance(String param1, String param2) {
        RentTimerFragment fragment = new RentTimerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        binding = FragmentRentTimerBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        binding.resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                binding.resetButton.setClickable(false);
                binding.resetButton.setTextColor(Color.parseColor("#8e8e8e"));
                binding.timerValue.setText(String.format("%02d", 00) + ":"
                        + String.format("%02d", 00) + ":"
                        + String.format("%03d", 000));
                startTime = SystemClock.uptimeMillis();
                timeSwapBuff = 0;
            }
        });
        binding.startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                binding.startButton.setClickable(false);
                binding.startButton.setTextColor(Color.parseColor("#a6a6a6"));
                binding.resetButton.setClickable(false);
                binding.resetButton.setTextColor(Color.parseColor("#a6a6a6"));
                binding.stopButton.setClickable(true);
                binding.stopButton.setTextColor(Color.parseColor("#000000"));
                startTime = SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread, 0);
            }
        });
        binding.stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                binding.startButton.setClickable(true);
                binding.startButton.setTextColor(Color.parseColor("#000000"));
                binding.stopButton.setClickable(false);
                binding.stopButton.setTextColor(Color.parseColor("#a6a6a6"));
                binding.resetButton.setClickable(true);
                binding.resetButton.setTextColor(Color.parseColor("#000000"));
                timeSwapBuff += timeInMilliseconds;
                customHandler.removeCallbacks(updateTimerThread);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }


    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            int seconds = (int) (updatedTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (updatedTime % 1000);

            String string = "";
            string += "" + String.format("%02d", minutes);
            string += ":" + String.format("%02d", seconds);
            string += ":" + String.format("%03d", milliseconds);

            binding.timerValue.setText(string);
            customHandler.postDelayed(this, 0);
        }
    };
}