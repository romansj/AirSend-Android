package com.cherrydev.airsend.app.utils.mymodels;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.utils.DialogActionItemInterface;

import java.util.List;

public class SimpleListAdapter<T extends DialogActionItemInterface> extends RecyclerView.Adapter<SimpleListAdapter<T>.ViewHolder> {

    private OnClickListener onClickListener;
    private List<T> items;

    public SimpleListAdapter(List<T> data) {
        items = data;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_text, parent, false);
        return new ViewHolder(itemView);
    }

    //https://stackoverflow.com/a/29862608/4673960
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int pos) {
        T item = getItem(pos);

        holder.textView.setText(item.getText());
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv);


            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    T myMessage = items.get(position);
                    onClickListener.onClick(myMessage);
                }

            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    T myMessage = items.get(position);
                    onClickListener.onLongClick(myMessage);
                }
                return true;
            });
        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public void updateData(List<T> newItems) {
        this.items = newItems;

        notifyDataSetChanged();
    }


    // convenience method for getting data at click position
    public T getItem(int position) {
        return items.get(position);
    }


    public List<T> getItems() {
        return items;
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public interface OnClickListener<T> {
        void onClick(T id);

        void onLongClick(T id);
    }


}
