/*package ucv.android.videomeeting.activities.modelos;

public class Sala {
    public int id;
    public int imagen;
    public Sala(int id, int imagen) {
        this.id = id;
        this.imagen = imagen;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }
}*/
package ucv.android.oficinasjl.modelos;
public class Sala {

    public int id;
    public int imagen;
    public String nombre;
    public String nombreUsuario;

    public Sala(int id, int imagen, String nombre,String nombreUsuario) {
        this.id = id;
        this.imagen = imagen;
        this.nombre = nombre;
        this.nombreUsuario = nombreUsuario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }
}

