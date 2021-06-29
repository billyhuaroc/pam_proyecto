package ucv.android.videomeeting.activities.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ucv.android.videomeeting.R;
import ucv.android.videomeeting.activities.listeners.UsersListener;
import ucv.android.videomeeting.activities.models.Usuario;

public class UserAdapter extends  RecyclerView.Adapter<UserAdapter.UserViewHolder>{

    private List<Usuario> usuarios;
    private UsersListener usersListener; //oyente definido

    public  UserAdapter(List<Usuario> usuarios, UsersListener usersListener){
        this.usuarios = usuarios;
        this.usersListener = usersListener;//oyente inicializado
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new UserViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.sala_item,
                        parent,
                        false
                )
        );
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserAdapter.UserViewHolder holder, int position) {
        holder.setUserData(usuarios.get(position));
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{//es clase para poder acceder al oyente

        ImageView salaTipo;
        //TextView textFirstChar, textUsername, textEmail;
        //ImageView imageAudioMeeting, imageVideoMeeting;

        UserViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            salaTipo = itemView.findViewById(R.id.salaTipo);
            /*textFirstChar = itemView.findViewById(R.id.textFirstChar);
            textUsername = itemView.findViewById(R.id.textUsername);
            textEmail = itemView.findViewById(R.id.textEmail);
            imageAudioMeeting = itemView.findViewById(R.id.imageAudioMeeting);
            imageVideoMeeting = itemView.findViewById(R.id.imageVideoMeeting);*/
        }
        void setUserData(Usuario usuario){
            salaTipo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    usersListener.initiateVideoMeeting(usuario);
                }
            });
            /*textFirstChar.setText(usuario.nombre.substring(0, 1));
            textUsername.setText(String.format("%s %s", usuario.nombre,usuario.cargo));
            textEmail.setText(usuario.email);
            imageVideoMeeting.setOnClickListener(v -> usersListener.initiateVideoMeeting(usuario));
            imageAudioMeeting.setOnClickListener(v -> usersListener.initiateAudioMeeting(usuario));*/
        }
    }
}//nuestra clase adaptader esta lista
