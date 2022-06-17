package com.cherrydev.airsend.app.messages;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {

    private OnClickListener onClickListener;
    private List<String> items;


    private final ArrayList<String> selectedItems = new ArrayList<>();


    // data is passed into the constructor
    public RvAdapter(List<String> data) {
        items = data;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(com.cherrydev.dialogs.R.layout.row_text, parent, false);
        return new ViewHolder(itemView);
    }

    //https://stackoverflow.com/a/29862608/4673960
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RvAdapter.ViewHolder holder, int pos) {
        String item = getItem(pos);

        holder.textView.setText(item);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(com.cherrydev.dialogs.R.id.tv);
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public void updateData(ArrayList<String> newItems) {
        this.items = newItems;

        notifyDataSetChanged();
    }


    // convenience method for getting data at click position
    public String getItem(int position) {
        return items.get(position);
    }


    public List<String> getItems() {
        return items;
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public interface OnClickListener {
        void onClick(int id);

        void onLongClick(int id);
    }


}
