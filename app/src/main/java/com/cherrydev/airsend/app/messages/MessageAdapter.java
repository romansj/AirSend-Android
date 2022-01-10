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
import com.cherrydev.airsend.app.database.models.UserMessage;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private OnClickListener onClickListener;
    private List<UserMessage> items = new ArrayList<>();


    public MessageAdapter(OnClickListener onClickListener) {
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
        UserMessage item = getItem(pos);


        holder.textView.setText(item.getIP());
        holder.textViewMessage.setText(item.getText());
        holder.timeTv.setText(item.getDateTime());
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView timeTv, textView, textViewMessage;

        ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_ip);
            textViewMessage = itemView.findViewById(R.id.tv_text);
            timeTv = itemView.findViewById(R.id.tv_time);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    UserMessage UserMessage = items.get(position);
                    onClickListener.onClick(UserMessage);
                }

            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    UserMessage UserMessage = items.get(position);
                    onClickListener.onLongClick(UserMessage);
                }
                return true;
            });


        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public void updateData(List<UserMessage> newItems) {
//        ArrayList<MyMessage> oldItems = new ArrayList<>(items);
        this.items.clear();
        this.items = newItems;
//
//        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ChipDiffUtil(items, oldItems));
//        diffResult.dispatchUpdatesTo(this);

        notifyDataSetChanged();
    }


    // convenience method for getting data at click position
    public UserMessage getItem(int position) {
        return items.get(position);
    }

    public UserMessage getItemById(int id) {
        UserMessage item = null;
        for (UserMessage chip : items) {
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

    public List<UserMessage> getItems() {
        return items;
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public interface OnClickListener {
        void onClick(UserMessage UserMessage);

        void onLongClick(UserMessage UserMessage);
    }


    public class ChipDiffUtil extends DiffUtil.Callback {

        private List<UserMessage> prevList;
        private List<UserMessage> newList;

        ChipDiffUtil(List<UserMessage> newList, List<UserMessage> oldList) {
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
