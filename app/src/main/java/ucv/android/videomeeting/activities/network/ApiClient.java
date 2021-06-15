package ucv.android.videomeeting.activities.network;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null; //definimos una instancia de actualizacion
    public static  Retrofit getCliente(){ //metodo publico para obtener clientes
        if (retrofit == null){ //verificaremos si la modificacion es nula
            retrofit = new Retrofit.Builder() //la inicializamos a la nueva actualizacion
                    .baseUrl("https://fcm.googleapis.com/fcm/") //usamos retroadaptacion
                    //para enviar mensajes remotos usando la nube de firebase
                    .addConverterFactory(ScalarsConverterFactory.create())//agregamos convertidor
                    //escalar de json para enviar mensajes remotos porque se necesita pasar el
                    // cuerpo de json en la solicitud de red y no tenemos que administrar
                    //la respuesta de red
                    .build(); //contruye el objeto de actualizacion
        }
        return retrofit; //se regresa el objeto de modificacion
    }
}//ya esta lista la api cliente
