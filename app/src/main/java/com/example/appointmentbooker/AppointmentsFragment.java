package com.example.appointmentbooker;

import static android.content.Context.MODE_PRIVATE;
import static com.example.appointmentbooker.LoginActivity.SHARED_PREFS;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appointmentbooker.Models.Appointment;
import com.example.appointmentbooker.Models.ServiceType;
import com.example.appointmentbooker.Utils.CustomUserAdapter;
import com.example.appointmentbooker.Utils.CustomAdminAdapter;
import com.example.appointmentbooker.Utils.DatabaseHelper;
import com.example.appointmentbooker.Utils.DateHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.function.Function;


public class AppointmentsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    GoogleSignInAccount account;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    public AppointmentsFragment() {
        // Required empty public constructor
    }

    ArrayList<Appointment> dataModels;
    ListView listView;
    public Dialog dialog;
    DatabaseHelper db;
    private static ArrayAdapter<Appointment> adapter;

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
        db = new DatabaseHelper(getContext());
        listView = view.findViewById(R.id.listView);
        dataModels = new ArrayList<>();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(getContext(), gso);
        account = GoogleSignIn.getLastSignedInAccount(getContext());

        SharedPreferences sharedPreferences = getContext().getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "");
        if (role.equals("1") || account != null) {
            listLoading();


        } else if (role.equals("2")) {
            dataModels = (ArrayList<Appointment>) db.listAdminAppointments();
            adapter = new CustomAdminAdapter(dataModels, getContext());
            listView.setAdapter(adapter);
        }
    }

    public void listLoading() {
        dataModels = (ArrayList<Appointment>) db.listAppointments();
        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.getWindow().setLayout(getView().getLayoutParams().MATCH_PARENT, getView().getLayoutParams().WRAP_CONTENT);
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
                String service_type = ((ServiceType) ((Spinner) dialog.findViewById(R.id.optionsSpinner)).getSelectedItem()).getName();
                String date = ((TextView) dialog.findViewById(R.id.dateTxt)).getText().toString();
                Appointment temp = null;
                for (Iterator<Appointment> iterator = dataModels.iterator(); iterator.hasNext(); ) {
                    Appointment appointment = iterator.next();
                    if (appointment.getDatetime().equals(date)) {
                        temp = appointment;
                    }
                }
                Calendar beginTime = Calendar.getInstance();
                beginTime.setTime(DateHelper.StringToDate(date));
                Calendar endTime = Calendar.getInstance();
                endTime.setTime(DateHelper.AddMinutesToDate(DateHelper.StringToDate(date), temp.getPeriod()));
                SharedPreferences sharedPreferences = getContext().getSharedPreferences(LoginActivity.SHARED_PREFS, Context.MODE_PRIVATE);
                String email = sharedPreferences.getString("email", "");
                if (account != null) {
                    email = account.getEmail();
                }
                boolean successBook = db.bookAppointment(temp.getId(), email, service_type);
                if (successBook) {
                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setData(Events.CONTENT_URI)
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                            .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                            .putExtra(Events.TITLE, service_type)
                            .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
                    startActivity(intent);


                    for (Iterator<Appointment> iterator = dataModels.iterator(); iterator.hasNext(); ) {
                        Appointment appointment = iterator.next();
                        if (appointment.getDatetime().equals(date)) {
                            iterator.remove();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(), "Sikertelen foglalás", Toast.LENGTH_SHORT).show();
                }
            }
        });


        adapter = new CustomUserAdapter(dataModels, getContext(), dialog);
        listView.setAdapter(adapter);
    }


}