package com.akondi.homemarketserver.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.akondi.homemarketserver.R;
import com.akondi.homemarketserver.adapters.OrderDetailsAdapter;
import com.akondi.homemarketserver.common.Common;

public class OrderDetails extends AppCompatActivity {

    TextView order_id, order_phone, order_address, order_total, order_comment;
    String order_id_value = "";
    RecyclerView foodList;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        order_id = (TextView) findViewById(R.id.order_id);
        order_phone = (TextView) findViewById(R.id.order_phone);
        order_address = (TextView) findViewById(R.id.order_address);
        order_total = (TextView) findViewById(R.id.order_total);
        order_comment = (TextView) findViewById(R.id.order_comment);

        foodList = (RecyclerView) findViewById(R.id.foodsList);
        foodList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        foodList.setLayoutManager(layoutManager);

        if (getIntent() != null) {
            order_id_value = getIntent().getStringExtra("OrderId");
        }

        //Set value
        order_id.setText(order_id_value);
        order_phone.setText(Common.currentRequest.getPhone());
        order_total.setText(Common.currentRequest.getTotal());
        order_address.setText(Common.currentRequest.getAddress());
        order_comment.setText(Common.currentRequest.getComment());

        OrderDetailsAdapter adapter = new OrderDetailsAdapter(Common.currentRequest.getFoods());
        adapter.notifyDataSetChanged();
        foodList.setAdapter(adapter);

    }
}
