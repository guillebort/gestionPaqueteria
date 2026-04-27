package tienda;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class GuardarRutaServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession sesion = request.getSession();

        // 1. Guardamos todo en la "mochila" (Sesión)
        sesion.setAttribute("direccionOrigen", request.getParameter("direccionOrigen"));
        sesion.setAttribute("latOrigen", request.getParameter("latOrigen"));
        sesion.setAttribute("lonOrigen", request.getParameter("lonOrigen"));

        sesion.setAttribute("direccionDestino", request.getParameter("direccionDestino"));
        sesion.setAttribute("latDestino", request.getParameter("latDestino"));
        sesion.setAttribute("lonDestino", request.getParameter("lonDestino"));

        // 2. Ahora sí, le mandamos a la pasarela de pago (que cargará las tarjetas)
        response.sendRedirect("finalizarPedido.html");
    }
}