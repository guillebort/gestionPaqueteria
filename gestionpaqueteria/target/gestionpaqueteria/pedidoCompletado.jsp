<%@page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Solo dejamos entrar si el usuario está logueado
    if (session.getAttribute("codigo") == null) {
        response.sendRedirect("index.html");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>¡Pedido Completado! - LogisTFG</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body class="bg-light">

    <mi-cabecera></mi-cabecera>
    <mi-menu></mi-menu>

    <main class="container my-5 text-center">
        <div class="card shadow border-0 mx-auto" style="max-width: 600px;">
            <div class="card-body py-5">
                <h1 class="display-1 text-success mb-4">✅</h1>
                <h2 class="text-success mb-3">¡Pago realizado con éxito!</h2>
                <p class="lead text-muted mb-4">
                    Tu pedido se ha guardado correctamente. En breve nos pondremos en contacto contigo para coordinar la entrega.
                </p>
                <div class="d-grid gap-2 d-sm-flex justify-content-sm-center">
                    <a href="productos.html" class="btn btn-primary btn-lg px-4 gap-3">Volver a la Tienda</a>
                    <a href="usuario.html" class="btn btn-outline-secondary btn-lg px-4">Ver mis pedidos</a>
                </div>
            </div>
        </div>
    </main>

    <mi-pie></mi-pie>
    <script src="js/mis-etiquetas.js"></script>
    
    <script>
        window.onload = function() {
            localStorage.removeItem("mi-carrito");
            console.log("Cesta vaciada tras la compra.");
        };
    </script>
</body>
</html>