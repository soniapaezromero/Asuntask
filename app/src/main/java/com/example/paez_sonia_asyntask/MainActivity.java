package com.example.paez_sonia_asyntask;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button ejercicio1;
    Button ejercicio2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ejercicio1=(Button)findViewById(R.id.botonEjecicio1);
        ejercicio2= (Button)findViewById(R.id.botonEjercicio2);
        ejercicio1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento =new Intent(v.getContext(),MainActivity2.class);
                startActivity(intento);
            }
        });
        ejercicio2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intento =new Intent(v.getContext(),MainActivity3.class);
                startActivity(intento);
            }
        });


    }
}