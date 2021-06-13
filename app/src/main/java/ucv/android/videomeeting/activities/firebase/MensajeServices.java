package ucv.android.videomeeting.activities.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
import org.jetbrains.annotations.NotNull;

public class MensajeServices extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull @NotNull String token) {
        super.onNewToken(token);
        Log.d("FCM","Token: "+token);
    }

    @Override
    public void onMessageReceived(@NonNull @NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification() != null){
            Log.d(
                    "FCM",
                    "Mensaje remoto recibido: "+ remoteMessage.getNotification().getBody()
            );
        }
    }
}
