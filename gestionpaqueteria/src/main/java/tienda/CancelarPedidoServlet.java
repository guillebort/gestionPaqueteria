package tienda;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class CancelarPedidoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession sesion = request.getSession();
        Integer idUsuario = (Integer) sesion.getAttribute("codigo");

        // Seguridad: Si no hay sesión, fuera
        if (idUsuario == null) {
            response.sendRedirect("login.html");
            return;
        }

        try {
            // Recogemos el ID del pedido que nos mandan por la URL
            int idPedido = Integer.parseInt(request.getParameter("id"));
            
            AccesoBD con = AccesoBD.getInstance();
            boolean exito = con.cancelarPedido(idPedido, idUsuario);

            if (exito) {
                sesion.setAttribute("mensaje", "✅ Pedido #" + idPedido + " cancelado. El stock ha sido devuelto.");
            } else {
                sesion.setAttribute("mensaje", "❌ No se pudo cancelar el pedido. (Quizás ya está enviado).");
            }

        } catch (Exception e) {
            sesion.setAttribute("mensaje", "❌ Error técnico al intentar cancelar.");
        }

        // Siempre volvemos al perfil
        response.sendRedirect("usuario.html");
    }
}