package tienda;

import java.io.IOException;
import java.util.ArrayList;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class FinalizarPedidoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        HttpSession sesion = request.getSession();
        Integer idUsuario = (Integer) sesion.getAttribute("codigo");

        // 1. Seguridad básica: Si no hay usuario logueado o no hay carrito, a la calle
        if (idUsuario == null || sesion.getAttribute("carritoJSON") == null) {
            response.sendRedirect("productos.jsp");
            return;
        }

        // 2. Recuperamos los datos de la sesión
        ArrayList<ProductoCarrito> carrito = (ArrayList<ProductoCarrito>) sesion.getAttribute("carritoJSON");
        Float total = (Float) sesion.getAttribute("totalPedido");

        // 3. Llamamos a tu función de la BD (que ahora devuelve el ID del pedido en lugar de true/false)
        AccesoBD con = AccesoBD.getInstance();
        int idNuevoPedido = con.guardarPedidoCompleto(idUsuario, total, carrito);

        System.out.println("=== TEST FINALIZAR PEDIDO ===");
        System.out.println("ID devuelto por la base de datos: " + idNuevoPedido);
        
        // 4. Comprobamos si ha ido bien
        if (idNuevoPedido > 0) {

            // 🚀 --- INICIO MAGIA PUNTO VERDE: GUARDAR TARJETA ---
            String tarjetaElegida = request.getParameter("tarjetaGuardada");
            String quiereGuardar = request.getParameter("guardarTarjetaCheck");

            // Si es una tarjeta NUEVA y ha marcado el check de guardar...
            if ("SI".equals(quiereGuardar) && (tarjetaElegida == null || tarjetaElegida.equals("NUEVA"))) {
                String num = request.getParameter("numeroTarjeta");
                String tit = request.getParameter("titularTarjeta");
                String cad = request.getParameter("caducidadTarjeta");

                // Doble comprobación por seguridad de que el número no está vacío
                if (num != null && !num.trim().isEmpty()) {
                    boolean guardada = con.guardarTarjeta(idUsuario, num, tit, cad);
                    if (guardada) {
                        System.out.println("DEBUG: Tarjeta " + num + " guardada en BD para el usuario " + idUsuario);
                    } else {
                        System.out.println("DEBUG: Fallo al intentar guardar la tarjeta en la BD.");
                    }
                }
            }
            // 🚀 --- FIN MAGIA PUNTO VERDE ---

            // Borramos el carrito de la memoria de Java
            sesion.removeAttribute("carritoJSON");
            sesion.removeAttribute("totalPedido");
            
            // Generamos la referencia chula
            String referencia = "REF-LOGIS-" + idNuevoPedido;
            sesion.setAttribute("mensaje", "✅ ¡Pago aceptado! Tu código de referencia es: <b>" + referencia + "</b>. Ya puedes verlo en tu historial.");
            
            // Le mandamos al perfil para que vea su pedido (OJO: he cambiado a .jsp por si tienes tu lógica ahí)
            response.sendRedirect("usuario.jsp");
        } else {
            System.out.println("ENTRA POR EL CAMINO MALO (ERROR)");
            // Si hubo algún error (falta de stock, fallo de BD...)
            sesion.setAttribute("mensaje", "❌ Error al tramitar el pedido. Por favor, inténtalo de nuevo.");
            response.sendRedirect("procesarPedido.jsp");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession sesion = request.getSession();
        Integer idUsuario = (Integer) sesion.getAttribute("codigo");

        // 1. Si está logueado, le buscamos las tarjetas en la Base de Datos
        if (idUsuario != null) {
            AccesoBD con = AccesoBD.getInstance();
            ArrayList<TarjetaBD> listaTarjetas = con.obtenerTarjetasUsuario(idUsuario);
            
            // 2. Guardamos la lista en el request para que el JSP pueda leerla
            request.setAttribute("misTarjetas", listaTarjetas);
        }

        // 3. Redirigimos al JSP para que pinte la pantalla
        request.getRequestDispatcher("procesarPedido.jsp").forward(request, response);
    }
}