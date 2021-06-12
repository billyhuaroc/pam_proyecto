package ucv.android.videomeeting.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ucv.android.videomeeting.R;

public class IniciarSesion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        findViewById(R.id.contacta_aqui).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),ContactaActivity.class)));
    }
}