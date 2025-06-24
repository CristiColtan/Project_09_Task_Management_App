package com.example.cctask;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class JsonUsersActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<String> userList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    Button buttonBackTop, buttonBackBottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Layout vertical principal
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(16, 16, 16, 16);

        // Buton sus
        buttonBackTop = new Button(this);
        buttonBackTop.setText("Back");
        layout.addView(buttonBackTop);

        // ListView fără weight
        listView = new ListView(this);
        LinearLayout.LayoutParams listParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        listView.setLayoutParams(listParams);
        layout.addView(listView);

        // Buton jos cu marginTop
        buttonBackBottom = new Button(this);
        buttonBackBottom.setText("Back");
        LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        bottomParams.topMargin = 40; // spațiu între listă și buton
        buttonBackBottom.setLayoutParams(bottomParams);
        layout.addView(buttonBackBottom);

        setContentView(layout);

        // Adapter
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
        listView.setAdapter(adapter);

        // Back
        buttonBackTop.setOnClickListener(v -> finish());
        buttonBackBottom.setOnClickListener(v -> finish());

        aplicaPreferinteText();
        // Descarcă JSON
        new DownloadJsonTask().execute("https://jsonplaceholder.typicode.com/users");
    }

    class DownloadJsonTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                return response.toString();
            } catch (Exception e) {
                Log.e("JSON", "Eroare la descărcare", e);
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) return;
            try {
                JSONArray jsonArray = new JSONArray(result);
                userList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject user = jsonArray.getJSONObject(i);
                    String name = user.getString("name");
                    String username = user.getString("username");
                    userList.add(name + " (" + username + ")");
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e("JSON", "Eroare la parsare", e);
            }
        }
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

        buttonBackBottom.setTextSize(textSize);
        buttonBackBottom.setTextColor(colorValue);
    }
}
