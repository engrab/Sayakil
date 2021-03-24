package com.oman.sayakil.ui.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oman.sayakil.R;
import com.oman.sayakil.adapters.MemberShipAdapter;
import com.oman.sayakil.databinding.FragmentMemberShipBinding;
import com.oman.sayakil.model.MemberModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MemberShipFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MemberShipFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private List<MemberModel> mList;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentMemberShipBinding binding;

    public MemberShipFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MemberShipFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MemberShipFragment newInstance(String param1, String param2) {
        MemberShipFragment fragment = new MemberShipFragment();
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
        binding = FragmentMemberShipBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();
        memberList();
        startRecyclerView();
        return view;
    }

    private void startRecyclerView() {
        binding.rvMemberShip.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMemberShip.setAdapter(new MemberShipAdapter(getContext(), mList));
    }

    private void memberList() {
        mList = new ArrayList<>();

        mList.add(new MemberModel("One Day Membership", "AED 20", getString(R.string.one_day_memeber_ship)));
        mList.add(new MemberModel("Three Day Membership", "AED 50", getString(R.string.three_day_memeber_ship)));
        mList.add(new MemberModel("Monthly Day Membership", "AED 99", getString(R.string.monthly_memeber_ship)));
        mList.add(new MemberModel("Yearly Day Membership", "AED 500", getString(R.string.yearly_memeber_ship)));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}