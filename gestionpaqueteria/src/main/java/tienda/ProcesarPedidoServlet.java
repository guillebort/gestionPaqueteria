package tienda;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class ProcesarPedidoServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession(true);
        float totalPedido = 0.0f;

        // 1. Limpiamos el carrito anterior por si hay basura en la sesión
        if (sesion.getAttribute("carritoJSON") != null) {
            sesion.removeAttribute("carritoJSON");
        }

        ArrayList<ProductoCarrito> carritoJSON = new ArrayList<ProductoCarrito>();
        AccesoBD con = AccesoBD.getInstance();

        // 2. LEEMOS EL CARRITO PRIMERO (Esté logueado o no, para no perder los datos)
        try (JsonReader jsonReader = Json.createReader(new InputStreamReader(request.getInputStream(), "utf-8"))) {
            JsonArray jobj = jsonReader.readArray();

            for (int i = 0; i < jobj.size(); i++) {
                JsonObject prod = jobj.getJsonObject(i);
                ProductoCarrito nuevo = new ProductoCarrito();

                nuevo.setCodigo(prod.getInt("codigo"));
                nuevo.setDescripcion(prod.getString("descripcion"));               
                nuevo.setPrecio(Float.parseFloat(prod.get("precio").toString()));

                int cantidad = prod.getInt("cantidad");
                
                // Consultamos el stock REAL en la base de datos
                int existencias = con.obtenerExistencias(nuevo.getCodigo());

                if (cantidad > existencias) {
                    cantidad = existencias; // Ajustamos al máximo posible
                }
                
                if (cantidad > 0) {
                    nuevo.setCantidad(cantidad);
                    carritoJSON.add(nuevo);
                    totalPedido += (nuevo.getPrecio() * cantidad);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 3. PROCESAMOS EL DESTINO SEGÚN EL LOGIN
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        if (carritoJSON.size() > 0) {
            sesion.setAttribute("carritoJSON", carritoJSON);
            sesion.setAttribute("totalPedido", totalPedido);

            Integer codigoUsuario = (Integer) sesion.getAttribute("codigo");
            String urlDestino;

            if (codigoUsuario == null || codigoUsuario <= 0) {
                // Si no está logueado, lo mandamos al login y que luego vuelva al proceso
                urlDestino = "loginTienda.jsp?url=finalizarPedido.html";
            } else {
                // Si está logueado, lo mandamos al Servlet de Finalizar (para que cargue las tarjetas)
                urlDestino = "datosEnvio.jsp";
            }
            
            // Devolvemos el JSON que el JS espera leer
            response.getWriter().write("{\"status\": \"ok\", \"redirect\": \"" + urlDestino + "\"}");

        } else {
            response.getWriter().write("{\"status\": \"error\", \"message\": \"Carrito vacío o sin stock.\"}");
        }
    }
}