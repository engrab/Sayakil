package com.oman.sayakil.ui.bottom_fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;
import com.oman.sayakil.Utils;
import com.oman.sayakil.databinding.FragmentCycleBinding;
import com.oman.sayakil.databinding.ItemCycleBinding;
import com.oman.sayakil.model.CycleModel;

import java.util.ArrayList;
import java.util.List;

public class CycleFragment extends Fragment {

    private static final String TAG = "CycleFragment";
    private FragmentCycleBinding binding;
    private List<CycleModel> mList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCycleBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        initList();



        if (Utils.isNetworkAvailable(getContext())){
            binding.tvNointernet.setVisibility(View.GONE);
            db.collection("data").document("cycle_info")
                    .get(Source.SERVER)
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot.exists()){
                                CycleModel cycleModel = documentSnapshot.toObject(CycleModel.class);

                                mList.add(cycleModel);
                                startRecyclerView();
                            }
                        }
                    });
        }
        else {
            binding.tvNointernet.setVisibility(View.VISIBLE);
        }
        return view;
    }

    private void initList() {
        mList = new ArrayList<>();
    }

    private void startRecyclerView() {
        binding.rvCycle.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvCycle.setAdapter(new CycleAdapter(getContext(), mList));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private class CycleAdapter extends RecyclerView.Adapter<CycleAdapter.ViewHolder> {

        private Context context;
        private List<CycleModel> cycleList;

        public CycleAdapter(Context context, List<CycleModel> cycleList) {
            this.context = context;
            this.cycleList = cycleList;
        }

        @NonNull
        @Override
        public CycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ItemCycleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull CycleAdapter.ViewHolder holder, int position) {

            holder.bindingCycle.tvTitle.setText(cycleList.get(position).getTitle());
        }

        @Override
        public int getItemCount() {
            return cycleList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ItemCycleBinding bindingCycle;

            public ViewHolder(@NonNull ItemCycleBinding binding) {
                super(binding.getRoot());
                bindingCycle = binding;
            }
        }
    }

}