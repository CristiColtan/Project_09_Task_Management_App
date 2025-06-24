package com.example.cctask;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateTaskActivity extends AppCompatActivity {

    EditText titleInput, assignedToInput;
    Button buttonSelecteazaData, buttonSaveTask, buttonBack;
    TextView textDataExpirare;
    DatabaseHelper dbHelper;
    Date dataExpirare ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_task);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        titleInput = findViewById(R.id.taskTitleInput);
        assignedToInput = findViewById(R.id.taskAssignedInput);
        buttonSelecteazaData= findViewById(R.id.buttonSelecteazaData);
        buttonSaveTask = findViewById(R.id.buttonSaveTask);
        buttonBack = findViewById(R.id.buttonBackCreateTask);
        textDataExpirare = findViewById(R.id.textDataExpirare);

        dbHelper = new DatabaseHelper(this);

        aplicaPreferinteText();

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String createdBy = prefs.getString("loggedUser", "necunoscut");

        buttonBack.setOnClickListener(v -> finish());

        buttonSelecteazaData.setOnClickListener(v -> selecteazaDataExpirare());

        buttonSaveTask.setOnClickListener(v -> {
            String title = titleInput.getText().toString().trim();
            String assignedTo = assignedToInput.getText().toString().trim();

            if (title.isEmpty() || assignedTo.isEmpty() || dataExpirare == null) {
                Toast.makeText(this, "Completează toate câmpurile", Toast.LENGTH_SHORT).show();
                return;
            }

            Task task = new Task(title, assignedTo, createdBy, dataExpirare);
            boolean success = dbHelper.insertTask(task);

            if (success) {
                Toast.makeText(this, "Task salvat!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Eroare la salvare!", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void selecteazaDataExpirare() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            // Setează data în Calendar
            calendar.set(selectedYear, selectedMonth, selectedDay);

            // Formatează data într-un string lizibil și setează în TextView
            dataExpirare = calendar.getTime();
            String dataFormatata = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dataExpirare);
            textDataExpirare.setText(dataFormatata);
        }, year, month, day);

        datePickerDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        aplicaPreferinteText();
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
        buttonSaveTask.setTextSize(textSize);
        buttonSaveTask.setTextColor(colorValue);

        buttonBack.setTextSize(textSize);
        buttonBack.setTextColor(colorValue);

        buttonSelecteazaData.setTextSize(textSize);
        buttonSelecteazaData.setTextColor(colorValue);
    }
}