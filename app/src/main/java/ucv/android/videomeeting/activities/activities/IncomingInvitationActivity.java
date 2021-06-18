package ucv.android.videomeeting.activities.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import ucv.android.videomeeting.R;
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
    }//este metodo de reunion entrante esta completo
}