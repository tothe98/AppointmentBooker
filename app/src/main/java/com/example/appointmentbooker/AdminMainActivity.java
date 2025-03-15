package com.example.appointmentbooker;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdminMainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavView;
    FrameLayout frameLayout;

    private String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_main);
        replaceFragment(new AppointmentsFragment());

        frameLayout = findViewById(R.id.frame_layout);
        bottomNavView = findViewById(R.id.bottomNavigationView2);

        bottomNavView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.appointments_tab) {
                replaceFragment(new AppointmentsFragment());
            } else if (item.getItemId() == R.id.add_appointment_tab) {
                replaceFragment(new AddAppointmentFragment());

            } else if (item.getItemId() == R.id.account_tab) {
                replaceFragment(new AccountFragment());

            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}