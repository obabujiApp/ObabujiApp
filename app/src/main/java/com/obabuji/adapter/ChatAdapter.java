package com.obabuji.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.obabuji.R;
import com.obabuji.model.ChatItem;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by user on 10-06-2017.
 */

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {
    private Context context;
    private final List<ChatItem> allItems;


    public class ViewHolder extends RecyclerView.ViewHolder {

        private final View mView;
        private final TextView tv_text_message_left;
        private final CardView layout_left;
        private final TextView time_left;

        private final TextView tv_text_message_right;
        private final CardView layout_right;
        private final TextView time_right;

        public ViewHolder(View view) {
            super(view);
            this.mView = view;
            this.tv_text_message_left = (TextView) view.findViewById(R.id.tv_text_message_left);
            this.time_left = (TextView) view.findViewById(R.id.time_left);
            this.layout_left = (CardView) view.findViewById(R.id.layout_left);

            this.tv_text_message_right = (TextView) view.findViewById(R.id.tv_text_message_right);
            this.time_right = (TextView) view.findViewById(R.id.time_right);
            this.layout_right = (CardView) view.findViewById(R.id.layout_right);


        }
    }

    public ChatAdapter(Context context, List<ChatItem> items) {
        allItems = items;
        this.context = context;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_detail_row_item, parent, false));
    }

    public void onBindViewHolder(ViewHolder holder, int position) {

        final ChatItem item = allItems.get(holder.getAdapterPosition());

        //2017-06-10 15:21:00
        String datetime=item.getDatetime();
        try{
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(datetime);

            datetime = new SimpleDateFormat("dd MMM yyyy HH:mm a").format(date);

        }catch (ParseException pe){
            pe.printStackTrace();
        }

         if(item.getLeftOrRight()==0){
             holder.layout_right.setVisibility(View.VISIBLE);
             holder.layout_left.setVisibility(View.GONE);
             holder.time_right.setText(datetime);
             holder.tv_text_message_right.setVisibility(View.VISIBLE);
             holder.tv_text_message_right.setText(item.getMessage());
        }else{
             holder.layout_right.setVisibility(View.GONE);
             holder.layout_left.setVisibility(View.VISIBLE);
             holder.time_left.setText(datetime);
             holder.tv_text_message_left.setVisibility(View.VISIBLE);
             holder.tv_text_message_left.setText(item.getMessage());
         }
        

    }

    public int getItemCount() {
        return this.allItems.size();
    }
}