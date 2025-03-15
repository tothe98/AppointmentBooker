package com.example.appointmentbooker;

import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.appointmentbooker.Models.Role;
import com.example.appointmentbooker.Utils.DatabaseHelper;

public class LoginActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
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

        isLogged();

        emailTxt = findViewById(R.id.emailTxtField);
        passwordTxt = findViewById(R.id.passwordTxtField);
        loginBtn = findViewById(R.id.button);
        forgotTxt = findViewById(R.id.forgetTxt);
        noaccTxt = findViewById(R.id.noaccTxt);
        db = new DatabaseHelper(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailTxt.getText().toString().isEmpty() || passwordTxt.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, R.string.all_fields_required_msg, Toast.LENGTH_SHORT).show();
                } else {
                    int[] result = db.login(new LoginUserModel(emailTxt.getText().toString(), passwordTxt.getText().toString()));
                    if (result[0] == 1) {
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("isLogged", "true");
                        editor.putString("email", emailTxt.getText().toString());
                        editor.putString("role", ""+result[1]);
                        editor.apply();
                        if(result[1] == 1){
                            Intent i = new Intent(LoginActivity.this, UserMainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addCategory(Intent.CATEGORY_HOME);
                            startActivity(i);
                            finish();
                        }
                        if(result[1] == 2){
                            Intent i = new Intent(LoginActivity.this, AdminMainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addCategory(Intent.CATEGORY_HOME);
                            startActivity(i);
                            finish();
                        }

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

    private void isLogged(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String check = sharedPreferences.getString("isLogged", "");
        String role = sharedPreferences.getString("role", "");
        if(check.equals("true")){
            if(role.equals("1")) {
                Intent i = new Intent(LoginActivity.this, UserMainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
                finish();
            } else if(role.equals("2")){
                Intent i = new Intent(LoginActivity.this, AdminMainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
                finish();
            }
        }
    }
}