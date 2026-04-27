<%@page language="java" contentType="text/html; charset=UTF-8" import="java.util.*, tienda.*" pageEncoding="UTF-8"%>
<%
    // Recuperamos la lista usando tu clase ProductoCarrito
    ArrayList<ProductoCarrito> listaCarrito = (ArrayList<ProductoCarrito>) session.getAttribute("carritoJSON");
    Float total = (Float) session.getAttribute("totalPedido");
    Integer idUsu = (Integer) session.getAttribute("codigo");

    if (listaCarrito == null || listaCarrito.isEmpty()) {
        response.sendRedirect("carrito.jsp");
        return;
    }

    ArrayList<TarjetaBD> misTarjetas = (ArrayList<TarjetaBD>) request.getAttribute("misTarjetas");
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Checkout Seguro - LogisTFG</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body class="bg-light">

    <mi-cabecera></mi-cabecera>
    <mi-menu data-user="<%= session.getAttribute("nombreUsuario") %>"></mi-menu>

    <main class="container my-5">
        <%
    // Recuperamos el mensaje de error del Servlet si lo hay
    String mensaje = (String) session.getAttribute("mensaje");
    if (mensaje != null) {
        session.removeAttribute("mensaje");
%>
    <div class="alert alert-danger text-center shadow-sm fw-bold mb-4">
        <%= mensaje %>
    </div>
<% } %>
        <div class="row g-5">
            <div class="col-md-5 col-lg-4 order-md-last">
                <h4 class="d-flex justify-content-between align-items-center mb-3">
                    <span class="text-primary">Tu pedido</span>
                    <span class="badge bg-primary rounded-pill"><%= listaCarrito.size() %></span>
                </h4>
                <ul class="list-group mb-3 shadow-sm">
                    <% 
                        for (ProductoCarrito p : listaCarrito) { 
                            float sub = p.getPrecio() * p.getCantidad();
                    %>
                    <li class="list-group-item d-flex justify-content-between lh-sm">
                        <div>
                            <h6 class="my-0"><%= p.getDescripcion() %></h6>
                            <small class="text-muted">Cant: <%= p.getCantidad() %> x <%= p.getPrecio() %>€</small>
                        </div>
                        <span class="text-muted"><%= String.format(Locale.US, "%.2f", sub) %>€</span>
                    </li>
                    <% } %>
                    <li class="list-group-item d-flex justify-content-between bg-light">
                        <span>Total (EUR)</span>
                        <strong class="text-success"><%= String.format(Locale.US, "%.2f", total) %>€</strong>
                    </li>
                </ul>
                <a href="productos.jsp" class="btn btn-outline-secondary w-100">⬅ Seguir comprando</a>
            </div>

            <div class="col-md-7 col-lg-8">
                <div class="card shadow-sm border-0">
                    <div class="card-body p-4">
                        <form action="finalizarPedido.html" method="POST" id="formPago">
                            
                            <h4 class="mb-4">Confirmación de Envío</h4>
                            <div class="row g-3">
                                <div class="mb-3 position-relative">
                                    <label class="form-label">Dirección</label>
                                    <input type="text" class="form-control" name="direccion" id="input_direccion" autocomplete="off" required>
                                    <ul id="lista_sugerencias" class="list-group position-absolute w-100 shadow" style="z-index: 1000; display: none;"></ul>
                                </div>

                                <div class="row">
                                    <div class="col-md-8 mb-3">
                                        <label class="form-label">Población</label>
                                        <input type="text" class="form-control" name="poblacion" id="input_poblacion" required>
                                    </div>
                                    <div class="col-md-4 mb-3">
                                        <label class="form-label">Código Postal</label>
                                        <input type="text" class="form-control" name="cp" id="input_cp" required>
                                    </div>
                                </div>

                                <input type="hidden" name="latitud" id="lat_input" value="0.0">
                                <input type="hidden" name="longitud" id="lon_input" value="0.0">

                            </div>

                            <hr class="my-4">
                            
                            <h4 class="mb-3">Información de Pago</h4>
                            
                            <% if (misTarjetas != null && !misTarjetas.isEmpty()) { %>
                                <div class="mb-4 p-3 border rounded bg-light border-primary">
                                    <label class="form-label fw-bold text-primary">💳 Mis tarjetas guardadas</label>
                                    <select class="form-select" name="tarjetaGuardada" id="tarjetaGuardada">
                                        <option value="NUEVA">➕ Usar una tarjeta nueva...</option>
                                        <% for (TarjetaBD t : misTarjetas) { %>
                                            <option value="<%= t.getId() %>" 
                                                    data-numero="<%= t.getNumero() %>" 
                                                    data-titular="<%= t.getTitular() %>" 
                                                    data-caducidad="<%= t.getCaducidad() %>">
                                                <%= t.getNumeroOculto() %> - <%= t.getTitular() %>
                                            </option>
                                        <% } %>
                                    </select>
                                </div>
                            <% } %>

                            <div id="seccionNuevaTarjeta">
                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label class="form-label">Nº Tarjeta</label>
                                        <input type="text" class="form-control" name="numeroTarjeta" id="numeroTarjeta" placeholder="0000 0000 0000 0000" maxlength="19">
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Titular</label>
                                        <input type="text" class="form-control" name="titularTarjeta" id="titularTarjeta" placeholder="Titular de la tarjeta">
                                    </div>
                                    <div class="col-md-4">
                                        <label class="form-label">Expiración</label>
                                        <input type="text" class="form-control" name="caducidadTarjeta" id="caducidadTarjeta" placeholder="MM/AAAA" maxlength="7">
                                    </div>
                                    <div class="col-md-4">
                                        <label class="form-label">CVV</label>
                                        <input type="text" class="form-control" placeholder="123" maxlength="3" oninput="this.value = this.value.replace(/[^0-9]/g, '')">
                                    </div>
                                </div>

                                <div class="form-check mt-3">
                                    <input class="form-check-input" type="checkbox" name="guardarTarjetaCheck" value="SI" id="guardarCheck">
                                    <label class="form-check-label text-success" for="guardarCheck">
                                        <strong>Guardar esta tarjeta para mis próximas compras</strong>
                                    </label>
                                </div>
                            </div>

                            <div class="d-grid gap-2 mt-5">
                                <button type="submit" onclick="localStorage.clear();" class="btn btn-success btn-lg">
                                    Confirmar y Pagar <%= String.format(Locale.US, "%.2f", total) %>€
                                </button>
                            </div>

                        </form>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <mi-pie></mi-pie>

    <script src="js/mis-etiquetas.js"></script>
    <script src="js/logica.js?v=<%= System.currentTimeMillis() %>"></script>

</body>
</html>