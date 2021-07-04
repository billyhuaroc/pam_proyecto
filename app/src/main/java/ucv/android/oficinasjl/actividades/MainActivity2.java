package ucv.android.oficinasjl.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ucv.android.oficinasjl.R;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        findViewById(R.id.imageBack).setOnClickListener(v -> onBackPressed());
    }
}