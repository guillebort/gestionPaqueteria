package admin;

import tienda.AccesoBD;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AsignarRutaServlet extends HttpServlet {
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        
        // Control de acceso: Verificar que el usuario logueado es Administrador
        // En tu tabla usuarios tienes el campo 'admin' (int)[cite: 3]
        Integer esAdmin = (Integer) session.getAttribute("admin");
        if (esAdmin == null || esAdmin != 1) {
            response.sendRedirect("admin/login.html");
            return;
        }

        // Recogemos los datos del formulario
        String idPedidoStr = request.getParameter("id_pedido");
        String idRepartidorStr = request.getParameter("id_repartidor");

        if (idPedidoStr != null && idRepartidorStr != null) {
            int idPedido = Integer.parseInt(idPedidoStr);
            int idRepartidor = Integer.parseInt(idRepartidorStr);
            
            AccesoBD con = AccesoBD.getInstance();
            
            // Llamamos a la BD para asignar el repartidor y cambiar el estado a 2 (Enviado)
            boolean exito = con.asignarRepartidor(idPedido, idRepartidor, 2);
            
            if (exito) {
                session.setAttribute("mensaje", "Ruta asignada correctamente al repartidor.");
            } else {
                session.setAttribute("mensaje", "Error al asignar la ruta.");
            }
        }

        // Redirigimos de vuelta al panel para que siga asignando
        response.sendRedirect("CargarPanelAdminServlet");
    }
}