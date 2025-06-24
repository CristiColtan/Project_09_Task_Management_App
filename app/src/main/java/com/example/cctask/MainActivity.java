package com.example.cctask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.Chart;
import com.google.android.gms.tasks.Tasks;

public class MainActivity extends AppCompatActivity {
    Button buttonSetari, buttonAboutUs, buttonUsers, buttonTasks,
            buttonLogout, buttonCreateTask, buttonReports, buttonParse;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonAboutUs= findViewById(R.id.buttonAboutUs);
        buttonSetari = findViewById(R.id.buttonSettings);
        buttonTasks=findViewById(R.id.buttonTasks);
        buttonUsers=findViewById(R.id.buttonUsers);
        buttonLogout = findViewById(R.id.buttonLogoutMain);
        buttonCreateTask = findViewById(R.id.buttonCreateTask);
        buttonReports = findViewById(R.id.buttonReports);
        buttonParse = findViewById(R.id.buttonParse);

        aplicaPreferinteText();

        buttonUsers.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(intent);
        });

        buttonParse.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, JsonUsersActivity.class);
            startActivity(intent);
        });

        buttonReports.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChartsActivity.class);
            startActivity(intent);
        });

        buttonTasks.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TasksActivity.class);
            startActivity(intent);
        });

        buttonAboutUs.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AboutUs.class);
            startActivity(intent);
        });

        buttonCreateTask.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateTaskActivity.class);
            startActivity(intent);
        });

        buttonSetari.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SetariActivity.class);
            startActivity(intent);
        });

        buttonLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("loggedUser");
            editor.remove("role");
            editor.apply();

            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        aplicaPreferinteText(); // 🔄 aplică preferințele de fiecare dată când revii în activitate
    }
    private void aplicaPreferinteText() {
        SharedPreferences prefs = getSharedPreferences(SetariActivity.PREFERINTE, MODE_PRIVATE);
        int textSize = prefs.getInt("textSize", 16);
        String culoare = prefs.getString("textColor", "Negru");

        int colorValue = Color.BLACK;
        switch (culoare) {
            case "Roșu": colorValue = Color.RED; break;
            case "Alb": colorValue = Color.WHITE; break;
            case "Verde": colorValue = Color.GREEN; break;
        }

        // Aplicăm la toate TextView/EditText relevante
        buttonAboutUs.setTextSize(textSize);
        buttonAboutUs.setTextColor(colorValue);

        buttonReports.setTextSize(textSize);
        buttonReports.setTextColor(colorValue);

        buttonUsers.setTextSize(textSize);
        buttonUsers.setTextColor(colorValue);

        buttonTasks.setTextSize(textSize);
        buttonTasks.setTextColor(colorValue);

        buttonSetari.setTextSize(textSize);
        buttonSetari.setTextColor(colorValue);

        buttonLogout.setTextSize(textSize);
        buttonLogout.setTextColor(colorValue);

        buttonCreateTask.setTextSize(textSize);
        buttonCreateTask.setTextColor(colorValue);

        buttonParse.setTextSize(textSize);
        buttonParse.setTextColor(colorValue);
    }
}