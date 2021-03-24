package com.oman.sayakil.ui.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.oman.sayakil.R;
import com.oman.sayakil.databinding.FragmentMemberShipBinding;
import com.oman.sayakil.databinding.ItemMemberShipBinding;
import com.oman.sayakil.model.MemberModel;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class MemberShipFragment extends Fragment {

    public static final String GOOGLE_PAY_PACKAGE_NAME = "com.google.android.apps.nbu.paisa.user";
    int GOOGLE_PAY_REQUEST_CODE = 123;
    String amount ="10";
    String name = "Highbrow Director";
    String upiId = "hashimads123@oksbi";
    String transactionNote = "pay test";
    String status;
    Uri uri;

    private List<MemberModel> mList;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentMemberShipBinding binding;

    public MemberShipFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    private static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getApplicationInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private static Uri getUpiPaymentUri(String name, String upiId, String transactionNote, String amount) {
        return new Uri.Builder()
                .scheme("upi")
                .authority("pay")
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", transactionNote)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();
    }
    private void payWithGPay() {
        if (isAppInstalled(getContext(), GOOGLE_PAY_PACKAGE_NAME)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(uri);
            intent.setPackage(GOOGLE_PAY_PACKAGE_NAME);
            startActivityForResult(intent, GOOGLE_PAY_REQUEST_CODE);
        } else {
            Toast.makeText(getContext(), "Please Install GPay", Toast.LENGTH_SHORT).show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            status = data.getStringExtra("Status").toLowerCase();
        }

        if ((RESULT_OK == resultCode) && status.equals("success")) {
            Toast.makeText(getContext(), "Transaction Successful", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Transaction Failed", Toast.LENGTH_SHORT).show();
        }
    }


    public class MemberShipAdapter extends RecyclerView.Adapter<MemberShipAdapter.ViewHolder> {

        private Context mContext;
        private List<MemberModel> mList;

        public MemberShipAdapter(Context mContext, List<MemberModel> mList) {
            this.mContext = mContext;
            this.mList = mList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ItemMemberShipBinding.inflate(LayoutInflater.from(mContext),parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


            holder.bindingItem.tvDays.setText(mList.get(position).getDays());
            holder.bindingItem.tvPrice.setText(mList.get(position).getPrice());
            holder.bindingItem.tvDesc.setText(mList.get(position).getDesc());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uri = getUpiPaymentUri(name, upiId, transactionNote, amount);
                    payWithGPay();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ItemMemberShipBinding bindingItem;
            public ViewHolder(@NonNull ItemMemberShipBinding binding) {
                super(binding.getRoot());
                bindingItem = binding;
            }
        }
    }
}