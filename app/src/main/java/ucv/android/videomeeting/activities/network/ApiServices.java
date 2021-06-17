package ucv.android.videomeeting.activities.network;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface ApiServices {

    @POST("send") //se envia la url base
    Call<String> sendRemoteMessage( //llamda de actualizacion de tipo de respuesta cadena y el
                                    //metodo para enviar primero el encabezado y segundo el cuerpo
            @HeaderMap HashMap<String, String> headers, //usamos mapa de encabezado porque
                                    //hay varios encabezados para este metodo
            @Body String remoteBody
            );
}//la interfaz de servicio api ya esta lista
