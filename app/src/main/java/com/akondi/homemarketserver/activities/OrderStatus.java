package com.akondi.homemarketserver.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.akondi.homemarketserver.R;
import com.akondi.homemarketserver.common.Common;
import com.akondi.homemarketserver.interfaces.ItemClickListener;
import com.akondi.homemarketserver.model.MyResponse;
import com.akondi.homemarketserver.model.Notification;
import com.akondi.homemarketserver.model.Request;
import com.akondi.homemarketserver.model.Sender;
import com.akondi.homemarketserver.model.Token;
import com.akondi.homemarketserver.remote.APIService;
import com.akondi.homemarketserver.viewholder.OrderViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Request, OrderViewHolder> mFirebaseAdapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    Spinner statusSpinner;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        setupFirebase();
        initService();
        setupRecyclerView();
        loadOrders(Common.currentUser.getPhone());
    }

    private void initService() {
        mService = Common.getFCMService();
    }

    private void setupFirebase() {
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void loadOrders(String phone) {
        FirebaseRecyclerOptions<Request> options =
                new FirebaseRecyclerOptions.Builder<Request>()
                        .setQuery(requests/*.orderByChild("phone")*/, Request.class)
                        .build();

        mFirebaseAdapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @NonNull
            @Override
            public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout.order_layout, viewGroup, false);

                return new OrderViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder holder, final int position, @NonNull final Request model) {
                holder.txtOrderId.setText(mFirebaseAdapter.getRef(position).getKey());
                holder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                holder.txtOrderAddress.setText(model.getAddress());
                holder.txtOrderPhone.setText(model.getPhone());
//                holder.setItemClickListener(new ItemClickListener() {
//                    @Override
//                    public void onClick(View view, int position, boolean isLongClick) {
//                        Intent trackingOrder = new Intent(OrderStatus.this, TrackingOrder.class);
//                        Common.currentRequest = model;
//                        startActivity(trackingOrder);
//                    }
//                });
                holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialog(mFirebaseAdapter.getRef(position).getKey(), mFirebaseAdapter.getItem(position));
                    }
                });

                holder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(mFirebaseAdapter.getRef(position).getKey());
                    }
                });

                holder.btnDetails.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        goToDetails(position, model);
                    }
                });
                holder.btnDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent trackingOrder = new Intent(OrderStatus.this, TrackingOrder.class);
                        Common.currentRequest = model;
                        startActivity(trackingOrder);
                    }
                });
            }
        };
        mFirebaseAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(mFirebaseAdapter);
    }

    private void goToDetails(int position, Request model) {
        Intent orderDetail = new Intent(OrderStatus.this, OrderDetails.class);
        Common.currentRequest = model;
        orderDetail.putExtra("OrderId", mFirebaseAdapter.getRef(position).getKey());
        startActivity(orderDetail);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
//        if (item.getTitle().equals(Common.UPDATE))
//            showUpdateDialog(mFirebaseAdapter.getRef(item.getOrder()).getKey(), mFirebaseAdapter.getItem(item.getOrder()));
//        else if (item.getTitle().equals(Common.DELETE)) {
//            deleteOrder(mFirebaseAdapter.getRef(item.getOrder()).getKey());
//        } else if (item.getTitle().equals(Common.MORE_DETAILS)) {
//            Intent orderDetail = new Intent(OrderStatus.this, OrderDetails.class);
//            Common.currentRequest = mFirebaseAdapter.getItem(item.getOrder());
//            orderDetail.putExtra("OrderId", mFirebaseAdapter.getRef(item.getOrder()).getKey());
//            startActivity(orderDetail);
//        }
        return super.onContextItemSelected(item);
    }

    private void deleteOrder(String key) {
        requests.child(key).removeValue();
        mFirebaseAdapter.notifyDataSetChanged();
    }

    private void showUpdateDialog(String key, final Request item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout, null, false);

        statusSpinner = (Spinner) view.findViewById(R.id.statusSpinner);
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.status_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        statusSpinner.setAdapter(adapter);
        statusSpinner.setOnItemSelectedListener(this);

        alertDialog.setView(view);

        final String localKey = key;

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                item.setStatus(String.valueOf(statusSpinner.getSelectedItemPosition()));
                requests.child(localKey).setValue(item);
                adapter.notifyDataSetChanged(); //Add to update item size
                sendOrderStatusToUser(localKey, item);
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void sendOrderStatusToUser(final String localKey, Request item) {
        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                            Token token = postSnapShot.getValue(Token.class);

                            // Make raw payload
                            Notification notification = new Notification("AK Dev", "Your order: " + localKey + "was updated");
                            Sender content = new Sender(token.getToken(), notification);

                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if (response.body().success == 1) {
                                                Toast.makeText(OrderStatus.this, "Order was updated !", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(OrderStatus.this, "Order was updated but failed to send notification !", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("Error", t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseAdapter.stopListening();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
