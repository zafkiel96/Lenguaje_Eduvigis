package model;

public class Personas {
    private String primerNombre;
    private String primerApellido;
    private int cedula;
    private String status;

    public Personas(String primerNombre, String primerApellido, int cedula, String status) {
        this.primerNombre = primerNombre;
        this.primerApellido = primerApellido;
        this.cedula = cedula;
        this.status = status;
    }

    public String getPrimerNombre() {
        return primerNombre;
    }

    public void setPrimerNombre(String primerNombre) {
        this.primerNombre = primerNombre;
    }

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public int getCedula() {
        return cedula;
    }

    public void setCedula(int cedula) {
        this.cedula = cedula;
    }

    public String getStatus() {
        return status.toLowerCase().equals("activo") ? "activo" : "inactivo";
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
