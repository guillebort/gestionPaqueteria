package tienda;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

public class ProductoServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Obtenemos la instancia de AccesoBD y la lista de productos
        AccesoBD bd = AccesoBD.getInstance();
        List<ProductoBD> lista = bd.obtenerProductosBD();
        
        // 2. Guardamos la lista en el "request" para que la JSP pueda verla
        request.setAttribute("listaProductos", lista);
        
        // 3. Delegamos el control a productos.jsp (la Vista)
        RequestDispatcher rd = request.getRequestDispatcher("productos.jsp");
        rd.forward(request, response);
    }
}