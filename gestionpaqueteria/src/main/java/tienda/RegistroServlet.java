package tienda;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class RegistroServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        // 1. Recogemos los datos (¡Importante el UTF-8 para las tildes y las ñ!)
        request.setCharacterEncoding("UTF-8");
        
        String usuario = request.getParameter("usuario");
        String clave = request.getParameter("clave");
        String nombre = request.getParameter("nombre");
        String apellidos = request.getParameter("apellidos");
        String domicilio = request.getParameter("domicilio");
        String poblacion = request.getParameter("poblacion");
        String provincia = request.getParameter("provincia");
        String cp = request.getParameter("cp");
        String telefono = request.getParameter("telefono");

        AccesoBD con = AccesoBD.getInstance();
        
        // 2. LLAMAMOS A TU FUNCIÓN EXACTA DE AccesoBD
        boolean exito = con.registrarUsuarioBD(usuario, clave, nombre, apellidos, domicilio, poblacion, provincia, cp, telefono);

        HttpSession sesion = request.getSession();

        if (exito) {
            // 3. Le iniciamos la sesión del tirón usando tu otra función
            int idNuevo = con.comprobarUsuarioBD(usuario, clave);
            if (idNuevo != -1) {
                sesion.setAttribute("codigo", idNuevo);
            }

            // Leemos si venía del carrito o del menú normal
            String rutaDestino = request.getParameter("url");
            if (rutaDestino == null || rutaDestino.trim().isEmpty()) {
                rutaDestino = "usuario.html"; 
            }
            
            sesion.setAttribute("mensaje", "✅ ¡Cuenta creada! Bienvenido a la tienda.");
            response.sendRedirect(rutaDestino);
            
        } else {
            // Si explota (probablemente email duplicado)
            sesion.setAttribute("mensaje", "❌ Error al registrar. Ese correo ya existe.");
            String rutaOrigen = request.getHeader("referer"); 
            response.sendRedirect(rutaOrigen != null ? rutaOrigen : "usuario.html");
        }
    }
}