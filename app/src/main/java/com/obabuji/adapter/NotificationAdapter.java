package com.obabuji.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.obabuji.R;
import com.obabuji.model.ItemNotification;

import java.util.List;

/**
 * Created by user on 08-06-2017.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private Context context;
    private final List<ItemNotification> allItems;



    public NotificationAdapter(Context context, List<ItemNotification> items) {
        allItems = items;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        public final TextView tv_message,tv_date;


        public ViewHolder(View view) {
            super(view);
            this.mView = view;
            this.tv_message = (TextView) view.findViewById(R.id.tv_message);
            this.tv_date = (TextView) view.findViewById(R.id.tv_date);

            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ItemNotification item = allItems.get(getAdapterPosition());
                }
            });

        }
    }



    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout. item_notification, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {

        ItemNotification item = allItems.get(holder.getAdapterPosition());
        holder.tv_message.setText(item.getMessage());
        holder.tv_date.setText(item.getDate());

    }

    public int getItemCount() {
        return this.allItems.size();
    }
}