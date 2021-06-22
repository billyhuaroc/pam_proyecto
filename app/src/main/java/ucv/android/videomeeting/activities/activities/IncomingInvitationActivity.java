package ucv.android.videomeeting.activities.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ucv.android.videomeeting.R;
import ucv.android.videomeeting.activities.network.ApiClient;
import ucv.android.videomeeting.activities.network.ApiServices;
import ucv.android.videomeeting.activities.utilities.Constants;

public class IncomingInvitationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_invitation);

        //definimos una vista de imagen para el tipo de reunion
        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        // y ahora el tipo de reunion con la intencion de obtener una cadena adicional
        String meetingType = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);
        //aqui estamos obteniendo el mensaje obtenido del servicio

        if (meetingType!=null){
            if (meetingType.equals("video")){
                imageMeetingType.setImageResource(R.drawable.ic_video);
            } // si el tipo no es nulo y el tipo de reunion es video configuramos en video el icono
        }
        //definimos las vistas para el primer caracter, nombre y el correo
        TextView textFirstChar = findViewById(R.id.textFirstChar);
        TextView textUsername = findViewById(R.id.textUsername);
        TextView textCorreo = findViewById(R.id.textCorreo);

        // para obtener el nombre de la intencion
        String nombre = getIntent().getStringExtra(Constants.KEY_NOMBRE);
        if (nombre != null){
            //si nombre no es nulo podremos establecer el primer caracter
            textFirstChar.setText(nombre.substring(0,1));
        }
        // ahora configuramos el nombre de usuario y el correo
        textUsername.setText(String.format(
                "%s %s",
                nombre,
                getIntent().getStringExtra(Constants.KEY_CARGO)
        ));

        textCorreo.setText(getIntent().getStringExtra(Constants.KEY_EMAIL));

        ImageView imageAcceptInvitation = findViewById(R.id.imageAcceptInvitation);
        imageAcceptInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitationResponse(
                        Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                        getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)
                );
            }
        });
        ImageView imageRejectInvitation = findViewById(R.id.imageRejectInvitation);
        imageRejectInvitation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitationResponse(
                        Constants.REMOTE_MSG_INVITATION_REJECTED,
                        getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)
                );
            }
        });
    }

    private void sendInvitationResponse(String type, String recibeToken){
        try {
            JSONArray tokens = new JSONArray();
            tokens.put(recibeToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE,Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE,type);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS,tokens);

            sendRemoteMessage(body.toString(),type);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
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
                    if (type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){
                        try {
                            URL serverUrl = new URL("https://meet.jit.si");
                            JitsiMeetConferenceOptions conferenceOptions =
                                    new JitsiMeetConferenceOptions.Builder()
                                    .setServerURL(serverUrl)
                                    .setWelcomePageEnabled(false)
                                    .setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_ROOM))
                                    .build();
                            JitsiMeetActivity.launch(IncomingInvitationActivity.this,conferenceOptions);
                            finish();

                        }catch (Exception e){
                            Toast.makeText(IncomingInvitationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        //Toast.makeText(IncomingInvitationActivity.this, "Invitacion aceptada", Toast.LENGTH_SHORT    ).show();
                    }
                    else{
                        Toast.makeText(IncomingInvitationActivity.this, "Invitacion rechazada", Toast.LENGTH_SHORT    ).show();
                        finish();
                    }
                    /*if (type.equals(Constants.REMOTE_MSG_INVITATION)){
                        Toast.makeText(IncomingInvitationActivity.this, "Invitacion enviada", Toast.LENGTH_SHORT    ).show();
                    }*/
                }else{
                    Toast.makeText(IncomingInvitationActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Toast.makeText(IncomingInvitationActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if (type!=null){
                if (type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)) {
                    Toast.makeText(context, "Invitacion cancelada", Toast.LENGTH_SHORT).show();
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
    //este metodo de reunion entrante esta completo
}