package tienda;

public class Direccion {
    private int id;
    private String textoDireccion;
    private double latitud;
    private double longitud;

    // Constructor vacío
    public Direccion() {}

    // Constructor con datos
    public Direccion(int id, String textoDireccion, double latitud, double longitud) {
        this.id = id;
        this.textoDireccion = textoDireccion;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTextoDireccion() { return textoDireccion; }
    public void setTextoDireccion(String textoDireccion) { this.textoDireccion = textoDireccion; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
}