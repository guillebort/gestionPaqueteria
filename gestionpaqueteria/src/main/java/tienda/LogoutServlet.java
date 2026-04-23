package tienda;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Destruimos la sesión en JAVA (el servidor olvida al usuario)
        HttpSession sesion = request.getSession(false);
        if (sesion != null) {
            sesion.invalidate(); 
        }

        // 2. Cargamos el "Puente Destructor" para limpiar el navegador
        request.getRequestDispatcher("limpiarCarrito.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}