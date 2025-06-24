package com.example.cctask;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class UsersActivity extends AppCompatActivity {

    Button buttonBack;
    ListView listViewUsers;
    DatabaseHelper dbHelper;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_users);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonBack = findViewById(R.id.buttonBackUsers);
        listViewUsers = findViewById(R.id.listViewUsers);
        dbHelper = new DatabaseHelper(this);

        // Preluăm userul logat din preferințe
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        currentUser = prefs.getString("loggedUser", "");

        aplicaPreferinteText();
        loadAllUsers();

        buttonBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        aplicaPreferinteText();
        loadAllUsers();
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

        buttonBack.setTextSize(textSize);
        buttonBack.setTextColor(colorValue);
    }

    private void loadAllUsers() {
        ArrayList<String> usersList = new ArrayList<>();
        Cursor cursor = dbHelper.getAllUsers();

        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_USERNAME));
                String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
                usersList.add("• " + username + " (" + role + ")");
            } while (cursor.moveToNext());
        }

        cursor.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, usersList);
        listViewUsers.setAdapter(adapter);

        listViewUsers.setOnItemLongClickListener((parent, view, position, id) -> {
            String item = parent.getItemAtPosition(position).toString();
            String username = item.replaceAll("•\\s*", "").split(" ")[0];

            if (username.equals("admin")) {
                Toast.makeText(this, "Nu poți șterge utilizatorul 'admin'!", Toast.LENGTH_SHORT).show();
                return true;
            }

            if (username.equals(currentUser)) {
                Toast.makeText(this, "Nu poți șterge propriul cont!", Toast.LENGTH_SHORT).show();
                return true;
            }

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirmare ștergere")
                    .setMessage("Ești sigur că vrei să ștergi utilizatorul \"" + username + "\"?")
                    .setPositiveButton("Șterge", (dialog, which) -> {
                        boolean deleted = dbHelper.deleteUser(username);
                        if (deleted) {
                            Toast.makeText(this, "Utilizator șters!", Toast.LENGTH_SHORT).show();
                            loadAllUsers();
                        } else {
                            Toast.makeText(this, "Eroare la ștergere!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Anulează", null)
                    .show();

            return true;
        });
    }
}
