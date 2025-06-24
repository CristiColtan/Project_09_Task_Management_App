package com.example.cctask;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SetariActivity extends AppCompatActivity {

    private SeekBar seekTextSize;
    private Spinner spinnerCuloare;
    private Button btnSalveaza, buttonBack;

    public static final String PREFERINTE = "PreferinteUtilizator";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_setari);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        seekTextSize = findViewById(R.id.seekTextSize);
        spinnerCuloare = findViewById(R.id.spinnerCuloare);
        btnSalveaza = findViewById(R.id.buttonSave);
        buttonBack=findViewById(R.id.buttonBackSettings);

        buttonBack.setOnClickListener(v->{
            finish();
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.culori_text, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuloare.setAdapter(adapter);

        btnSalveaza.setOnClickListener(v -> {
            int textSize = seekTextSize.getProgress() + 12; // ex: între 12 și 30sp
            String culoare = spinnerCuloare.getSelectedItem().toString();

            SharedPreferences prefs = getSharedPreferences(PREFERINTE, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("textSize", textSize);
            editor.putString("textColor", culoare);
            editor.apply();

            Toast.makeText(this, "Setări salvate", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}