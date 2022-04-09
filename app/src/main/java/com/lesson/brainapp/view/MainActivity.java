package com.lesson.brainapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.lesson.brainapp.R;
import com.lesson.brainapp.model.Test.Test;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLow;
    private Button btnMiddle;
    private Button btnHigh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnLow = findViewById(R.id.btn_low_level);
        btnMiddle = findViewById(R.id.btn_middle_level);
        btnHigh = findViewById(R.id.btn_high_level);
        btnLow.setOnClickListener(this);
        btnMiddle.setOnClickListener(this);
        btnHigh.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, GameActivity.class);
        int id = v.getId();
        if (id == R.id.btn_low_level) {
            intent.putExtra("level", Test.LOW);
            intent.putExtra("levelRecordName", "recordLow");
            startActivity(intent);
        } else if (id == R.id.btn_middle_level) {
            intent.putExtra("level", Test.MIDDLE);
            intent.putExtra("levelRecordName", "recordMiddle");
            startActivity(intent);
        } else if (id == R.id.btn_high_level) {
            intent.putExtra("level", Test.HIGH);
            intent.putExtra("levelRecordName", "recordHigh");
            startActivity(intent);
        }
    }

}