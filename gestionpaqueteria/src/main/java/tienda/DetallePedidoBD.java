package tienda;

public class DetallePedidoBD {

    private ProductoBD producto; 
    private int cantidad;
    private double precio; 

    public ProductoBD getProducto() { return producto; }
    public void setProducto(ProductoBD producto) { this.producto = producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    
    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}