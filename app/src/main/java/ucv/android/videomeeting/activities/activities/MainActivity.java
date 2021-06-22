package ucv.android.videomeeting.activities.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ucv.android.videomeeting.R;
import ucv.android.videomeeting.activities.adapters.UserAdapter;
import ucv.android.videomeeting.activities.listeners.UsersListener;
import ucv.android.videomeeting.activities.models.Usuario;
import ucv.android.videomeeting.activities.utilities.Constants;
import ucv.android.videomeeting.activities.utilities.PreferenceManager;

public class MainActivity extends AppCompatActivity implements UsersListener {

    private PreferenceManager preferenceManager;
    private List<Usuario> usuarios;
    private UserAdapter userAdapter;
    private TextView textErrorMessage;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferenceManager=new PreferenceManager(getApplicationContext());

        TextView textTittle = findViewById(R.id.textTittle);
        textTittle.setText(String.format(
            "%s %s",
            preferenceManager.getString(Constants.KEY_NOMBRE),
            preferenceManager.getString(Constants.KEY_CARGO)
        ));

        findViewById(R.id.textSignOut).setOnClickListener(v -> SingOut());

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<InstanceIdResult> task) {
                if(task.isSuccessful() && task.getResult()!=null) {
                    sendFCMTokenToDataBase(task.getResult().getToken());
                }
            }
        });


        RecyclerView userRecyclerView = findViewById(R.id.usersRecyclerView);
        textErrorMessage = findViewById(R.id.textErrorMessage);

        usuarios=new ArrayList<>();
        userAdapter = new UserAdapter(usuarios, this); //pasamos el oyente al usuario
        userRecyclerView.setAdapter(userAdapter);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::getUsers); //actualizar metodo

        getUsers();
    }

    private void getUsers(){
        swipeRefreshLayout.setRefreshing(true); //actualizar como true
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECION_USERS)
                .get()
                .addOnCompleteListener(task -> {
                    swipeRefreshLayout.setRefreshing(false); //actualizar como true de respuesta
                    String miUserId = preferenceManager.getString(Constants.KEY_USER_ID);
                    if (task.isSuccessful() && task.getResult()!=null){
                        usuarios.clear(); //borrar la lista de usuarios antes de agregar new data
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
                            if (miUserId.equals(documentSnapshot.getId())){
                                //aquí, mostraremos la lista de usuarios excepto el usuario
                                // que ha iniciado sesión actualmente, porque nadie se reunirá
                                //con él mismo. es por eso que excluimos de la lista a
                                // un usuario que inició sesión.
                                continue;
                            }
                            Usuario usuario = new Usuario();
                            usuario.nombre = documentSnapshot.getString(Constants.KEY_NOMBRE);
                            usuario.cargo = documentSnapshot.getString(Constants.KEY_CARGO);
                            usuario.email = documentSnapshot.getString(Constants.KEY_EMAIL);
                            usuario.token = documentSnapshot.getString(Constants.KEY_FCM_TOKEN);
                            usuarios.add(usuario);
                        }
                        if (usuarios.size()>0){
                            userAdapter.notifyDataSetChanged();
                        }else {
                            textErrorMessage.setText(String.format("%s","Usuarios no disponibles"));
                            textErrorMessage.setVisibility(View.VISIBLE);
                        }
                    }else{
                        textErrorMessage.setText(String.format("%s","Usuarios no disponibles"));
                        textErrorMessage.setVisibility(View.VISIBLE);
                    }
                });
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
                    startActivity(new Intent(getApplicationContext(),IniciarSesion.class));
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(MainActivity.this, "No pudo cerrar sesión", Toast.LENGTH_SHORT).show());
    }

    //implementamos metodos oyentes

    @Override
    public void initiateVideoMeeting(Usuario usuario) {
        if(usuario.token == null  || usuario.token.trim().isEmpty()){
            Toast.makeText(this, ""+usuario.nombre + "no habilitado para audio", Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(this, "Video reunion con "+usuario.nombre+ " de "+  usuario.cargo, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),OutgoingInvitationActivity.class);
            // pasamos el objeto directamente con la intencion que se debe a que la clase usuario
            //implementa la interfaz serializable
            intent.putExtra("usuario",usuario);
            intent.putExtra("type","video");
            startActivity(intent);
        }
    }

    @Override
    public void initiateAudioMeeting(Usuario usuario) {
        if(usuario.token == null  || usuario.token.trim().isEmpty()){
            Toast.makeText(this, ""+usuario.nombre + "no habilitado para audio", Toast.LENGTH_SHORT).show();
        }else {
            //Toast.makeText(this, "Audio reunion con "+usuario.nombre+ " de "+  usuario.cargo, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),OutgoingInvitationActivity.class);
            intent.putExtra("usuario",usuario);
            intent.putExtra("type","audio");
            startActivity(intent);
        }
    }
}