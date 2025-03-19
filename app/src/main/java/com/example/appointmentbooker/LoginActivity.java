package com.example.appointmentbooker;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.appointmentbooker.Models.LoginUserModel;
import com.example.appointmentbooker.Models.Role;
import com.example.appointmentbooker.Models.User;
import com.example.appointmentbooker.Utils.DatabaseHelper;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";
    EditText emailTxt, passwordTxt;
    Button loginBtn;
    TextView noaccTxt;
    ImageButton googleSigninBtn;
    DatabaseHelper db;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    GoogleSignInAccount account;

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
        noaccTxt = findViewById(R.id.noaccTxt);
        googleSigninBtn = findViewById(R.id.googleSigninBtn);
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
                        editor.putString("role", "" + result[1]);
                        editor.apply();
                        if (result[1] == 1) {
                            Intent i = new Intent(LoginActivity.this, UserMainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            i.addCategory(Intent.CATEGORY_HOME);
                            startActivity(i);
                            finish();
                        }
                        if (result[1] == 2) {
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

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(LoginActivity.this, gso);
        googleSigninBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInWithGoogle();
            }
        });
    }

    private void signInWithGoogle() {
        Intent i = gsc.getSignInIntent();
        startActivityForResult(i, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                task.getResult(ApiException.class);
                boolean isExists = db.checkUserExists(task.getResult().getEmail());
                if (!isExists) {
                    User user = new User();
                    user.setLastName(task.getResult().getFamilyName());
                    user.setFirstName(task.getResult().getGivenName());
                    user.setEmail(task.getResult().getEmail());
                    user.setPhone("-");
                    user.setPassword("GOOGLE");
                    boolean addUser = db.addUser(user);
                    if (!addUser) {
                        throw new Exception("Something went wrong");
                    }
                }
                System.out.println(task.getResult().getEmail());
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("isLogged", "true");
                editor.putString("role", "1");
                editor.apply();
                Intent i = new Intent(LoginActivity.this, UserMainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            } catch (ApiException e) {
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(LoginActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void isLogged() {
        if (account != null) {
            Intent i = new Intent(LoginActivity.this, UserMainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addCategory(Intent.CATEGORY_HOME);
            startActivity(i);
            finish();
        }
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        String check = sharedPreferences.getString("isLogged", "");
        String role = sharedPreferences.getString("role", "");
        if (check.equals("true")) {
            if (role.equals("1")) {
                Intent i = new Intent(LoginActivity.this, UserMainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
                finish();
            } else if (role.equals("2")) {
                Intent i = new Intent(LoginActivity.this, AdminMainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
                finish();
            }
        }
    }
}