package com.example.cctask;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TasksActivity extends AppCompatActivity {

    Button buttonBack;
    ListView listViewTasks;
    DatabaseHelper dbHelper;
    private ArrayList<Task> taskList = new ArrayList<>();
    private TaskItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tasks);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        buttonBack = findViewById(R.id.buttonBackTasks);
        listViewTasks = findViewById(R.id.listViewTasks);
        dbHelper = new DatabaseHelper(this);

        loadAllTasks();

        buttonBack.setOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllTasks();
    }

    private void loadAllTasks() {
        taskList.clear();
        Cursor cursor = dbHelper.getAllTasks();

        if (cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));
                String assignedTo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ASSIGNED_TO));
                String createdBy = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_BY));
                String dueDateStr = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DUE_DATE));

                try {
                    Date dueDate = Task.dateFormat.parse(dueDateStr);
                    taskList.add(new Task(title, assignedTo, createdBy, dueDate));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        adapter = new TaskItemAdapter(this, taskList);
        listViewTasks.setAdapter(adapter);

        listViewTasks.setOnItemLongClickListener((parent, view, position, id) -> {
            Task task = taskList.get(position);

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Confirmare ștergere")
                    .setMessage("Ești sigur că vrei să ștergi task-ul: \"" + task.getTitle() + "\"?")
                    .setPositiveButton("Șterge", (dialog, which) -> {
                        boolean deleted = dbHelper.deleteTask(task.getTitle(), task.getCreatedBy());
                        if (deleted) {
                            Toast.makeText(this, "Task șters!", Toast.LENGTH_SHORT).show();
                            loadAllTasks(); // refresh list
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
