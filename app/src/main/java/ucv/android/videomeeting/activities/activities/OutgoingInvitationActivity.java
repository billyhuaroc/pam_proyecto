package ucv.android.videomeeting.activities.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.jetbrains.annotations.NotNull;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ucv.android.videomeeting.R;
import ucv.android.videomeeting.activities.models.Usuario;
import ucv.android.videomeeting.activities.network.ApiClient;
import ucv.android.videomeeting.activities.network.ApiServices;
import ucv.android.videomeeting.activities.utilities.Constants;
import ucv.android.videomeeting.activities.utilities.PreferenceManager;

public class OutgoingInvitationActivity extends AppCompatActivity {

    //definimos administrador de preferencias
    private PreferenceManager preferenceManager;
    //y el token del remitente
    private String inviterToken = null;
    String meetingRoom = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_invitation);

        //ahora inicializar ambas
        preferenceManager = new PreferenceManager(getApplicationContext());

        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        //copiamos el nombre de la pagina principal
        String meetingType = getIntent().getStringExtra("type");

        //comprobamos que el tipo de reunion no es nulo
        if (meetingType != null) {
            //verificamos si es audio o video
            if(meetingType.equals("video")){
                //si es video configuramos el icono de video de imagen
                imageMeetingType.setImageResource(R.drawable.ic_video);
            }
        }
        //ahora definimos los detalles del usuario como el nombre y letra
        TextView textFirstChar = findViewById(R.id.textFirstChar);
        TextView textUsername = findViewById(R.id.textUsername);
        TextView textEmail = findViewById(R.id.textCorreo);

        //una intencion donde necesitamos lanzar un objeto de usuario
        Usuario usuario = (Usuario) getIntent().getSerializableExtra("usuario");
        //si el usuario no es nulo
        if (usuario != null){
            //mostraremos la primera letra de su nombre
            textFirstChar.setText(usuario.nombre.substring(0,1));
            //y mostraremos nombre email
            textUsername.setText(String.format("%s %s",usuario.nombre,usuario.cargo));
            textEmail.setText(usuario.email);
        }
        //ahora definimos la vista para cancelar
        ImageView imageStopInvitation = findViewById(R.id.imageStopInvitation);
        imageStopInvitation.setOnClickListener(v -> {
            //acabaremos de regresar de la actividad reciente
            if (usuario!=null){
                cancelInvitation(usuario.token);
            }
        });

        //usaremos firebase y le pasaremos el oyente
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<InstanceIdResult> task) {
                if(task.isSuccessful() && task.getResult()!=null) {
                    inviterToken = task.getResult().getId();
                    if (meetingType!=null || usuario!=null){
                        //llamamos a iniciar reunion y el primer parametro es el tipo
                        initiateMeeting(meetingType,usuario.token); //no se si se vaya a pasar esto
                    }
                }
            }
        });
    }

    // metodo para iniciar la reunion
    public void initiateMeeting(String meetingType, String receiverToken){
        try{
            //creamos un area json
            JSONArray tokens = new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE,meetingType);
            data.put(Constants.KEY_NOMBRE,preferenceManager.getString(Constants.KEY_NOMBRE));
            data.put(Constants.KEY_CARGO,preferenceManager.getString(Constants.KEY_CARGO));
            data.put(Constants.KEY_EMAIL,preferenceManager.getString(Constants.KEY_EMAIL));
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);

            meetingRoom =
                    preferenceManager.getString(Constants.KEY_USER_ID)+ "_" +
                            UUID.randomUUID().toString().substring(0,5);
            data.put(Constants.REMOTE_MSG_MEETING_ROOM,meetingType);

            body.put(Constants.REMOTE_MSG_DATA,data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);

            //preparamos la api para enviar los paremetros
            sendRemoteMessage(body.toString(),Constants.REMOTE_MSG_INVITATION);

        }catch (Exception exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
        //este metodo de inicio de reunion esta completo
    }


    private void sendRemoteMessage(String remoteMessageBody, String type){
        //llamada usando la red retrofit
        ApiClient.getCliente().create(ApiServices.class).sendRemoteMessage(
                Constants.getRemoteMessageEncabezados(),remoteMessageBody
        ).enqueue(new Callback<String>() { //nos dara dos devoluciones
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                //respuesta
                if (response.isSuccessful()){
                    if (type.equals(Constants.REMOTE_MSG_INVITATION)){
                        Toast.makeText(OutgoingInvitationActivity.this, "Invitacion enviada", Toast.LENGTH_SHORT    ).show();
                    }else if (type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){
                        Toast.makeText(OutgoingInvitationActivity.this, "Invitacion cancelada", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else{
                    Toast.makeText(OutgoingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(OutgoingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    private void cancelInvitation(String recibeToken){
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(recibeToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,Constants.REMOTE_MSG_INVITATION_CANCELLED);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);

            sendRemoteMessage(body.toString(),Constants.REMOTE_MSG_INVITATION_RESPONSE);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){
                try {
                    URL serverUrl = new URL("https://meet.jit.si");
                    JitsiMeetConferenceOptions conferenceOptions =
                            new JitsiMeetConferenceOptions.Builder()
                                    .setServerURL(serverUrl)
                                    .setWelcomePageEnabled(false)
                                    .setRoom(meetingRoom)
                                    .build();
                    JitsiMeetActivity.launch(OutgoingInvitationActivity.this,conferenceOptions);
                    finish();
                }catch (Exception e){
                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                //Toast.makeText(context, "Invitacion aceptada", Toast.LENGTH_SHORT).show();
            }else if (type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)){
                Toast.makeText(context, "Invitacion rechazada", Toast.LENGTH_SHORT).show();
                finish();
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