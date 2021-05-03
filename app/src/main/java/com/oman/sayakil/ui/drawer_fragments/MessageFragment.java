package com.oman.sayakil.ui.drawer_fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.oman.sayakil.databinding.FragmentMessageBinding;
import com.oman.sayakil.databinding.ItemMessageBinding;
import com.oman.sayakil.model.MessageModel;

import java.util.ArrayList;
import java.util.List;


public class MessageFragment extends Fragment {

    private static final String TAG = "MessageFragment";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private MessageAdapter mAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private FragmentMessageBinding binding;
    private List<MessageModel> mList;

    public MessageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MessageFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MessageFragment newInstance(String param1, String param2) {
        MessageFragment fragment = new MessageFragment();
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
        binding = FragmentMessageBinding.inflate(getLayoutInflater(),container, false);
        View view = binding.getRoot();
        mList = new ArrayList<>();
        startRecyclerViewAdapter();
        // Read from the firestore database.
        getListItems();

        return view;
    }

    private void getListItems() {
        db.collection("notification").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            Log.d(TAG, "onSuccess: LIST EMPTY");
                            return;
                        } else {
                            // Convert the whole Query Snapshot to a list
                            // of objects directly! No need to fetch each
                            // document.
                            List<MessageModel> types = queryDocumentSnapshots.toObjects(MessageModel.class);

                            // Add all to your list
                            mList.addAll(types);
                            mAdapter.notifyDataSetChanged();
                            startRecyclerViewAdapter();
                            Log.d(TAG, "onSuccess: " + mList);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startRecyclerViewAdapter() {
        mAdapter = new MessageAdapter(getContext(), mList);
        binding.rvMessage.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMessage.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

        private Context context;
        private List<MessageModel> messageList;

        public MessageAdapter(Context context, List<MessageModel> messageList) {
            this.context = context;
            this.messageList = messageList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(ItemMessageBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

            holder.bindingmessage.tvMsg.setText(messageList.get(position).getNotify());
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public ItemMessageBinding bindingmessage;
            public ViewHolder(@NonNull ItemMessageBinding binding1) {
                super(binding1.getRoot());
                bindingmessage = binding1;
            }
        }
    }
}