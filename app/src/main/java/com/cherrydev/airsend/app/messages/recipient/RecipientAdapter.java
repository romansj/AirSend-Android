package com.cherrydev.airsend.app.messages.recipient;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.database.models.Device;

import java.util.ArrayList;
import java.util.List;

public class RecipientAdapter extends RecyclerView.Adapter<RecipientAdapter.ViewHolder> {

    OnClickListener onClickListener;
    private List<Device> items;


    private List<String> selectedItems = new ArrayList<>();


    // data is passed into the constructor
    public RecipientAdapter(List<Device> data) {
        items = data;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_selection_wicon_two_lines, parent, false);
        return new ViewHolder(itemView);
    }

    //https://stackoverflow.com/a/29862608/4673960
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        Device item = getItem(pos);

        holder.textView.setText(item.getName());
        holder.textView2.setText(item.getIP() + " : " + item.getPort() + " | " + item.getClientRunning());
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView, textView2;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);


            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    Device device = items.get(position);
                    onClickListener.onClick(device);
                }

            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Device device = items.get(position);
                    onClickListener.onLongClick(device);
                }
                return true;
            });
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public void updateData(List<Device> newItems) {
        this.items = newItems;

        notifyDataSetChanged();
    }


    // convenience method for getting data at click position
    public Device getItem(int position) {
        return items.get(position);
    }


    public List<Device> getItems() {
        return items;
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public interface OnClickListener {
        void onClick(Device params);

        void onLongClick(Device params);
    }


}
