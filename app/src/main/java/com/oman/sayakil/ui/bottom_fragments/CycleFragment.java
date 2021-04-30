package com.oman.sayakil.ui.bottom_fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.oman.sayakil.R;
import com.oman.sayakil.databinding.FragmentCycleBinding;
import com.oman.sayakil.databinding.ItemCycleBinding;
import com.oman.sayakil.model.CycleModel;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class CycleFragment extends Fragment implements PaymentResultListener {

    private static final String TAG = "CycleFragment";
    private FragmentCycleBinding binding;
    private List<CycleModel> mList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CycleAdapter mAdapter;
    private DocumentReference document;
    private static final String KEY_TRANSCATION_ID="t_id";

    int amount = 100;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCycleBinding.inflate(getLayoutInflater(), container, false);
        View view = binding.getRoot();

        initList();
        getListItems();
        startRecyclerView();
        createAccountOnFireStore();

//        if (Utils.isNetworkAvailable(getContext())) {
//            binding.tvNointernet.setVisibility(View.GONE);
//            db.collection("data").document("cycle_info")
//                    .get(Source.SERVER)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                        @Override
//                        public void onSuccess(DocumentSnapshot documentSnapshot) {
//
//                            if (documentSnapshot.exists()) {
//                                CycleModel cycleModel = documentSnapshot.toObject(CycleModel.class);
//
//                                mList.add(cycleModel);
//                                startRecyclerView();
//                            }
//                        }
//                    });
//        } else {
//            binding.tvNointernet.setVisibility(View.VISIBLE);
//        }


        return view;
    }

    private void getListItems() {
        db.collection("cycleinfo").get()
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
                            List<CycleModel> types = queryDocumentSnapshots.toObjects(CycleModel.class);

                            // Add all to your list
                            mList.addAll(types);
                            mAdapter.notifyDataSetChanged();
                            startRecyclerView();
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

    private void initList() {
        mList = new ArrayList<>();
    }

    private void startRecyclerView() {
        mAdapter = new CycleAdapter(getContext(), mList);
        binding.rvCycle.setLayoutManager(new GridLayoutManager(getContext(),2));
        binding.rvCycle.setAdapter(mAdapter);
    }

    private void createAccountOnFireStore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String email = null;
        String phoneNumber = null;


        if (currentUser != null) {
            email = currentUser.getEmail();
            phoneNumber = currentUser.getPhoneNumber();
        }
        if (email != null && !email.isEmpty()) {

            document = FirebaseFirestore.getInstance().collection(email).document(currentUser.getUid());
        } else {
            if (phoneNumber != null) {

                document = FirebaseFirestore.getInstance().collection(phoneNumber).document(currentUser.getUid());
            } else {
                Toast.makeText(getContext(), "Please Authenticate your self", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void saveDataOnFirstore(String transation) {

        HashMap<String, String> data = new HashMap<>();

        data.put(KEY_TRANSCATION_ID, transation);


        document.set(data, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), "successfully inserted transcation id", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void razorPay(int money, String title){
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_6lbQOCxNc8Or5W");
        checkout.setImage(R.mipmap.ic_launcher);

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("name", "Sayakil Studio");
            jsonObject.put("description", title);
            jsonObject.put("theme.color", "#0093DD");
            jsonObject.put("currency", "USD");
            jsonObject.put("amount", money);
            jsonObject.put("prefill.contact", "+968 9632 2522");
            jsonObject.put("prefill.email", "sayakilstudio@gmail.com");
            checkout.open(getActivity(), jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onPaymentSuccess(String s) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Payment ID");
        builder.setMessage(s);
        builder.show();
        saveDataOnFirstore(s);
    }

    @Override
    public void onPaymentError(int i, String s) {

        saveDataOnFirstore(s);
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
            holder.bindingCycle.tvPrice.setText(String.valueOf(cycleList.get(position).getPrice()));
            cycleList.get(position).getImage();
            Picasso.get().load(cycleList.get(position).getImage()).into(holder.bindingCycle.ivImage);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String title = cycleList.get(position).getTitle();
                    int price = cycleList.get(position).getPrice();
                    String image = cycleList.get(position).getImage();
                    String desc = cycleList.get(position).getDesc();
                    showCustomDialog(title, desc, price, image);
                    amount = Math.round(Float.parseFloat(String.valueOf(cycleList.get(position).getPrice()))*amount);

                }
            });

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

    private void showCustomDialog(String title, String desc, int price, String url) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_cycle_info);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ImageView image = dialog.findViewById(R.id.icon);
        TextView tvTitle = dialog.findViewById(R.id.title);
        TextView tvPrice = dialog.findViewById(R.id.price);
        TextView tvDesc = dialog.findViewById(R.id.content);

        Picasso.get().load(url).into(image);
        tvPrice.setText(String.valueOf(price));
        tvTitle.setText(title);
        tvDesc.setText(desc);



        dialog.findViewById(R.id.btn_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                razorPay(amount, title);
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}