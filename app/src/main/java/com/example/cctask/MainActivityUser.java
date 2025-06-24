package com.example.cctask;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivityUser extends AppCompatActivity {

    Button buttonLogout;
    ListView listViewTasks;
    DatabaseHelper dbHelper;
    String currentUser;

    ArrayList<Task> userTasks = new ArrayList<>();
    ArrayList<String> displayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_user);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonLogout = findViewById(R.id.buttonLogoutMainUser);
        listViewTasks = findViewById(R.id.listViewUserTasks);
        dbHelper = new DatabaseHelper(this);

        // Preluăm utilizatorul conectat
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUser = prefs.getString("loggedUser", "");

        aplicaPreferinteText();
        loadUserTasks();

        buttonLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();
            editor.remove("loggedUser");
            editor.remove("role");
            editor.apply();

            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        listViewTasks.setOnItemClickListener((parent, view, position, id) -> {
            Task task = userTasks.get(position);

            new AlertDialog.Builder(this)
                    .setTitle(task.getTitle())
                    .setMessage(
                            "Atribuit către: " + task.getAssignedTo() + "\n" +
                                    "Creat de: " + task.getCreatedBy() + "\n" +
                                    "Dată expirare: " + Task.dateFormat.format(task.getDataExpirare())
                    )
                    .setPositiveButton("OK", null)
                    .show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        aplicaPreferinteText();
        loadUserTasks();
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

        buttonLogout.setTextSize(textSize);
        buttonLogout.setTextColor(colorValue);
    }

    private void loadUserTasks() {
        userTasks.clear();
        displayList.clear();

        Cursor cursor = dbHelper.getAllTasks();
        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
                String assignedTo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ASSIGNED_TO));
                String createdBy = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_BY));
                String dueDate = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DUE_DATE));

                if (assignedTo.equals(currentUser)) {
                    try {
                        Task task = new Task(title, assignedTo, createdBy, Task.dateFormat.parse(dueDate));
                        userTasks.add(task);
                        displayList.add("• " + title);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
        listViewTasks.setAdapter(adapter);
    }
}
