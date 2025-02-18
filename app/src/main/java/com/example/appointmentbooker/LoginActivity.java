package com.example.appointmentbooker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appointmentbooker.Models.LoginUserModel;
import com.example.appointmentbooker.Utils.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    EditText emailTxt, passwordTxt;
    Button loginBtn;
    TextView forgotTxt, noaccTxt;

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailTxt = findViewById(R.id.emailTxtField);
        passwordTxt = findViewById(R.id.passwordTxtField);
        loginBtn = findViewById(R.id.button);
        forgotTxt = findViewById(R.id.forgetTxt);
        noaccTxt = findViewById(R.id.noaccTxt);
        db = new DatabaseHelper(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(emailTxt.getText().toString().isEmpty() || passwordTxt.getText().toString().isEmpty()){
                    Toast.makeText(LoginActivity.this, R.string.all_fields_required_msg, Toast.LENGTH_SHORT).show();
                } else {
                    if(db.login(new LoginUserModel(emailTxt.getText().toString(), passwordTxt.getText().toString()))){
                        Toast.makeText(LoginActivity.this, "Sikeres bejelentkezés", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(LoginActivity.this, R.string.wrong_email_or_password_msg, Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        noaccTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(i);
            }
        });

        forgotTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Nincs még kész az activity", Toast.LENGTH_SHORT).show();
            }
        });



    }
}