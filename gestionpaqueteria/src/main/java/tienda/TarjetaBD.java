package tienda;

public class TarjetaBD {
    private int id;
    private int idUsuario;
    private String numero;
    private String titular;
    private String caducidad;

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    
    public String getNumero() { return numero; }
    public void setNumero(String numero) { this.numero = numero; }
    
    public String getTitular() { return titular; }
    public void setTitular(String titular) { this.titular = titular; }
    
    public String getCaducidad() { return caducidad; }
    public void setCaducidad(String caducidad) { this.caducidad = caducidad; }
    
    // Un método extra para que en el HTML se vea bonito: "Visa terminada en ...1234"
    public String getNumeroOculto() {
        if (numero != null && numero.length() >= 4) {
            return "**** **** **** " + numero.substring(numero.length() - 4);
        }
        return numero;
    }
}