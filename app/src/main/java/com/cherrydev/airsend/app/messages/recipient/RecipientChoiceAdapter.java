package com.cherrydev.airsend.app.messages.recipient;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.cherrydev.airsend.R;
import com.cherrydev.airsend.app.database.models.Device;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

public class RecipientChoiceAdapter extends RecyclerView.Adapter<RecipientChoiceAdapter.ViewHolder> {

    private List<Device> devicesList = new ArrayList<>();
    private List<Device> selectedDevices = new ArrayList<>();


    public RecipientChoiceAdapter(List<Device> devicesList, List<Device> checkedDevices) {
        this.devicesList.addAll(devicesList);
        this.selectedDevices.addAll(checkedDevices);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_device_selection, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            if (payloads.get(0) instanceof Boolean) {
                Timber.i("onBindViewHolder w payload " + (boolean) payloads.get(0));
                holder.checkBox.setChecked((Boolean) payloads.get(0));
            }

        } else {
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Device device = getItem(position);
        holder.textView.setText(device.getName());
        holder.tvDescription.setText(device.getIP());
        holder.checkBox.setChecked(containsDeviceWithID(device) != -1);
    }

    public void updateData(List<Device> data) {
        devicesList.clear();
        if (data != null) {
            devicesList.addAll(data);
        }
        notifyDataSetChanged();
    }

    @Nullable
    public Device getItem(int index) {
        return devicesList.get(index);
    }


    @Override
    public int getItemCount() {
        return devicesList.size();
    }

//    @Override
//    public long getItemId(int position) {
//        return getItem(position).getId();
//    }

    Device getItemById(String id) {
        Device toReturn = null;
        for (Device device : devicesList) {
            if (Objects.equals(id, device.getIP())) {
                toReturn = device;
            }
        }
        return toReturn;
    }


    public void clearList() {
        devicesList.clear();
    }

    public List<Device> getCheckedItems() {
        return selectedDevices;
    }

    public void setCheckedItems(List<Device> checkedDevices) {
        this.selectedDevices.clear();
        this.selectedDevices.addAll(checkedDevices);
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private TextView textView, tvDescription;

        ViewHolder(View itemView) {

            super(itemView);

            checkBox = itemView.findViewById(R.id.checkbox);
            textView = itemView.findViewById(R.id.textView);
            tvDescription = itemView.findViewById(R.id.tv_description);


            itemView.setOnClickListener(v -> selectItem(getAdapterPosition()));
        }
    }

    //returns index
    private int containsDeviceWithID(Device device) {
        int index = -1;

        for (Device checkedDevice : selectedDevices) {
            if (checkedDevice.getIP().equals(device.getIP())) {
                index = selectedDevices.indexOf(checkedDevice);
                break;
            }
        }

        return index;
    }

    private void selectItem(int position) {
        Device device = getItem(position);
        if (device == null) return;

        int index = containsDeviceWithID(device);

        boolean contains = index != -1;

        if (contains) {
            selectedDevices.remove(device);
        } else {
            selectedDevices.add(device);
        }

        notifyItemChanged(position, !contains);
    }

    public interface ItemClickListener {
        void onCheckedChanged(boolean isChecked, int position);
    }
}
