<%@page language="java" contentType="text/html; charset=UTF-8" import="tienda.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Acceso y Registro - LogisTFG</title>
    <link rel="icon" type="image/ico" href="img/icono.ico" sizes="64x64">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="css/estilo.css">
</head>
<body class="bg-light">

    <mi-cabecera></mi-cabecera>
    <mi-menu></mi-menu>

    <main class="container my-5">
        <%
            // Mostramos errores si el LoginServlet nos devuelve alguno
            String mensaje = (String) session.getAttribute("mensaje");
            if (mensaje != null) {
                session.removeAttribute("mensaje");
        %>
            <div class="alert alert-danger text-center mx-auto" style="max-width: 600px;"><%= mensaje %></div>
        <% } %>

        <div class="row justify-content-center">
            <div class="col-md-6 col-lg-5">
                <div class="card shadow-sm">
                    <div class="card-header bg-primary text-white text-center py-3">
                        <h3 class="mb-0">Identificación de Usuario</h3>
                    </div>
                    <div class="card-body p-4">
                        
                        <form action="login.html" method="POST">
                            <input type="hidden" name="url" value="usuario.jsp">
                            
                            <div class="d-flex justify-content-center mb-4">
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="tipoAcceso" id="radioAcceso" value="Acceso" checked onchange="cambiarModoUsuario()">
                                    <label class="form-check-label" for="radioAcceso">Acceso</label>
                                </div>
                                <div class="form-check form-check-inline">
                                    <input class="form-check-input" type="radio" name="tipoAcceso" id="radioRegistro" value="Registro" onchange="cambiarModoUsuario()">
                                    <label class="form-check-label" for="radioRegistro">Registro</label>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label for="usuario_email" class="form-label">👤 Usuario / Email</label>
                                <input type="email" class="form-control" id="usuario_email" name="usuario" required placeholder="correo@ejemplo.com">
                            </div>
                            <div class="mb-3">
                                <label for="usuario_pass" class="form-label">🔑 Contraseña</label>
                                <input type="password" class="form-control" id="usuario_pass" name="clave" required minlength="4" placeholder="Mínimo 4 caracteres">
                            </div>

                            <div id="campos_registro" class="d-none">
                                <hr>
                                <h5 class="mb-3 text-secondary">Datos del usuario</h5>
                                <div class="row">
                                    <div class="col-6 mb-3">
                                        <label class="form-label">Nombre</label>
                                        <input type="text" class="form-control" name="nombre">
                                    </div>
                                    <div class="col-6 mb-3">
                                        <label class="form-label">Apellidos</label>
                                        <input type="text" class="form-control" name="apellidos">
                                    </div>
                                </div>
                                <div class="mb-3">
                                    <label class="form-label">Dirección Principal</label>
                                    <input type="text" class="form-control" name="domicilio">
                                </div>
                                <div class="row">
                                    <div class="col-6 mb-3">
                                        <label class="form-label">Población</label>
                                        <input type="text" class="form-control" name="poblacion">
                                    </div>
                                    <div class="col-6 mb-3">
                                        <label class="form-label">Provincia</label>
                                        <input type="text" class="form-control" name="provincia">
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-4 mb-4">
                                        <label class="form-label">C.P.</label>
                                        <input type="text" class="form-control" name="cp">
                                    </div>
                                    <div class="col-8 mb-4">
                                        <label class="form-label">Teléfono</label>
                                        <input type="tel" class="form-control" name="telefono">
                                    </div>
                                </div>
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-success" id="btn_submit">Entrar</button>
                                <button type="reset" class="btn btn-outline-secondary">Cancelar</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </main>

    <mi-pie></mi-pie>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
    <script src="./js/mis-etiquetas.js"></script>
    <script>
        function cambiarModo() {
            var esRegistro = document.getElementById('radioRegistro').checked;
            var divCampos = document.getElementById('campos_registro');
            var btnSubmit = document.getElementById('btn_submit');
            
            if (esRegistro) {
                divCampos.classList.remove('d-none');
                btnSubmit.textContent = 'Crear Cuenta y Entrar';
            } else {
                divCampos.classList.add('d-none');
                btnSubmit.textContent = 'Entrar';
            }
        }
    </script>
</body>
</html>