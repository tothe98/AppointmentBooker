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

import com.example.appointmentbooker.Models.SignupUserModel;
import com.example.appointmentbooker.Utils.DatabaseHelper;

public class SignupActivity extends AppCompatActivity {


    EditText fNameTxt, lNameTxt, phoneTxt, emailTxt, passTxt, passConfTxt;
    Button signupBtn;
    TextView haveAccTxt;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        fNameTxt = findViewById(R.id.fnameTxtField);
        lNameTxt = findViewById(R.id.lnameTxtField);
        phoneTxt = findViewById(R.id.phoneTxtField);
        emailTxt = findViewById(R.id.emailTxtField);
        passTxt = findViewById(R.id.passwordTxtField);
        passConfTxt = findViewById(R.id.passwordConfTxtField);
        signupBtn = findViewById(R.id.button);
        haveAccTxt = findViewById(R.id.accTxt);
        db = new DatabaseHelper(this);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fName = fNameTxt.getText().toString();
                String lName = lNameTxt.getText().toString();
                String phone = phoneTxt.getText().toString();
                String email = emailTxt.getText().toString();
                String pass = passTxt.getText().toString();
                String passConf = passConfTxt.getText().toString();
                if(fName.isEmpty() || lName.isEmpty() || phone.isEmpty() || email.isEmpty() || pass.isEmpty() || passConf.isEmpty()){
                    Toast.makeText(SignupActivity.this, R.string.all_fields_required_msg, Toast.LENGTH_SHORT).show();
                } else if(pass.equals(passConf)){
                    if(!db.checkUserExists(email)){
                        if(db.signup(new SignupUserModel(fName, lName, email, phone, pass,passConf))){
                            Toast.makeText(SignupActivity.this, R.string.signup_successful, Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                            startActivity(i);
                        } else {
                            Toast.makeText(SignupActivity.this, R.string.signup_error, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignupActivity.this, R.string.user_exists_msg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignupActivity.this, R.string.two_passwords_arent_same_msg, Toast.LENGTH_SHORT).show();
                }
            }
        });


        haveAccTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });

    }
}