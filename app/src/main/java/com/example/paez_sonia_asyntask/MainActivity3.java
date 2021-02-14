package com.example.paez_sonia_asyntask;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.paez_sonia_asyntask.databinding.ActivityMain3Binding;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import static com.example.paez_sonia_asyntask.Conversion.convertirDolares;
import static com.example.paez_sonia_asyntask.Conversion.convertirEuros;

public class MainActivity3 extends AppCompatActivity implements View.OnClickListener{
    private ActivityMain3Binding binding;
    Conversion convert = new Conversion();
    String cambioPasado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        binding = ActivityMain3Binding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);
        String url = "https://dam.org.es/ficheros/rate.txt";
        binding.botonconv.setOnClickListener(this);
        OkHttpClient cliente = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call llamada;
        llamada = cliente.newCall(request);
        llamada.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(MainActivity3.this, "Error en la conexion: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String myresponse = response.body().string();
                MainActivity3.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        cambioPasado = myresponse;
                    }
                });
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v == binding.botonconv) {
            try {
                double cambio= Double.parseDouble(cambioPasado);
                if (binding.eurosDolares.isChecked()) {
                    binding.dolares.setText(convertirDolares(binding.euros.getText().toString(), cambio));
                } else {
                    binding.euros.setText(convertirEuros(binding.dolares.getText().toString(), cambio));
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Error en la conversión: " + e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }
    }

}
