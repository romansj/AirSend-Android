package com.cherrydev.airsend.app.settings.license;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cherrydev.airsend.R;

import java.util.List;

public class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.LicenseHolder> {


    private List<LicenseItem> list;

    public LicenseAdapter(List<LicenseItem> list) {
        this.list = list;
    }


    public static class LicenseHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView website;
        TextView licenseText;
        TextView licenseLink;

        public LicenseHolder(@NonNull View itemView) {
            super(itemView);
            licenseLink = itemView.findViewById(R.id.button_view_license);
            title = itemView.findViewById(R.id.title);
            website = itemView.findViewById(R.id.website);
            licenseText = itemView.findViewById(R.id.licenseText);
        }

    }

    public void setList(List<LicenseItem> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    private LicenseItem getItem(int adapterPosition) {
        return list.get(adapterPosition);
    }

    @NonNull
    @Override
    public LicenseHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_license, parent, false);
        return new LicenseHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LicenseHolder holder, int position) {
        holder.title.setText(list.get(position).getTitle());
        holder.website.setText(list.get(position).getWebsiteLink());
        holder.licenseText.setText(list.get(position).getLicenseText());
        holder.licenseLink.setText(list.get(position).getLicenseLink());
    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
