package com.oman.sayakil.ui.drawer_fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.oman.sayakil.R;
import com.oman.sayakil.databinding.FragmentPaymentInformationBinding;
import com.oman.sayakil.pref.SharedPreferencesManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PaymentInformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaymentInformationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentPaymentInformationBinding binding;

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
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
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
        readInfo();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_save){
            Toast.makeText(getContext(), "Save Information", Toast.LENGTH_SHORT).show();
            writInfo();
        }
        return true;
    }

    private void writInfo() {
        SharedPreferencesManager.getInstance(getContext()).setCardHolderName(binding.etCardHolderName.getText().toString());
        SharedPreferencesManager.getInstance(getContext()).setFirstName(binding.etCardHolderName.getText().toString());
        SharedPreferencesManager.getInstance(getContext()).setCreditCard(binding.etCardNumber.getText().toString());
        SharedPreferencesManager.getInstance(getContext()).setSecurityCode(binding.etSecurityCode.getText().toString());
        SharedPreferencesManager.getInstance(getContext()).setExpiryDate(binding.etExpiryDate.getText().toString());
    }

    private void readInfo(){
        binding.etCardHolderName.setText(SharedPreferencesManager.getInstance(getContext()).getCardHolderName());
        binding.etCardNumber.setText(SharedPreferencesManager.getInstance(getContext()).getCreditCard());
        binding.etExpiryDate.setText(SharedPreferencesManager.getInstance(getContext()).getExpiryDate());
        binding.etSecurityCode.setText(SharedPreferencesManager.getInstance(getContext()).getSecurityCode());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}