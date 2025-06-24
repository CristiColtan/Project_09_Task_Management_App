package com.example.cctask;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.HashMap;

public class ChartsActivity extends AppCompatActivity {

    PieChart pieChartRoles;
    BarChart barChartAssignedTasks;
    DatabaseHelper dbHelper;
    Button buttonBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);

        buttonBack = findViewById(R.id.buttonBackReports);
        pieChartRoles = findViewById(R.id.pieChartRoles);
        barChartAssignedTasks = findViewById(R.id.barChartAssignedTasks);
        dbHelper = new DatabaseHelper(this);

        buttonBack.setOnClickListener(v -> finish());

        aplicaPreferinteText();

        showUserRolePieChart();
        showTasksPerAssignedUserChart();
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

        buttonBack.setTextSize(textSize);
        buttonBack.setTextColor(colorValue);
    }
    private void showUserRolePieChart() {
        Cursor cursor = dbHelper.getAllUsers();
        int adminCount = 0;
        int userCount = 0;

        while (cursor.moveToNext()) {
            String role = cursor.getString(cursor.getColumnIndexOrThrow("role"));
            if ("admin".equalsIgnoreCase(role)) {
                adminCount++;
            } else {
                userCount++;
            }
        }
        cursor.close();

        ArrayList<PieEntry> entries = new ArrayList<>();
        if (adminCount > 0) entries.add(new PieEntry(adminCount, "Admini"));
        if (userCount > 0) entries.add(new PieEntry(userCount, "Useri"));

        PieDataSet dataSet = new PieDataSet(entries, "Roluri utilizatori");

        ArrayList<Integer> pieColors = new ArrayList<>();
        pieColors.add(Color.parseColor("#FF7043")); // portocaliu
        pieColors.add(Color.parseColor("#66BB6A")); // verde
        dataSet.setColors(pieColors);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);

        pieChartRoles.setData(data);
        pieChartRoles.setUsePercentValues(true);
        pieChartRoles.getDescription().setEnabled(false);
        pieChartRoles.setEntryLabelColor(Color.BLACK);
        pieChartRoles.invalidate();
    }

    private void showTasksPerAssignedUserChart() {
        Cursor cursor = dbHelper.getAllTasks();
        HashMap<String, Integer> taskCount = new HashMap<>();

        while (cursor.moveToNext()) {
            String assignedTo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ASSIGNED_TO));
            taskCount.put(assignedTo, taskCount.getOrDefault(assignedTo, 0) + 1);
        }
        cursor.close();

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> users = new ArrayList<>();
        int index = 0;
        for (String user : taskCount.keySet()) {
            entries.add(new BarEntry(index, taskCount.get(user)));
            users.add(user);
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Task-uri atribuite");
        ArrayList<Integer> barColors = new ArrayList<>();
        barColors.add(Color.parseColor("#42A5F5")); // albastru
        barColors.add(Color.parseColor("#AB47BC")); // mov
        barColors.add(Color.parseColor("#FF7043")); // portocaliu
        barColors.add(Color.parseColor("#66BB6A")); // verde
        barColors.add(Color.parseColor("#EF5350")); // roșu
        dataSet.setColors(barColors);

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.9f);
        data.setValueTextSize(14f);

        barChartAssignedTasks.setData(data);
        barChartAssignedTasks.setFitBars(true);
        barChartAssignedTasks.getDescription().setEnabled(false);

        XAxis xAxis = barChartAssignedTasks.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(users));
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(12f);
        xAxis.setDrawGridLines(false);

        barChartAssignedTasks.getAxisLeft().setTextSize(12f);
        barChartAssignedTasks.getAxisRight().setEnabled(false);

        barChartAssignedTasks.invalidate();
    }
}
