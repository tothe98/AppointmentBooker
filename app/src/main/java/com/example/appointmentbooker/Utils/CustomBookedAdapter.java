package com.example.appointmentbooker.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appointmentbooker.Models.Appointment;
import com.example.appointmentbooker.Models.User;
import com.example.appointmentbooker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomBookedAdapter extends ArrayAdapter<Appointment> implements View.OnClickListener {

    private ArrayList<Appointment> dataSet;
    Context mContext;
    DatabaseHelper db;

    private static class ViewHolder {
        TextView txtDate, txtTime, txtPeriod, btnCancel;
    }

    public CustomBookedAdapter(ArrayList<Appointment> data, Context context) {
        super(context, R.layout.booked_row_item, data);
        this.dataSet = data;
        this.mContext = context;


    }

    @Override
    public void onClick(View v) {
        db = new DatabaseHelper(mContext);
        int position = (Integer) v.getTag();
        Object object = getItem(position);

        Appointment dataModel = (Appointment) object;
        showCustomDeleteDialog(dataModel.getId(), position);


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
            convertView = inflater.inflate(R.layout.booked_row_item, parent, false);

            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.dateTxt);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.timeTxt);
            viewHolder.txtPeriod = (TextView) convertView.findViewById(R.id.periodTxt);
            viewHolder.btnCancel = (TextView) convertView.findViewById(R.id.btnCancel);
            Date appointmentDate = DateHelper.StringToDate(dataModel.getDatetime());
            boolean canCancel = DateHelper.CompareDate(appointmentDate);
            boolean beforeCheck = DateHelper.BeforeCheck(appointmentDate);
            if (canCancel && !beforeCheck) {
                viewHolder.btnCancel.setVisibility(View.VISIBLE);
                viewHolder.btnCancel.setTextColor(Color.RED);
            } else {
                viewHolder.btnCancel.setVisibility(View.INVISIBLE);
            }


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
        viewHolder.btnCancel.setOnClickListener(this);
        viewHolder.btnCancel.setTag(position);
        return convertView;
    }

    private void showCustomDeleteDialog(int id, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.cancel_title)
                .setMessage(R.string.cancel_question)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean successDelete = db.removeBooking(id);
                        if (successDelete) {
                            Toast.makeText(mContext, R.string.successful_cancel, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            removeItem(position);
                        } else {
                            Toast.makeText(mContext, R.string.unsuccessful_cancel, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_bg);
        dialog.show();
    }

    private void removeItem(int position) {
        dataSet.remove(position);
        notifyDataSetChanged();
    }
}