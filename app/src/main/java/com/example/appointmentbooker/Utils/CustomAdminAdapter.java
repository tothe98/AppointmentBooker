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

public class CustomAdminAdapter extends ArrayAdapter<Appointment> implements View.OnClickListener {

    private ArrayList<Appointment> dataSet;
    Context mContext;
    DatabaseHelper db;

    private static class ViewHolder {
        TextView txtDate, txtTime, txtPeriod, btnViewDelete;
    }

    public CustomAdminAdapter(ArrayList<Appointment> data, Context context) {
        super(context, R.layout.admin_row_item, data);
        this.dataSet = data;
        this.mContext = context;


    }

    @Override
    public void onClick(View v) {
        db = new DatabaseHelper(mContext);
        int position = (Integer) v.getTag();
        Object object = getItem(position);

        Appointment dataModel = (Appointment) object;
        if (dataModel.getBookedEmail() == null) {
            showCustomDeleteDialog(dataModel.getId(), position);
        } else {
            User u = db.getUserInfo(((Appointment) object).getBookedEmail());
            showCustomViewDialog(u.getFirstName() + " " + u.getLastName(), u.getPhone(), u.getEmail());

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
            convertView = inflater.inflate(R.layout.admin_row_item, parent, false);

            viewHolder.txtDate = (TextView) convertView.findViewById(R.id.dateTxt);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.timeTxt);
            viewHolder.txtPeriod = (TextView) convertView.findViewById(R.id.periodTxt);
            viewHolder.btnViewDelete = (TextView) convertView.findViewById(R.id.btnViewDelete);
            if (dataModel.getBookedEmail() == null) {
                viewHolder.btnViewDelete.setText(R.string.delete_btn_title);
                viewHolder.btnViewDelete.setTextColor(Color.RED);
            } else {
                viewHolder.btnViewDelete.setText(R.string.view_btn_title);
                viewHolder.btnViewDelete.setTextColor(Color.BLACK);
                convertView.findViewById(R.id.root).setBackgroundColor(Color.YELLOW);
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
        viewHolder.btnViewDelete.setOnClickListener(this);
        viewHolder.btnViewDelete.setTag(position);
        return convertView;
    }

    private void showCustomViewDialog(String name, String phone, String email) {
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 40);

        LinearLayout nameLayout = new LinearLayout(mContext);
        nameLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView nameTitleTxt = new TextView(mContext);
        nameTitleTxt.setText(R.string.info_name_title);
        nameTitleTxt.setTextSize(16);
        nameTitleTxt.setTypeface(null, Typeface.BOLD);
        TextView nameTxt = new TextView(mContext);
        nameTxt.setText(name);
        nameTxt.setTextSize(16);
        nameTxt.setPadding(20, 0, 0, 0);
        nameLayout.addView(nameTitleTxt);
        nameLayout.addView(nameTxt);

        LinearLayout phoneLayout = new LinearLayout(mContext);
        phoneLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView phoneTitleTxt = new TextView(mContext);
        phoneTitleTxt.setText(R.string.info_phone_title);
        phoneTitleTxt.setTextSize(16);
        phoneTitleTxt.setTypeface(null, Typeface.BOLD);
        TextView phoneTxt = new TextView(mContext);
        phoneTxt.setText(phone);
        phoneTxt.setTextSize(16);
        phoneTxt.setTypeface(null, Typeface.ITALIC);
        phoneTxt.setPadding(20, 0, 0, 0);
        phoneTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + phone));
                mContext.startActivity(intent);
            }
        });
        phoneLayout.addView(phoneTitleTxt);
        phoneLayout.addView(phoneTxt);

        LinearLayout emailLayout = new LinearLayout(mContext);
        phoneLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView emailTitleTxt = new TextView(mContext);
        emailTitleTxt.setText(R.string.info_email_title);
        emailTitleTxt.setTextSize(16);
        emailTitleTxt.setTypeface(null, Typeface.BOLD);
        TextView emailTxt = new TextView(mContext);
        emailTxt.setText(email);
        emailTxt.setTextSize(16);
        emailTxt.setTypeface(null, Typeface.ITALIC);
        emailTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + email));
                mContext.startActivity(intent);
            }
        });
        emailTxt.setPadding(20, 0, 0, 0);
        emailLayout.addView(emailTitleTxt);
        emailLayout.addView(emailTxt);

        layout.addView(nameLayout);
        layout.addView(phoneLayout);
        layout.addView(emailLayout);


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.information_title)
                .setView(layout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.custom_dialog_bg);
        dialog.show();
    }

    private void showCustomDeleteDialog(int id, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.deletion_title)
                .setMessage(R.string.deletion_confirm_title)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean successDelete = db.deleteAppointment(id);
                        if (successDelete) {
                            Toast.makeText(mContext, R.string.successful_deletion, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                            removeItem(position);
                        } else {
                            Toast.makeText(mContext, R.string.unsuccessful_deletion, Toast.LENGTH_LONG).show();
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