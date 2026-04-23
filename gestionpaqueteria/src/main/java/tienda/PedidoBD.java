package tienda;

import java.util.List;
import java.util.ArrayList;
import java.sql.Date;

public class PedidoBD {
    private int id;
    private int idUsuario;
    private Date fecha;
    private double importeTotal;
    private String estado;
    private List<DetallePedidoBD> detalles = new ArrayList<>();

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }
    
    public double getImporteTotal() { return importeTotal; }
    public void setImporteTotal(double importeTotal) { this.importeTotal = importeTotal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<DetallePedidoBD> getDetalles() { return detalles; }
    public void setDetalles(List<DetallePedidoBD> detalles) { this.detalles = detalles; }
}