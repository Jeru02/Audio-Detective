package com.example.AudioDetective;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class answerActivity3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_answer3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
            //ensure view is not covered by androids nav bar
        });


        Button button = findViewById(R.id.back_button);


        Intent intent = getIntent();
        String artist = intent.getStringExtra("artist");
        String title = intent.getStringExtra("title");

        Log.i("infooo", artist +" "+ title);

        TextView textViewToChange = findViewById(R.id.textView3);
        textViewToChange.setText(artist);

        TextView textViewToChange2 = findViewById(R.id.textView4);
        textViewToChange2.setText(title);



    }


    public void goBACK (View v){

        Button button = findViewById(R.id.back_button);

        Intent intent = new Intent(answerActivity3.this, MainActivity.class);
        startActivity(intent);
        //directing back to main activity



    }






}

