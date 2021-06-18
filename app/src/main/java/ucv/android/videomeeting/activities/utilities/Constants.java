package ucv.android.videomeeting.activities.utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECION_USERS = "usuarios";
    public static final String KEY_CARGO = "cargo";
    public static final String KEY_CONTRASEÑA = "contraseña";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_NOMBRE = "nombre";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_FCM_TOKEN = "fcm_token";

    public static final String KEY_PREFERENCE_NAME = "videoMeetingPreference";
    public static final String KEY_IS_SIGNED_IN= "isSignedIn";

    //ahora definiremos los encabezados, uno para la autorizacion y el segundo el contenido
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

    //usaremos otras para enviar personalizados
    public static final String REMOTE_MSG_TYPE= "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    // nos aseguramos de escribir asi para que funcione
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";

    //obtener encabezados de mensajes remotos
    public static HashMap<String, String> getRemoteMessageEncabezados(){
        HashMap<String,String> encabezados = new HashMap<>();
        //es primero es la autorizacion
        encabezados.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                //le pasaremos la clave de firebase
                "key=AAAA0ixlxq8:APA91bGim-3g4CM1rpWTduEaJ15FarkUYGGN6S52QipI2YvkGz7jalNDqhV63MbClsBEiLDbTk974gmcC0KMUhtlzEZs5H0nysiqO5MJ0FwHt7WPnTWbp0wXpo_CYULwAx4rnUNQ3sVM   "
        );
        //el segundo es el contenido
        encabezados.put(Constants.REMOTE_MSG_CONTENT_TYPE, "apliccation/json");
        // y retornamos el mapa de encabezados
        return encabezados;
    }
}
