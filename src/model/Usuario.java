package model;
public class Usuario {
    private int id;
    private String cedula;
    private String usuario;
    private String contraseña;
    private String correo;
    private String status;

    public Usuario(int id, String cedula, String usuario, String contraseña, String correo, String status) {
        this.id = id;
        this.cedula = cedula;
        this.usuario = usuario;
        this.contraseña = contraseña;
        this.correo = correo;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getCedula() {
        return cedula;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getContraseña() {
        return contraseña;
    }

    public String getCorreo() {
        return correo;
    }

    public String getStatus() {
        return status;
    }
}