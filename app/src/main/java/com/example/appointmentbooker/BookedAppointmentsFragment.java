package com.example.appointmentbooker;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.appointmentbooker.Models.Appointment;
import com.example.appointmentbooker.Utils.CustomBookedAdapter;
import com.example.appointmentbooker.Utils.CustomUserAdapter;
import com.example.appointmentbooker.Utils.DatabaseHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.ArrayList;
import java.util.List;

public class BookedAppointmentsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    DatabaseHelper db;
    GoogleSignInAccount account;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    ArrayList<Appointment> dataModels;
    ListView listView;
    Dialog dialog;
    private static ArrayAdapter<Appointment> adapter;
    private String mParam1;
    private String mParam2;

    public BookedAppointmentsFragment() {
        // Required empty public constructor
    }
    public static BookedAppointmentsFragment newInstance(String param1, String param2) {
        BookedAppointmentsFragment fragment = new BookedAppointmentsFragment();
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
        return inflater.inflate(R.layout.fragment_booked_appointments, container, false);
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

        String email = "";
        if(account!= null){
            email = account.getEmail();
        } else {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences(LoginActivity.SHARED_PREFS, Context.MODE_PRIVATE);
            String spemail = sharedPreferences.getString("email", "");
            email = spemail;
        }

        dataModels = db.filterByEmailAppointment(email);
        adapter = new CustomBookedAdapter(dataModels, getContext());
        listView.setAdapter(adapter);


    }
}