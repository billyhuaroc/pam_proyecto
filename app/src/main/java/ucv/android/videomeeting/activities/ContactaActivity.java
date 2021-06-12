package ucv.android.videomeeting.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ucv.android.videomeeting.R;

public class ContactaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacta);

        findViewById(R.id.imageBack).setOnClickListener(v -> onBackPressed());
    }
}