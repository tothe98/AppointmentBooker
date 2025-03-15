package com.example.appointmentbooker;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.appointmentbooker.Models.Appointment;
import com.example.appointmentbooker.Utils.DatabaseHelper;
import com.example.appointmentbooker.Utils.DateHelper;

import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddAppointmentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddAppointmentFragment extends Fragment {

    Button addBtn, setBtn;
    EditText dateTxt, periodTxt;
    String date_time = "";
    int mYear = 0;
    int mMonth = 0;
    int mDay = 0;
    int mHour = 0;
    int mMinute = 0;

    DatabaseHelper databaseHelper;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddAppointmentFragment() {
        // Required empty public constructor
    }
    public static AddAppointmentFragment newInstance(String param1, String param2) {
        AddAppointmentFragment fragment = new AddAppointmentFragment();
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
        return inflater.inflate(R.layout.fragment_add_appointment, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addBtn = view.findViewById(R.id.buttonAdd);
        setBtn = view.findViewById(R.id.buttonSet);
        dateTxt = view.findViewById(R.id.editTextDatetime);
        periodTxt = view.findViewById(R.id.editTextPeriod);
        databaseHelper = new DatabaseHelper(getContext());

        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (DateHelper.BeforeCheck(mYear, mMonth, mDay, mHour, mMinute)) {
                    Toast.makeText(getContext(), R.string.wrong_date, Toast.LENGTH_LONG).show();
                } else {
                    if (databaseHelper.checkExistsAppointment(date_time)) {
                        Toast.makeText(getContext(), R.string.appointment_exists, Toast.LENGTH_LONG).show();
                    } else {
                        int period = Integer.valueOf(periodTxt.getText().toString());
                        boolean addSuccess = databaseHelper.addAppointment(new Appointment(date_time, period));
                        if (addSuccess) {
                            Toast.makeText(getContext(), R.string.successful_add, Toast.LENGTH_LONG).show();
                            dateTxt.setText("");
                            periodTxt.setText("");
                        } else {
                            Toast.makeText(getContext(), R.string.failed_add, Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

        });
    }

    private void datePicker() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mYear = year;
                        mMonth = monthOfYear;
                        mDay = dayOfMonth;
                        date_time = year + "." + ((monthOfYear + 1) <= 9 ? "0" + (monthOfYear + 1) : (monthOfYear + 1)) + "." + (dayOfMonth <= 9 ? "0" + dayOfMonth : dayOfMonth);
                        tiemPicker();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void tiemPicker() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mHour = hourOfDay;
                        mMinute = minute;
                        date_time = date_time + " " + (hourOfDay <= 9 ? "0" + hourOfDay : hourOfDay) + ":" + (minute <= 9 ? "0" + minute : minute);
                        dateTxt.setText(date_time);
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }
}