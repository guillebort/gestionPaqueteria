<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Seguridad: Si entra aquí sin estar logueado o sin carrito, lo echamos
    if (session.getAttribute("codigo") == null || session.getAttribute("carritoJSON") == null) {
        response.sendRedirect("productos.html");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Datos de Envío - LogisTFG</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body class="bg-light">
    <mi-cabecera></mi-cabecera>
    <mi-menu data-user="${nombreUsuario}"></mi-menu>

    <main class="container my-5">
        <div class="row justify-content-center">
            <div class="col-md-8">
                <div class="d-flex justify-content-between mb-4">
                    <div class="text-primary fw-bold">Paso 1: Origen y Destino</div>
                    <div class="text-muted">Paso 2: Pago</div>
                </div>

                <div class="card shadow-sm border-0">
                    <div class="card-header bg-dark text-white py-3">
                        <h4 class="mb-0">📍 Detalles de la Ruta</h4>
                    </div>
                    <div class="card-body p-4">
                        <form action="guardarRuta.html" method="POST">
                            
                            <h5 class="text-primary mb-3">Dirección de Recogida (Origen)</h5>
                            <div class="mb-4 position-relative">
                                <input type="text" class="form-control form-control-lg border-primary" 
                                       name="direccionOrigen" id="input_origen" placeholder="Ej: Calle de la Piruleta, Madrid" required autocomplete="off">
                                <ul id="lista_origen" class="list-group position-absolute w-100 shadow" style="z-index: 1050; display: none; top: 100%;"></ul>
                                
                                <input type="hidden" name="latOrigen" id="lat_origen" value="0.0">
                                <input type="hidden" name="lonOrigen" id="lon_origen" value="0.0">
                            </div>

                            <hr class="my-4">

                            <h5 class="text-success mb-3">Dirección de Entrega (Destino)</h5>
                            <div class="mb-4 position-relative">
                                <input type="text" class="form-control form-control-lg border-success" 
                                       name="direccionDestino" id="input_destino" placeholder="Ej: Avenida Siempre Viva, Valencia" required autocomplete="off">
                                <ul id="lista_destino" class="list-group position-absolute w-100 shadow" style="z-index: 1050; display: none; top: 100%;"></ul>
                                
                                <input type="hidden" name="latDestino" id="lat_destino" value="0.0">
                                <input type="hidden" name="lonDestino" id="lon_destino" value="0.0">
                            </div>

                            <div class="d-grid gap-2 mt-5">
                                <button type="submit" class="btn btn-dark btn-lg">Confirmar Ruta y Continuar al Pago ➔</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <mi-pie></mi-pie>
    <script src="js/mis-etiquetas.js"></script>
    <script src="js/logica-mapas.js?v=<%= System.currentTimeMillis() %>"></script>
</body>
</html>