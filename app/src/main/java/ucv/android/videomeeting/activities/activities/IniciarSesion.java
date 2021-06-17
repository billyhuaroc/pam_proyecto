package ucv.android.videomeeting.activities.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import ucv.android.videomeeting.R;
import ucv.android.videomeeting.activities.utilities.Constants;
import ucv.android.videomeeting.activities.utilities.PreferenceManager;

public class IniciarSesion extends AppCompatActivity {

    private EditText inputEmail, inputContraseña;
    private MaterialButton btnSignIn;
    private ProgressBar signInProgressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);

        findViewById(R.id.contacta_aqui).setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), ContactaActivity.class)));

        preferenceManager = new PreferenceManager(getApplicationContext());
        signInProgressBar = findViewById(R.id.signInProgressBar);

        inputEmail = findViewById(R.id.inputEmail);
        inputContraseña = findViewById(R.id.inputContraseña);
        btnSignIn = findViewById(R.id.btnSignIn);

        btnSignIn.setOnClickListener(view -> {
            if (inputEmail.getText().toString().trim().isEmpty()) {
                Toast.makeText(IniciarSesion.this, "Falta el email", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                Toast.makeText(IniciarSesion.this, "Falta validar email", Toast.LENGTH_SHORT).show();
            } else if (inputContraseña.getText().toString().trim().isEmpty()) {
                Toast.makeText(IniciarSesion.this, "Falta contraseña", Toast.LENGTH_SHORT).show();
            } else {
                signIn();
            }
        });
    }

    private void signIn() {
        btnSignIn.setVisibility(View.INVISIBLE);
        signInProgressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_CONTRASEÑA,inputContraseña.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() !=null && task.getResult().getDocuments().size()>0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN,true);
                        preferenceManager.putString(Constants.KEY_USER_ID,documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_NOMBRE,documentSnapshot.getString(Constants.KEY_NOMBRE));
                        preferenceManager.putString(Constants.KEY_CARGO,documentSnapshot.getString(Constants.KEY_CARGO));
                        preferenceManager.putString(Constants.KEY_EMAIL,documentSnapshot.getString(Constants.KEY_EMAIL));

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    }else {
                        signInProgressBar.setVisibility(View.INVISIBLE);
                        btnSignIn.setVisibility(View.VISIBLE);
                        Toast.makeText(IniciarSesion.this,"Incapaz de acceder",Toast.LENGTH_SHORT).show();
                    }
                });
    }
}