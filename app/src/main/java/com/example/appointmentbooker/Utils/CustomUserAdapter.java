package com.example.appointmentbooker.Utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.appointmentbooker.Models.Appointment;
import com.example.appointmentbooker.Models.ServiceType;
import com.example.appointmentbooker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CustomUserAdapter extends ArrayAdapter<Appointment> implements View.OnClickListener {

    private ArrayList<Appointment> dataSet;
    Context mContext;
    Dialog dialog;
    DatabaseHelper db;
    private static class ViewHolder {
        TextView txtDate, txtTime, txtPeriod, btnBook;
    }

    public CustomUserAdapter(ArrayList<Appointment> data, Context context, Dialog d) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;
        this.dialog = d;


    }

    @Override
    public void onClick(View v) {
        db = new DatabaseHelper(mContext);
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        Appointment dataModel = (Appointment) object;
        ((TextView)dialog.findViewById(R.id.dateTxt)).setText(dataModel.getDatetime());
        List<ServiceType> serviceTypes = db.listServiceTypes(dataModel.getPeriod());
        CustomSpinnerAdatper spinnerAdapter = new CustomSpinnerAdatper(getContext(), serviceTypes);
        ((Spinner)dialog.findViewById(R.id.optionsSpinner)).setAdapter(spinnerAdapter);

        if (v.getId() == R.id.btnBook) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dialog.show();
            }
        }
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Appointment dataModel = getItem(position);
        ViewHolder viewHolder;

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.dateTxt);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.timeTxt);
            viewHolder.txtPeriod = (TextView) convertView.findViewById(R.id.periodTxt);
            viewHolder.btnBook = (TextView) convertView.findViewById(R.id.btnBook);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        viewHolder.txtDate.setText(formatter.format(DateHelper.StringToDate(dataModel.getDatetime())));
        viewHolder.txtTime.setText(formatter.format(DateHelper.AddMinutesToDate(DateHelper.StringToDate(dataModel.getDatetime()), dataModel.getPeriod())));


        viewHolder.txtPeriod.setText("" + dataModel.getPeriod());
        viewHolder.btnBook.setOnClickListener(this);
        viewHolder.btnBook.setTag(position);
        return convertView;
    }

}