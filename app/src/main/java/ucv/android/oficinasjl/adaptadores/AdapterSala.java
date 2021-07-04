package ucv.android.oficinasjl.adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ucv.android.oficinasjl.R;
import ucv.android.oficinasjl.modelos.Sala;

public class AdapterSala extends BaseAdapter {
    Context context;
    List<Sala> lst;

    public AdapterSala(Context context, List<Sala> lst) {
        this.context = context;
        this.lst = lst;
    }

    @Override
    public int getCount() {
        return lst.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        ImageView img_foto;
        TextView tvt_nombre, tvt_usuario;

        Sala universidad = lst.get(position);

        if(view == null)
            view = LayoutInflater.from(context).inflate(R.layout.adapter_sala, null);

        img_foto    = view.findViewById(R.id.img_foto);
        tvt_nombre  = view.findViewById(R.id.tvt_nombre);
        tvt_usuario = view.findViewById(R.id.tvt_usuario);

        img_foto.setImageResource(universidad.imagen);
        // SEMANA 3 = img_foto.setImageURI();

        tvt_nombre.setText(universidad.nombre);
        tvt_usuario.setText(universidad.nombreUsuario);

        return view;
    }
}
