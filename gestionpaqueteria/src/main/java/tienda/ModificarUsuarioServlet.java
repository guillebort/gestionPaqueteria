package tienda;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ModificarUsuarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer codigo = (Integer) session.getAttribute("codigo");
        
        // Seguridad: Si no está logueado, a la calle
        if (codigo == null || codigo <= 0) {
            response.sendRedirect("loginUsuario.jsp");
            return;
        }

        // Recogemos los datos
        String nombre = request.getParameter("nombre");
        String apellidos = request.getParameter("apellidos");
        String domicilio = request.getParameter("domicilio");
        String poblacion = request.getParameter("poblacion");
        String provincia = request.getParameter("provincia");
        String cp = request.getParameter("cp");
        String telefono = request.getParameter("telefono");
        
        String clave1 = request.getParameter("clave1");
        String clave2 = request.getParameter("clave2");

        // Verificación de seguridad extra en Java por si falla el JavaScript
        if (clave1 != null && !clave1.isEmpty() && !clave1.equals(clave2)) {
            session.setAttribute("mensaje", "❌ Las contraseñas no coinciden.");
            response.sendRedirect("usuario.html");
            return;
        }

        AccesoBD con = AccesoBD.getInstance();
        boolean exito = con.modificarUsuarioBD(codigo, clave1, nombre, apellidos, domicilio, poblacion, provincia, cp, telefono);

        if (exito) {
            session.setAttribute("mensaje", "✅ Perfil actualizado correctamente.");
        } else {
            session.setAttribute("mensaje", "❌ Hubo un error al actualizar tus datos.");
        }
        
        response.sendRedirect("usuario.html");
    }
}