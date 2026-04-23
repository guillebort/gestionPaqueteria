package tienda;

import java.io.IOException;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ConfirmarPedidoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession sesion = request.getSession();
        Integer codigoUsuario = (Integer) sesion.getAttribute("codigo");
        
        @SuppressWarnings("unchecked")
        ArrayList<ProductoCarrito> carrito = (ArrayList<ProductoCarrito>) sesion.getAttribute("carritoJSON");
        Float total = (Float) sesion.getAttribute("totalPedido");

        // Medidas de seguridad por si alguien recarga la página
        if (codigoUsuario == null || carrito == null || carrito.isEmpty()) {
            response.sendRedirect("carrito.html");
            return;
        }

        // Llamamos directamente a la BD sin recoger parámetros innecesarios del formulario
        AccesoBD con = AccesoBD.getInstance();
        int exito = con.guardarPedidoCompleto(codigoUsuario, total, carrito);

        if (exito != -1) {
            // BORRAMOS EL CARRITO DE LA SESIÓN DE JAVA PORQUE YA ESTÁ COMPRADO
            sesion.removeAttribute("carritoJSON");
            sesion.removeAttribute("totalPedido");
            
            // Le mandamos a la página de victoria
            response.sendRedirect("pedidoCompletado.jsp");
        } else {
            // Si hubo un error en la base de datos
            sesion.setAttribute("mensaje", "❌ Hubo un error al procesar tu pago. Inténtalo de nuevo.");
            response.sendRedirect("procesarPedido.jsp");
        }
    }
}