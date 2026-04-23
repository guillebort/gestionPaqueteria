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
        if (carritoJSON.size() > 0) {
            // Guardamos el carrito y el total en la sesión (ya están a salvo)
            sesion.setAttribute("carritoJSON", carritoJSON);
            sesion.setAttribute("totalPedido", totalPedido);

            // AHORA comprobamos la seguridad
            Integer codigoUsuario = (Integer) sesion.getAttribute("codigo");

            if (codigoUsuario == null || codigoUsuario <= 0) {
                // NO ESTÁ LOGUEADO: Lo mandamos al login, pero con el billete directo a la pasarela
                response.sendRedirect("loginTienda.jsp?url=procesarPedido.jsp");
            } else {
                // SÍ ESTÁ LOGUEADO: Pasa directo a formalizar el pedido
                RequestDispatcher rd = request.getRequestDispatcher("procesarPedido.jsp");
                rd.forward(request, response);
            }
            
        } else {
            // Si llega vacío o no había stock de nada
            sesion.setAttribute("mensaje", "⚠️ No se ha podido procesar el pedido por falta de stock.");
            response.sendRedirect("procesarPedido.jsp");
        }
    }
}