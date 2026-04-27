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

        // 1. Seguridad: Si no hay usuario o carrito, fuera
        if (idUsuario == null || sesion.getAttribute("carritoJSON") == null) {
            response.sendRedirect("productos.html");
            return;
        }

        ArrayList<ProductoCarrito> carrito = (ArrayList<ProductoCarrito>) sesion.getAttribute("carritoJSON");
        Float total = (Float) sesion.getAttribute("totalPedido");

        // 2. Captura ROBUSTA de coordenadas (para que no dé error al tramitar)
        double latitud = 0.0;
        double longitud = 0.0;
        try {
            String sLat = request.getParameter("latitud");
            String sLon = request.getParameter("longitud");
            if (sLat != null && !sLat.trim().isEmpty()) {
                latitud = Double.parseDouble(sLat.replace(",", "."));
            }
            if (sLon != null && !sLon.trim().isEmpty()) {
                longitud = Double.parseDouble(sLon.replace(",", "."));
            }
        } catch (Exception e) {
            System.out.println("⚠️ Coordenadas no válidas o vacías, usando 0.0");
        }

        // 3. Guardar en BD
        AccesoBD con = AccesoBD.getInstance();
        int idNuevoPedido = con.guardarPedidoCompleto(idUsuario, total, carrito, latitud, longitud);

        // 4. Lógica de Redirección (CORREGIDA)
        if (idNuevoPedido > 0) {
            // --- ÉXITO ---
            
            // Guardar tarjeta si el usuario lo pidió
            String quiereGuardar = request.getParameter("guardarTarjetaCheck");
            String tarjetaElegida = request.getParameter("tarjetaGuardada");
            if ("SI".equals(quiereGuardar) && (tarjetaElegida == null || tarjetaElegida.equals("NUEVA"))) {
                String num = request.getParameter("numeroTarjeta");
                String tit = request.getParameter("titularTarjeta");
                String cad = request.getParameter("caducidadTarjeta");
                if (num != null && !num.trim().isEmpty()) {
                    con.guardarTarjeta(idUsuario, num, tit, cad);
                }
            }

            // Limpiar carrito
            sesion.removeAttribute("carritoJSON");
            sesion.removeAttribute("totalPedido");
            
            // Crear mensaje con referencia
            String referencia = "REF-LOGIS-" + idNuevoPedido;
            sesion.setAttribute("mensaje", referencia); // Guardamos solo la REF para el JSP nuevo
            
            // REDIRECCIÓN CORRECTA AL ÉXITO
            response.sendRedirect("pedidoCompletado.jsp");

        } else {
            // --- ERROR ---
            System.out.println("❌ ERROR: El pedido no se pudo guardar en la BD.");
            sesion.setAttribute("mensaje", "❌ Error al tramitar el pedido. Revisa el stock o los datos de pago.");
            
            // Si hay error, lo mandamos de vuelta a la pasarela de pago para que lo intente de nuevo
            response.sendRedirect("finalizarPedido.html");
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