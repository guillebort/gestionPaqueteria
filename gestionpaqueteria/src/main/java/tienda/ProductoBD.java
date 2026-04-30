package tienda;
public class ProductoBD {

    private int id;
    private String descripcion;
    private double precio;
    private int existencias;
    private String imagen;
    private String caracteristicas;
    private String colorCss;

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getDescripcion(){
        return this.descripcion;
    }

    public void setDescripcion(String descripcion){
        this.descripcion = descripcion;
    }

    public double getPrecio(){
        return precio;
    }

    public void setPrecio(double precio){
        this.precio = precio;
    }

    public int getExistencias(){
        return existencias;
    }

    public void setExistencias(int existencias){
        this.existencias = existencias;
    }

    public String getImagen(){
        return this.imagen;
    }

    public void setImagen(String imagen){
        this.imagen = imagen;
    }
    
    public String getCaracteristicas() {
    return caracteristicas;
    }

    public void setCaracteristicas(String caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    public String getColorCss() {
        return colorCss;
    }

    public void setColorCss(String colorCss) {
        this.colorCss = colorCss;
    }
}
