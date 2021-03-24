package com.oman.sayakil.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.oman.sayakil.databinding.ItemMemberShipBinding;
import com.oman.sayakil.model.MemberModel;

import java.util.List;

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

        holder.binding.tvTitle.setText(mList.get(position).getDaysMember());
        holder.binding.tvPrice.setText(mList.get(position).getMemberPrice());
        holder.binding.tvDesc.setText(mList.get(position).getMemberDesc());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                call transcation fragment
                Toast.makeText(mContext, "Item Click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ItemMemberShipBinding binding;
        public ViewHolder(@NonNull ItemMemberShipBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
