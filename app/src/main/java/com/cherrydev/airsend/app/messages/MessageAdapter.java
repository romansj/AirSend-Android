package com.cherrydev.airsend.app.messages;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.cherrydev.airsend.R;
import com.cherrydev.time.CommonTimeUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter<T extends IMessage> extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private OnClickListener<T> onClickListener;
    private List<T> items = new ArrayList<>();


    public MessageAdapter(OnClickListener<T> onClickListener) {
        this.onClickListener = onClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_message, parent, false);
        return new ViewHolder(itemView);
    }

    //https://stackoverflow.com/a/29862608/4673960
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int pos) {
        T item = getItem(pos);


        holder.textView.setText(item.getIP() + ", ID: " + item.getId());
        holder.textViewMessage.setText(item.getText());

        long itemDateTime = item.getDateTime();
        String formattedDateTimeString = CommonTimeUtils.Format.toFormattedDateTimeString(LocalDateTime.ofInstant(Instant.ofEpochMilli(itemDateTime), ZoneId.systemDefault()), true);
        holder.timeTv.setText(formattedDateTimeString);
        if (item.getStatus() == null) holder.statusView.setVisibility(View.GONE);
        else holder.statusView.setText(item.getStatus());

    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeTv, textView, textViewMessage, statusView;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_ip);
            textViewMessage = itemView.findViewById(R.id.tv_text);
            timeTv = itemView.findViewById(R.id.tv_time);
            statusView = itemView.findViewById(R.id.tv_status);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    T message = items.get(position);
                    onClickListener.onClick(message);
                }

            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    T message = items.get(position);
                    onClickListener.onLongClick(message);
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
//        ArrayList<MyMessage> oldItems = new ArrayList<>(items);
        this.items.clear();
        this.items = newItems;
//
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ChipDiffUtil(items, oldItems));
//        diffResult.dispatchUpdatesTo(this);

        notifyDataSetChanged();
    }


    // convenience method for getting data at click position
    public T getItem(int position) {
        return items.get(position);
    }

    public T getItemById(int id) {
        T item = null;
        for (T chip : items) {
            if (id == chip.getId()) {
                item = chip;
            }
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        return items.get(position).getId();
    }

    public List<T> getItems() {
        return items;
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public class ChipDiffUtil extends DiffUtil.Callback {

        private List<T> prevList;
        private List<T> newList;

        ChipDiffUtil(List<T> newList, List<T> oldList) {
            this.newList = newList;
            this.prevList = oldList;
        }

        @Override
        public int getOldListSize() {
            return prevList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldPos, int newPos) {
            return prevList.get(oldPos).getId() == newList.get(newPos).getId();
        }

        @Override
        public boolean areContentsTheSame(int oldPos, int newPos) {
            return prevList.get(oldPos).getId() == newList.get(newPos).getId();
        }
    }


}
