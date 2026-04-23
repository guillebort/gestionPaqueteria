package tienda;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ContactoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        String nombre = request.getParameter("nombre");
        String email = request.getParameter("email");
        String asunto = request.getParameter("asunto");
        String mensaje = request.getParameter("mensaje");
        
        AccesoBD con = AccesoBD.getInstance();
        boolean exito = con.guardarMensajeContacto(nombre, email, asunto, mensaje);
        
        HttpSession sesion = request.getSession();
        if (exito) {
            sesion.setAttribute("mensajeContacto", "✅ ¡Mensaje enviado con éxito! Nos pondremos en contacto contigo pronto.");
        } else {
            sesion.setAttribute("mensajeContacto", "❌ Hubo un error al enviar tu mensaje. Por favor, inténtalo de nuevo.");
        }
        
        // Redirigimos a la página
        response.sendRedirect("contacto.jsp");
    }
}