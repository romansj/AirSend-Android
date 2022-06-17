package com.cherrydev.airsend.app.connections;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.database.models.Device;
import com.cherrydev.airsendcore.core.Status;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

public class AdapterDevice extends RecyclerView.Adapter<AdapterDevice.ViewHolder> {

    private OnClickListener onClickListener;
    private List<DeviceWrapper> items = new ArrayList<>();


    public AdapterDevice(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_device, parent, false);
        return new ViewHolder(itemView);
    }

    //https://stackoverflow.com/a/29862608/4673960
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull AdapterDevice.ViewHolder holder, int pos) {
        DeviceWrapper item = getItem(pos);


        holder.tvName.setText(item.getDevice().getName());
        holder.tvIP.setText(item.getDevice().getIP() + ":" + item.getDevice().getPort());
        holder.imgDeviceType.setImageDrawable(
                item.getDevice().getDeviceType().equals("W") ?
                        holder.itemView.getContext().getDrawable(R.drawable.computer) :
                        holder.itemView.getContext().getDrawable(R.drawable.smartphone));

        boolean isRunning = item.getDevice().getClientRunning() == Status.RUNNING;
        holder.btnConnectDisconnect.setText(isRunning ? "Disconnect" : "Connect");
        holder.btnConnectDisconnect.setIcon(holder.btnConnectDisconnect.getContext().getDrawable(isRunning ? R.drawable.link_off : R.drawable.link_plus));
        holder.tvConnectionType.setText(item.getDevice().getDeviceType());
        holder.tvServerRunning.setText("Server: " + item.getDevice().getServerRunning() + ", client: " + item.getDevice().getClientRunning());
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvIP, tvName, tvConnectionType, tvServerRunning;
        MaterialButton btnConnectDisconnect, btnDelete, btnReconnect;
        SwitchMaterial toggleClipboardSharing;
        ImageView imgDeviceType;

        ViewHolder(View itemView) {
            super(itemView);
            tvIP = itemView.findViewById(R.id.tv_ip);
            tvName = itemView.findViewById(R.id.tv_name);
            tvConnectionType = itemView.findViewById(R.id.tv_connection_type);
            tvServerRunning = itemView.findViewById(R.id.tv_server_running);
            btnConnectDisconnect = itemView.findViewById(R.id.btn_connect_disconnect);
            //btnReconnect = itemView.findViewById(R.id.btn_reconnect);
            btnDelete = itemView.findViewById(R.id.btn_delete);
            toggleClipboardSharing = itemView.findViewById(R.id.toggle_clipboard_sharing);
            imgDeviceType = itemView.findViewById(R.id.image_view);


            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;

                Device myMessage = items.get(position).getDevice();
                onClickListener.onClick(myMessage, ClickItem.CLICK);
            });

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Device device = items.get(position).getDevice();
                    onClickListener.onClick(device, ClickItem.LONG_CLICK);
                }
                return true;
            });


            btnConnectDisconnect.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;
                onClickListener.onClick(items.get(position).getDevice(), ClickItem.CONNECT_DISCONNECT);
            });


            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position == RecyclerView.NO_POSITION) return;
                onClickListener.onClick(items.get(position).getDevice(), ClickItem.DELETE);
            });

        }
    }


    @Override
    public int getItemCount() {
        return items.size();
    }


    public void updateData(List<DeviceWrapper> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }


    // convenience method for getting data at click position
    public DeviceWrapper getItem(int position) {
        return items.get(position);
    }

    public DeviceWrapper getItemByIP(String IP) {
        DeviceWrapper item = null;
        for (DeviceWrapper chip : items) {
            if (IP.equals(chip.getDevice().getIP())) {
                item = chip;
            }
        }
        return item;
    }

    public List<DeviceWrapper> getItems() {
        return items;
    }


    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


    public interface OnClickListener {
        void onClick(Device connectionItem, ClickItem clickItem);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    public enum ClickItem {
        CLICK, LONG_CLICK, CONNECT_DISCONNECT, DELETE
    }

}
