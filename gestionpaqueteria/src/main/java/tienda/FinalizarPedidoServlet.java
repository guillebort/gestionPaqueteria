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

        if (idUsuario == null || sesion.getAttribute("carritoJSON") == null) {
            response.sendRedirect("productos.html");
            return;
        }

        ArrayList<ProductoCarrito> carrito = (ArrayList<ProductoCarrito>) sesion.getAttribute("carritoJSON");
        Float total = (Float) sesion.getAttribute("totalPedido");

        // 1. RECUPERAR LAS RUTAS DE LA SESIÓN (Guardadas en el paso anterior)
        String dirOrigen = (String) sesion.getAttribute("direccionOrigen");
        double latOrigen = Double.parseDouble((String) sesion.getAttribute("latOrigen"));
        double lonOrigen = Double.parseDouble((String) sesion.getAttribute("lonOrigen"));

        String dirDestino = (String) sesion.getAttribute("direccionDestino");
        double latDestino = Double.parseDouble((String) sesion.getAttribute("latDestino"));
        double lonDestino = Double.parseDouble((String) sesion.getAttribute("lonDestino"));

        // 2. Guardar en BD con todos los datos correctos
        AccesoBD con = AccesoBD.getInstance();
        int idNuevoPedido = con.guardarPedido(idUsuario, total, carrito, 
                                                      dirOrigen, latOrigen, lonOrigen, 
                                                      dirDestino, latDestino, lonDestino);

        if (idNuevoPedido > 0) {
            // Guardar tarjeta
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

            // 3. LIMPIAR SESIÓN COMPLETA
            sesion.removeAttribute("carritoJSON");
            sesion.removeAttribute("totalPedido");
            sesion.removeAttribute("direccionOrigen");
            sesion.removeAttribute("latOrigen");
            sesion.removeAttribute("lonOrigen");
            sesion.removeAttribute("direccionDestino");
            sesion.removeAttribute("latDestino");
            sesion.removeAttribute("lonDestino");

            sesion.setAttribute("mensaje", "REF-LOGIS-" + idNuevoPedido);
            response.sendRedirect("pedidoCompletado.jsp");
        } else {
            sesion.setAttribute("mensaje", "❌ Error al tramitar el pedido. Revisa el stock.");
            response.sendRedirect("finalizarPedido.html");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession sesion = request.getSession();
        Integer idUsuario = (Integer) sesion.getAttribute("codigo");
        if (idUsuario != null) {
            AccesoBD con = AccesoBD.getInstance();
            ArrayList<TarjetaBD> listaTarjetas = con.obtenerTarjetasUsuario(idUsuario);
            request.setAttribute("misTarjetas", listaTarjetas);
        }
        request.getRequestDispatcher("procesarPedido.jsp").forward(request, response);
    }
}