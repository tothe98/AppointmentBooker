package com.example.appointmentbooker;

import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.appointmentbooker.Models.Appointment;
import com.example.appointmentbooker.Models.ServiceType;
import com.example.appointmentbooker.Utils.CustomAdapter;
import com.example.appointmentbooker.Utils.DateHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;


public class AppointmentsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public AppointmentsFragment() {
        // Required empty public constructor
    }

    ArrayList<Appointment> dataModels;
    ListView listView;
    Dialog dialog;
    private static CustomAdapter adapter;

    public static AppointmentsFragment newInstance(String param1, String param2) {
        AppointmentsFragment fragment = new AppointmentsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_appointments, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.listView);
        dataModels = new ArrayList<>();
        dataModels.add(new Appointment(1, "2025.11.01 11:00", 45));
        dataModels.add(new Appointment(2, "2025.11.01 12:00", 30));
        dataModels.add(new Appointment(3, "2025.11.01 13:30", 60));
        dataModels.add(new Appointment(4, "2025.11.02 09:00", 45));
        dataModels.add(new Appointment(5, "2025.11.02 10:15", 50));
        dataModels.add(new Appointment(6, "2025.11.02 11:30", 35));
        dataModels.add(new Appointment(7, "2025.11.02 13:00", 40));
        dataModels.add(new Appointment(8, "2025.11.02 14:15", 25));
        dataModels.add(new Appointment(9, "2025.11.02 15:30", 50));
        dataModels.add(new Appointment(10, "2025.11.02 16:45", 55));
        dataModels.add(new Appointment(11, "2025.11.02 18:00", 45));
        dataModels.add(new Appointment(12, "2025.11.02 19:15", 50));
        dataModels.add(new Appointment(13, "2025.11.03 09:00", 30));
        dataModels.add(new Appointment(14, "2025.11.03 10:15", 35));
        dataModels.add(new Appointment(15, "2025.11.03 11:30", 40));
        dataModels.add(new Appointment(16, "2025.11.03 12:45", 25));
        dataModels.add(new Appointment(17, "2025.11.03 14:00", 50));
        dataModels.add(new Appointment(18, "2025.11.03 15:30", 55));
        dataModels.add(new Appointment(19, "2025.11.03 16:45", 60));
        dataModels.add(new Appointment(20, "2025.11.03 18:00", 30));

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.getWindow().setLayout(view.getLayoutParams().MATCH_PARENT, view.getLayoutParams().WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.custom_dialog_bg));
        dialog.setCancelable(false);
        Function<Integer, Void> dialogOpen = id -> {
            dialog.show();
            return null;
        };
        dialog.findViewById(R.id.cancelBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.bookBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String service_type = ((ServiceType)((Spinner) dialog.findViewById(R.id.optionsSpinner)).getSelectedItem()).getName();
                String date = ((TextView) dialog.findViewById(R.id.dateTxt)).getText().toString();
                Appointment temp = null;
                for (Iterator<Appointment> iterator = dataModels.iterator(); iterator.hasNext();) {
                    Appointment appointment = iterator.next();
                    if (appointment.getDatetime().equals(date)) {
                        temp=appointment;
                    }
                }
                Calendar beginTime = Calendar.getInstance();
                beginTime.setTime(DateHelper.StringToDate(date));
                Calendar endTime = Calendar.getInstance();
                endTime.setTime(DateHelper.AddMinutesToDate(DateHelper.StringToDate(date), temp.getPeriod()));

                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(Events.TITLE, service_type)
                        .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
                startActivity(intent);


                for (Iterator<Appointment> iterator = dataModels.iterator(); iterator.hasNext();) {
                    Appointment appointment = iterator.next();
                    if (appointment.getDatetime().equals(date)) {
                        iterator.remove();
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        adapter = new CustomAdapter(dataModels, getContext().getApplicationContext(), dialog);
        listView.setAdapter(adapter);
    }


}