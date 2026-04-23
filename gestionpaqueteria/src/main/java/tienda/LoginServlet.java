package tienda;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String usuario = request.getParameter("usuario");
        String clave = request.getParameter("clave");
        String urlDestino = request.getParameter("url"); // esto viene como "miCuenta.jsp"
        String tipoAcceso = request.getParameter("tipoAcceso");
        
        HttpSession session = request.getSession(true);
        AccesoBD con = AccesoBD.getInstance();
        
        if ("Acceso".equals(tipoAcceso)) {
            int codigo = con.comprobarUsuarioBD(usuario, clave);
            if (codigo > 0) {
                // Todo OK -> Guardamos código y lo mandamos a miCuenta.jsp
                UsuarioBD u = con.obtenerUsuarioBD(codigo);
                session.setAttribute("codigo", codigo);
                session.setAttribute("nombreUsuario", u.getNombre());
                response.sendRedirect(urlDestino);
            } else {
                // Falla clave -> Lo mandamos de vuelta al Login con error
                session.setAttribute("mensaje", "⚠️ Usuario o contraseña incorrectos.");
                response.sendRedirect("loginUsuario.jsp");
            }
            
        } else if ("Registro".equals(tipoAcceso)) {
            String nombre = request.getParameter("nombre");
            String apellidos = request.getParameter("apellidos");
            String domicilio = request.getParameter("domicilio");
            String poblacion = request.getParameter("poblacion");
            String provincia = request.getParameter("provincia");
            String cp = request.getParameter("cp");
            String telefono = request.getParameter("telefono");
            
            boolean exito = con.registrarUsuarioBD(usuario, clave, nombre, apellidos, domicilio, poblacion, provincia, cp, telefono);
            
            if (exito) {
                // Si el registro va bien, lo logueamos directamente y lo mandamos a miCuenta.jsp
                int codigoNuevo = con.comprobarUsuarioBD(usuario, clave);
                session.setAttribute("codigo", codigoNuevo);
                session.setAttribute("nombreUsuario", nombre);
                response.sendRedirect(urlDestino);
            } else {
                // Si falla, de vuelta al login con error
                session.setAttribute("mensaje", "❌ Error al registrar: Revisa los datos o el correo.");
                response.sendRedirect("loginUsuario.jsp");
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.getRequestDispatcher("loginUsuario.jsp").forward(request, response);
    }
}