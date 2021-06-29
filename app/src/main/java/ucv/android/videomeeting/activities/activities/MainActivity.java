package ucv.android.videomeeting.activities.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ucv.android.videomeeting.R;
import ucv.android.videomeeting.activities.adapters.UserAdapter;
import ucv.android.videomeeting.activities.listeners.UsersListener;
import ucv.android.videomeeting.activities.models.Usuario;
import ucv.android.videomeeting.activities.network.ApiClient;
import ucv.android.videomeeting.activities.network.ApiServices;
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
                            //usuario.email = documentSnapshot.getString(Constants.KEY_EMAIL);
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
                    preferenceManager.clearPreference();
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
            //Intent intent = new Intent(getApplicationContext(),OutgoingInvitationActivity.class);
            // pasamos el objeto directamente con la intencion que se debe a que la clase usuario
            //implementa la interfaz serializable
            /*intent.putExtra("usuario",usuario);
            intent.putExtra("type","video");
            startActivity(intent);*/
            Toast.makeText(this, "Ok", Toast.LENGTH_SHORT).show();
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

    private void sendRemoteMessage(String remoteMessageBody){
        //llamada usando la red retrofit
        ApiClient.getCliente().create(ApiServices.class).sendRemoteMessage(
                Constants.getRemoteMessageEncabezados(),remoteMessageBody
        ).enqueue(new Callback<String>() { //nos dara dos devoluciones
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                //respuesta
                if (response.isSuccessful()){
                    Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    //finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type!=null){
                if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){
                    //Toast.makeText(context, "Invitacion aceptada", Toast.LENGTH_SHORT).show();
                    try {
                        URL serverUrl = new URL("https://meet.jit.si");

                        JitsiMeetConferenceOptions.Builder builder =
                                new JitsiMeetConferenceOptions.Builder();
                        builder.setServerURL(serverUrl);
                        builder.setWelcomePageEnabled(false);
                        builder.setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM));
                        JitsiMeetActivity.launch(MainActivity.this,builder.build());
                        finish();
                    }catch (Exception e){
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)){
                    Toast.makeText(context, "Invitacion rechazada", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }
}