package ucv.android.videomeeting.activities.firebase;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;

import ucv.android.videomeeting.activities.activities.IncomingInvitationActivity;
import ucv.android.videomeeting.activities.utilities.Constants;

public class MensajeServices extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        //Log.d("FCM","Token: "+token);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //obtener dato de un mensaje remoto
        String type = remoteMessage.getData().get(Constants.REMOTE_MSG_TYPE);

        if (type!=null){
            //si es nulo entonces verificamos si es el tipo es igual a la constante
            if (type.equals(Constants.REMOTE_MSG_INVITATION)){
                // entonces comenzamos la actividad mostrando la actividad de invitacion
                Intent intent = new Intent(getApplicationContext(), IncomingInvitationActivity.class);
                //con esta intencion pasaremos datos desde un mensaje remoto
                intent.putExtra(
                        Constants.REMOTE_MSG_MEETING_TYPE,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_TYPE)
                );
                // y le pasaremos nombre, cargo y correo
                intent.putExtra(
                        Constants.KEY_NOMBRE,
                        remoteMessage.getData().get(Constants.KEY_NOMBRE)
                );
                intent.putExtra(
                        Constants.KEY_CARGO,
                        remoteMessage.getData().get(Constants.KEY_CARGO)
                );
                intent.putExtra(
                        Constants.KEY_EMAIL,
                        remoteMessage.getData().get(Constants.KEY_EMAIL)
                );
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITER_TOKEN,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITER_TOKEN)
                );
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITER_TOKEN,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITER_TOKEN)
                );
                //ya que estamos comenzando una actividad desde un contexto no una actividad
                // necesitaremos marcar cual es la intencion
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //finalmente hara que comienze la actividad con esta intencion
                startActivity(intent);
            }
        }
        //y el metodo de recepcion esta listo
    }
}
