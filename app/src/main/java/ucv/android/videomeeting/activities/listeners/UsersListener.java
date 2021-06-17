package ucv.android.videomeeting.activities.listeners;


import ucv.android.videomeeting.activities.models.Usuario;

public interface UsersListener { //para iniciar una videoconferencia con cualquier
    //usuario usaremos este oyente

    //definiremos 2 metodos, uno es para videoconferencia y otro es para audio
    void initiateVideoMeeting(Usuario usuario); //tomara el objeto usuario

    void initiateAudioMeeting(Usuario usuario); //para ser escuchado

}//la interfaz de oyente ya esta lista
