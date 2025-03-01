package com.example.appointmentbooker.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.appointmentbooker.Models.ServiceType;
import com.example.appointmentbooker.R;

import java.util.List;

public class CustomSpinnerAdatper extends ArrayAdapter<ServiceType> {
    private Context mContext;
    private List<ServiceType> mItems;

    public CustomSpinnerAdatper(@NonNull Context context, @NonNull List<ServiceType> items) {
        super(context, R.layout.spinner_item, items);
        this.mContext = context;
        this.mItems = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }

        ServiceType item = mItems.get(position);
        TextView textView = convertView.findViewById(R.id.spinnerItemText);
        textView.setText(item.getName());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }

        ServiceType item = mItems.get(position);
        TextView textView = convertView.findViewById(R.id.spinnerItemText);
        textView.setText(item.getName());

        return convertView;
    }
}

