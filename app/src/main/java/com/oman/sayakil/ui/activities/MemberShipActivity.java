package com.oman.sayakil.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.oman.sayakil.R;
import com.oman.sayakil.databinding.ItemMemberShipBinding;
import com.oman.sayakil.model.MemberModel;
import com.oman.sayakil.utils.Tools;

import java.util.ArrayList;
import java.util.List;



public class MemberShipActivity extends AppCompatActivity {

    private List<MemberModel> mList;
    private RecyclerView mRecyclerView;
    private ActionBar actionBar;
    Toolbar toolbar;

    public void initToolbar() {

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        Tools.setSystemBarColorInt(this, Color.parseColor("#0A7099"));


    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_ship);
        mRecyclerView = findViewById(R.id.rv_member_ship);
        memberList();
        startRecyclerView();
        initToolbar();
    }



    private void startRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(new MemberShipAdapter(this, mList));
    }

    private void memberList() {
        mList = new ArrayList<>();

        mList.add(new MemberModel("One Day Membership", 20, getString(R.string.one_day_memeber_ship)));
        mList.add(new MemberModel("Three Day Membership", 50, getString(R.string.three_day_memeber_ship)));
        mList.add(new MemberModel("Monthly Day Membership", 99, getString(R.string.monthly_memeber_ship)));
        mList.add(new MemberModel("Yearly Day Membership", 500, getString(R.string.yearly_memeber_ship)));
    }




    public static class MemberShipAdapter extends RecyclerView.Adapter<MemberShipAdapter.ViewHolder> {

        private final Context mContext;
        private final List<MemberModel> mList;

        public MemberShipAdapter(Context mContext, List<MemberModel> mList) {
            this.mContext = mContext;
            this.mList = mList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ItemMemberShipBinding.inflate(LayoutInflater.from(mContext), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


            holder.bindingItem.tvDays.setText(mList.get(position).getDays());
            holder.bindingItem.tvPrice.setText(String.valueOf(mList.get(position).getPrice()));
            holder.bindingItem.tvDesc.setText(mList.get(position).getDesc());


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, PaymentCardDetailsActivity.class);
                    intent.putExtra("price_key", mList.get(position).getPrice());
                    mContext.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            ItemMemberShipBinding bindingItem;
            public ViewHolder(@NonNull ItemMemberShipBinding binding) {
                super(binding.getRoot());
                bindingItem = binding;
            }
        }
    }
}