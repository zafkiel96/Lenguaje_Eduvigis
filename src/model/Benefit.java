package model;

public class Benefit {
    private String name;
    private String date;
    private String status;
    
    public Benefit(String name, String date, String status) {
        this.name = name;
        this.date = date;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status.toLowerCase().equals("activo") ? "activo" : "inactivo";
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
