package com.example.cctask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    Button loginButton;
    TextView goToRegister;
    DatabaseHelper dbHelper;
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

        SharedPreferences prefss = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String savedUser = prefss.getString("loggedUser", null);
        String savedRole = prefss.getString("role", null);

        if (savedUser != null && savedRole != null) {
            // Userul este deja logat → mergem direct în MainActivity
            if ("admin".equals(savedRole)) {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("username", savedUser);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(this, MainActivityUser.class);
                intent.putExtra("username", savedUser);
                startActivity(intent);
            }

            finish();
        }

        usernameInput = findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        goToRegister = findViewById(R.id.goToRegister);

        dbHelper = new DatabaseHelper(this);

        loginButton.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completează toate câmpurile!", Toast.LENGTH_SHORT).show();
            } else {
                boolean valid = dbHelper.checkUserCredentials(username, password);
                if (valid) {
                    String role = dbHelper.getUserRole(username);
                    Toast.makeText(this, "Autentificare reușită!", Toast.LENGTH_SHORT).show();

                    SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString("loggedUser", username);
                    editor.putString("role", role);
                    editor.apply();

                    // Redirectează către activitatea principală
                    if ("admin".equals(role)) {
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(this, MainActivityUser.class);
                        intent.putExtra("username", username);
                        startActivity(intent);
                    }

                    finish();
                } else {
                    Toast.makeText(this, "Date incorecte!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        goToRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }


}