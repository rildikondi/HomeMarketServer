package com.akondi.homemarketserver.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.akondi.homemarketserver.R;
import com.akondi.homemarketserver.common.Common;
import com.akondi.homemarketserver.interfaces.ItemClickListener;

public class OrderViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnCreateContextMenuListener {

    public TextView txtOrderId, txtOrderStatus, txtOrderPhone, txtOrderAddress;
    private ItemClickListener itemClickListener;
    public Button btnEdit, btnRemove, btnDetails, btnDirection;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);

        txtOrderId = itemView.findViewById(R.id.order_id);
        txtOrderStatus = itemView.findViewById(R.id.order_status);
        txtOrderPhone = itemView.findViewById(R.id.order_phone);
        txtOrderAddress = itemView.findViewById(R.id.order_address);

        btnEdit = itemView.findViewById(R.id.btnEdit);
        btnRemove = itemView.findViewById(R.id.btnRemove);
        btnDetails = itemView.findViewById(R.id.btnDetails);
        btnDirection = itemView.findViewById(R.id.btnDirection);

//        itemView.setOnClickListener(this);
//        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        //this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        //itemClickListener.onClick(v, getAdapterPosition(), false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        menu.setHeaderTitle("Select the action");
//        menu.add(0, 0, getAdapterPosition(), Common.UPDATE);
//        menu.add(0, 1, getAdapterPosition(), Common.DELETE);
//        menu.add(0, 2, getAdapterPosition(), Common.MORE_DETAILS);
    }
}
