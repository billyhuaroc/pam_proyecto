package ucv.android.oficinasjl.firebase;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.annotation.NonNull;
public class MensajeToken extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM","Token: "+token);
    }
    @Override
    public void onMessageReceived(@NonNull RemoteMessage mensajeRemoto) {
        super.onMessageReceived(mensajeRemoto);
        Log.d("FCM","Token: "+mensajeRemoto);
    }
}
