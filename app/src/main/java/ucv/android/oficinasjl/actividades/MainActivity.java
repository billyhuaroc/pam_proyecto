package ucv.android.oficinasjl.actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ucv.android.oficinasjl.R;
import ucv.android.oficinasjl.adaptadores.AdapterSala;
import ucv.android.oficinasjl.modelos.Sala;
import ucv.android.oficinasjl.utilidades.Constants;
import ucv.android.oficinasjl.utilidades.PreferenceManager;

public class MainActivity extends AppCompatActivity{

    private PreferenceManager preferenceManager;
    ListView ListViewSala;
    List<Sala> lst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager = new PreferenceManager(getApplicationContext());

        TextView textTittle = findViewById(R.id.textTittle);
        textTittle.setText(String.format(
            "%s",
            preferenceManager.getString(Constants.KEY_NOMBRE)
        ));

        findViewById(R.id.textSignOut).setOnClickListener(v -> SingOut());

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if(task.isSuccessful() && task.getResult()!=null) {
                    sendFCMTokenToDataBase(task.getResult().getToken());
                }
            }
        });

        ListViewSala = findViewById(R.id.ListViewUniveridad);

        AdapterSala adapter = new AdapterSala(this, GetData());
        ListViewSala.setAdapter(adapter);

        ListViewSala.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Sala sala = lst.get(position);
                Toast.makeText(MainActivity.this, sala.nombre, Toast.LENGTH_SHORT).show();

                if (sala.id == 1){
                    EditText editText = findViewById(R.id.conferenceName);
                    editText.setText("SALA DE ESPERA");
                    String text = editText.getText().toString();
                    if (text.length() > 0) {
                        JitsiMeetConferenceOptions options
                                = new JitsiMeetConferenceOptions.Builder()
                                .setRoom(text)
                                .build();
                        JitsiMeetActivity.launch(MainActivity.this, options);
                    }
                }
                if (sala.id == 2){
                    EditText editText = findViewById(R.id.conferenceName);
                    editText.setText("SALA DE JUEGOS");
                    String text = editText.getText().toString();
                    if (text.length() > 0) {
                        JitsiMeetConferenceOptions options
                                = new JitsiMeetConferenceOptions.Builder()
                                .setRoom(text)
                                .build();
                        JitsiMeetActivity.launch(MainActivity.this, options);
                    }
                }
                if (sala.id == 1){
                    EditText editText = findViewById(R.id.conferenceName);
                    editText.setText("SALA DE TRABAJO");
                    String text = editText.getText().toString();
                    if (text.length() > 0) {
                        JitsiMeetConferenceOptions options
                                = new JitsiMeetConferenceOptions.Builder()
                                .setRoom(text)
                                .build();
                        JitsiMeetActivity.launch(MainActivity.this, options);
                    }
                }
            }
        });

        try {
            // creacion de objeto con JitsiMeetConferenceOptions en ingles
            // el nombre de la clase sera opciones
            JitsiMeetConferenceOptions opciones = new JitsiMeetConferenceOptions.Builder()
                    .setServerURL(new URL(""))
                    .setWelcomePageEnabled(false)
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    private void sendFCMTokenToDataBase(String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference =
            database.collection(Constants.KEY_COLLECION_USERS).document(
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        documentReference.update(Constants.KEY_FCM_TOKEN,token)
                //ya no necesitamos este mensaje de "token actualizado correctamente", así que elimínelo
            //.addOnCompleteListener(task -> Toast.makeText(MainActivity.this,"Token actualizado satisfactoriamente", Toast.LENGTH_SHORT).show())
            .addOnFailureListener(e -> Toast.makeText(MainActivity.this,"Token no enviado!: "+e.getMessage(), Toast.LENGTH_SHORT).show());

    }

    private void SingOut(){
        Toast.makeText(this,"Cerrando sesión...",Toast.LENGTH_SHORT).show();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference=
            database.collection(Constants.KEY_COLLECION_USERS).document(
                    preferenceManager.getString(Constants.KEY_USER_ID)
            );
        HashMap<String, Object> updates=new HashMap<>();
        updates.put(Constants.KEY_FCM_TOKEN, FieldValue.delete());
        documentReference.update(updates)
                .addOnSuccessListener(unused -> {
                    preferenceManager.clearPreference();
                    startActivity(new Intent(getApplicationContext(),IniciarSesion.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "No pudo cerrar sesión", Toast.LENGTH_SHORT).show());
    }

    private List<Sala> GetData() {
        lst = new ArrayList<>();

        lst.add(new Sala(1,R.drawable.sala_espera,"Sala de espera",""));
        lst.add(new Sala(2,R.drawable.sala_juegos, "Sala de juegos",""));
        lst.add(new Sala(3,R.drawable.sala_trabajo, "Sala de trabajo",""));


        /*EditText editText = findViewById(R.id.conferenceName);
        String text = editText.getText().toString();

        if (text == "SALADEESPERA"){
            lst.add(new Sala(1,R.drawable.sala_espera,"Sala de espera",preferenceManager.getString(Constants.KEY_NOMBRE)));
        }
        if (text == "SALADEJUEGOS"){
            lst.add(new Sala(2,R.drawable.sala_espera,"Sala de juegos",preferenceManager.getString(Constants.KEY_NOMBRE)));
        }*/

        return lst;
    }
}