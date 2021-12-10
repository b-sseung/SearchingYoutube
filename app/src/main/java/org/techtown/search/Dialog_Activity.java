package org.techtown.search;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Dialog_Activity extends AppCompatActivity {

    int id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        if (getIntent() != null) {
            Intent intent1 = getIntent();
            id = intent1.getIntExtra("1818", 0);
        }

        Button button = findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (id != 0 ){
                    intent.putExtra("1818", id);
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        Button button2 = findViewById(R.id.button2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (id != 0 ){
                    intent.putExtra("1818", id);
                }
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
    }
}
