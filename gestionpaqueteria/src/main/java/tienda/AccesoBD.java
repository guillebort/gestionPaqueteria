package tienda;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AccesoBD {
    private static AccesoBD instanciaUnica = null;
	private Connection conexionBD = null;

	public static AccesoBD getInstance() {
		if (instanciaUnica == null) {
			instanciaUnica = new AccesoBD();
		}
		return instanciaUnica;
	}

	private AccesoBD() {
		abrirConexionBD();
	}

	public void abrirConexionBD() {
		if (conexionBD == null) {
			String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
			// daw es el nombre de la base de datos que hemos creado con anterioridad.
			String DB_URL = "jdbc:mariadb://localhost:3305/gestionlogistica";
			// El usuario root y su clave son los que se puso al instalar MariaDB.
			String USER = "root";
			String PASS = "root";
			try {
				Class.forName(JDBC_DRIVER);
				conexionBD = DriverManager.getConnection(DB_URL, USER, PASS);
			} catch (Exception e) {
				System.err.println("No se ha podido conectar a la base de datos");
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

	public boolean comprobarAcceso() {
		abrirConexionBD();
		return (conexionBD != null);
	}

	//Metodo para obtener la lista de productos de la BD
	public List<ProductoBD> obtenerProductosBD() {
        abrirConexionBD();
	    ArrayList<ProductoBD> productos = new ArrayList<>();
        try {
		    String query = "SELECT id,descripcion,precio,existencias,imagen,caracteristicas,color_css FROM productos";
		    PreparedStatement s = conexionBD.prepareStatement(query);
		    ResultSet resultado = s.executeQuery();
		    while(resultado.next()){
			    ProductoBD producto = new ProductoBD();
			    producto.setId(resultado.getInt("id"));
			    producto.setDescripcion(resultado.getString("descripcion"));
			    producto.setPrecio(resultado.getFloat("precio"));
			    producto.setExistencias(resultado.getInt("existencias"));
			    producto.setImagen(resultado.getString("imagen"));
				producto.setCaracteristicas(resultado.getString("caracteristicas"));
    			producto.setColorCss(resultado.getString("color_css"));
			    productos.add(producto);
		    }
	    } catch(Exception e) {
		    System.err.println("Error ejecutando la consulta a la base de datos");
		    System.err.println(e.getMessage());
	    }
	    return productos;
    }

	public int comprobarUsuarioBD(String usuario, String clave){
		abrirConexionBD();
		int codigo = -1;

		try{
			String query = "SELECT id FROM usuarios WHERE usuario=? AND clave=?";
			PreparedStatement s = conexionBD.prepareStatement(query);
			s.setString(1, usuario);
			String claveSegura = encriptarSHA1(clave);
            s.setString(2, claveSegura);

			ResultSet resultado = s.executeQuery();
			
			if(resultado.next()){
				codigo = resultado.getInt("id");
			}

		}catch(Exception e) {
            System.err.println("Error verificando usuario/clave");
            System.err.println(e.getMessage());
        }

		return codigo;
	}

    // Método para comprobar si un correo ya existe antes de registrarlo
    public boolean existeUsuario(String email) {
        abrirConexionBD();
        try {
            String sql = "SELECT id FROM usuarios WHERE usuario = ?";
            PreparedStatement ps = conexionBD.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            boolean existe = rs.next(); // Si hay un resultado, es true (existe)
            
            rs.close();
            ps.close();
            return existe;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
	// Método para encriptar contraseñas en SHA-1
	private String encriptarSHA1(String clave) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(clave.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch(Exception ex) {
            ex.printStackTrace();
            return clave;
        }
    }

	// Método para guardar un usuario nuevo en la BD
    public boolean registrarUsuarioBD(String usuario, String clave, String nombre, String apellidos, String domicilio, String poblacion, 
									  String provincia, String cp, String telefono) {
        abrirConexionBD();
        try {
            // Encriptamos la clave antes de mandarla a MariaDB
            String claveSegura = encriptarSHA1(clave);
            
            String sql = "INSERT INTO usuarios (usuario, clave, nombre, apellidos, domicilio, poblacion, provincia, cp, telefono, activo, rol) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1, 0)";
            PreparedStatement s = conexionBD.prepareStatement(sql);
            s.setString(1, usuario);
            s.setString(2, claveSegura);
            s.setString(3, nombre);
            s.setString(4, apellidos);
            s.setString(5, domicilio);
            s.setString(6, poblacion);
            s.setString(7, provincia);
            s.setString(8, cp);
            s.setString(9, telefono);
            
            int filas = s.executeUpdate();
            return filas > 0; // Devuelve true si se insertó correctamente
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

	public UsuarioBD obtenerUsuarioBD(int id) {
    abrirConexionBD();
    UsuarioBD u = null;
    try {
        String query = "SELECT * FROM usuarios WHERE id = ?";
        PreparedStatement s = conexionBD.prepareStatement(query);
        s.setInt(1, id);
        ResultSet rs = s.executeQuery();
        if (rs.next()) {
            u = new UsuarioBD();
            u.setId(rs.getInt("id"));
            u.setUsuario(rs.getString("usuario"));
            u.setNombre(rs.getString("nombre"));
            u.setApellidos(rs.getString("apellidos"));
            u.setDomicilio(rs.getString("domicilio"));
            u.setPoblacion(rs.getString("poblacion"));
            u.setProvincia(rs.getString("provincia"));
            u.setCp(rs.getString("cp"));
            u.setTelefono(rs.getString("telefono"));
            u.setRol(rs.getInt("rol"));
        }
    } catch (Exception e) { e.printStackTrace(); }
		return u;
	}

    // Función para modificar los datos del usuario en la base de datos
    public boolean modificarUsuarioBD(int id, String clave, String nombre, String apellidos, String domicilio, String poblacion, String provincia, String cp, String telefono) {
        abrirConexionBD();
        try {
            // Si la clave está vacía, actualizamos todo menos la clave
            if (clave == null || clave.trim().isEmpty()) {
                String sql = "UPDATE usuarios SET nombre=?, apellidos=?, domicilio=?, poblacion=?, provincia=?, cp=?, telefono=? WHERE id=?";
                PreparedStatement s = conexionBD.prepareStatement(sql);
                s.setString(1, nombre);
                s.setString(2, apellidos);
                s.setString(3, domicilio);
                s.setString(4, poblacion);
                s.setString(5, provincia);
                s.setString(6, cp);
                s.setString(7, telefono);
                s.setInt(8, id);
                return s.executeUpdate() > 0;
            } else {
                // Si ha escrito una clave nueva, la encriptamos y la actualizamos también
                String claveSegura = encriptarSHA1(clave);
                String sql = "UPDATE usuarios SET clave=?, nombre=?, apellidos=?, domicilio=?, poblacion=?, provincia=?, cp=?, telefono=? WHERE id=?";
                PreparedStatement s = conexionBD.prepareStatement(sql);
                s.setString(1, claveSegura);
                s.setString(2, nombre);
                s.setString(3, apellidos);
                s.setString(4, domicilio);
                s.setString(5, poblacion);
                s.setString(6, provincia);
                s.setString(7, cp);
                s.setString(8, telefono);
                s.setInt(9, id);
                return s.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Función para comprobar el stock real de un producto antes de venderlo
    public int obtenerExistencias(int idProducto) {
        abrirConexionBD();
        int existencias = 0;
        try {
            String sql = "SELECT existencias FROM productos WHERE id = ?";
            PreparedStatement s = conexionBD.prepareStatement(sql);
            s.setInt(1, idProducto);
            ResultSet rs = s.executeQuery();
            if (rs.next()) {
                existencias = rs.getInt("existencias");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return existencias;
    }

    public int guardarPedido(int idUsuario, float importeTotal, ArrayList<ProductoCarrito> carrito, 
                                  String textoOrigen, double latOrigen, double lonOrigen,
                                  String textoDestino, double latDestino, double lonDestino) {
    int idPedidoNuevo = -1;
    
    try {
        abrirConexionBD();
        conexionBD.setAutoCommit(false);

        // 1. Insertar Dirección de Origen
        String sqlDir = "INSERT INTO direcciones (calle_texto, latitud, longitud) VALUES (?, ?, ?)";
        PreparedStatement psOrigen = conexionBD.prepareStatement(sqlDir, Statement.RETURN_GENERATED_KEYS);
        psOrigen.setString(1, textoOrigen);
        psOrigen.setDouble(2, latOrigen);
        psOrigen.setDouble(3, lonOrigen);
        psOrigen.executeUpdate();
        ResultSet rsOrigen = psOrigen.getGeneratedKeys();
        rsOrigen.next();
        int idDirOrigen = rsOrigen.getInt(1);

        // 2. Insertar Dirección de Destino
        PreparedStatement psDestino = conexionBD.prepareStatement(sqlDir, Statement.RETURN_GENERATED_KEYS);
        psDestino.setString(1, textoDestino);
        psDestino.setDouble(2, latDestino);
        psDestino.setDouble(3, lonDestino);
        psDestino.executeUpdate();
        ResultSet rsDestino = psDestino.getGeneratedKeys();
        rsDestino.next();
        int idDirDestino = rsDestino.getInt(1);

        // 3. Insertar el Pedido (Estado 1 = Pendiente)
        String sqlPedido = "INSERT INTO pedidos (persona, fecha, importe, estado, id_direccion_origen, id_direccion_destino) VALUES (?, CURDATE(), ?, 1, ?, ?)";
        PreparedStatement psPedido = conexionBD.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS);
        psPedido.setInt(1, idUsuario);
        psPedido.setFloat(2, importeTotal);
        psPedido.setInt(3, idDirOrigen);
        psPedido.setInt(4, idDirDestino);
        psPedido.executeUpdate();
        ResultSet rsPedido = psPedido.getGeneratedKeys();
        rsPedido.next();
        idPedidoNuevo = rsPedido.getInt(1);

        // 4. Insertar el Detalle del pedido (los productos del carrito)
        String sqlDetalle = "INSERT INTO detalle (id_pedido, id_producto, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
        String sqlRestarStock = "UPDATE productos SET existencias = existencias - ? WHERE id = ?"; 

        PreparedStatement psDetalle = conexionBD.prepareStatement(sqlDetalle);
        PreparedStatement psStock = conexionBD.prepareStatement(sqlRestarStock);
        
        for (ProductoCarrito prod : carrito) {
            psDetalle.setInt(1, idPedidoNuevo);
            psDetalle.setInt(2, prod.getCodigo());
            psDetalle.setInt(3, prod.getCantidad());
            psDetalle.setFloat(4, prod.getPrecio());
            psDetalle.executeUpdate();

            psStock.setInt(1, prod.getCantidad());
            psStock.setInt(2, prod.getCodigo()); // El ID del producto
            psStock.executeUpdate();
        }
        psDetalle.close();
        psStock.close();
        conexionBD.commit(); 

    } catch (Exception e) {
        if (conexionBD != null) {
            try { conexionBD.rollback(); } catch (SQLException ex) { ex.printStackTrace(); } // SI HAY ERROR, DESHACEMOS TODO
        }
        e.printStackTrace();
    } finally {
         try { if (conexionBD != null) conexionBD.setAutoCommit(true); } catch (Exception ex) {}
    }
    
    return idPedidoNuevo;
}

    // FUNCIÓN MEJORADA PARA MOSTRAR LOS ERRORES EN PANTALLA
    public java.util.ArrayList<PedidoBD> obtenerHistorialDetallado(int idUsuario) {
        java.util.ArrayList<PedidoBD> lista = new java.util.ArrayList<>();
        
        try {
            // 1. FORZAMOS A QUE LA CONEXIÓN ESTÉ VIVA SÍ O SÍ
            // (Si la función de arriba la cerró, la volvemos a abrir)
            if (conexionBD == null || conexionBD.isClosed()) {
                abrirConexionBD();
            }

            // 2. Sacamos los pedidos y cruzamos con la tabla 'estados'
            String sqlPedidos = "SELECT p.id, p.fecha, p.importe, e.descripcion as nombre_estado " +
                                "FROM pedidos p JOIN estados e ON p.estado = e.id " +
                                "WHERE p.persona = ? ORDER BY p.fecha DESC";
            java.sql.PreparedStatement ps = conexionBD.prepareStatement(sqlPedidos);
            ps.setInt(1, idUsuario);
            java.sql.ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                PedidoBD ped = new PedidoBD();
                ped.setId(rs.getInt("id"));
                ped.setFecha(rs.getDate("fecha"));
                ped.setImporteTotal(rs.getDouble("importe"));
                ped.setEstado(rs.getString("nombre_estado"));

                // 3. Por cada pedido, sacamos sus líneas de detalle cruzadas con 'productos'
                String sqlDetalle = "SELECT pr.descripcion, d.cantidad, d.precio_unitario " +
                                    "FROM detalle d JOIN productos pr ON d.id_producto = pr.id " +
                                    "WHERE d.id_pedido = ?";
                java.sql.PreparedStatement psDet = conexionBD.prepareStatement(sqlDetalle);
                psDet.setInt(1, ped.getId());
                java.sql.ResultSet rsDet = psDet.executeQuery();

                while (rsDet.next()) {
                    DetallePedidoBD linea = new DetallePedidoBD();
                    linea.setCantidad(rsDet.getInt("cantidad"));
                    linea.setPrecio(rsDet.getDouble("precio_unitario"));
                    
                    ProductoBD prod = new ProductoBD();
                    prod.setDescripcion(rsDet.getString("descripcion"));
                    linea.setProducto(prod);

                    ped.getDetalles().add(linea); 
                }
                
                lista.add(ped);
            }
        } catch (Exception e) {
            // 💣 AQUÍ ESTÁ LA TRAMPA: Si algo falla, cortamos el código y lanzamos 
            // el error hacia arriba para que tu usuario.jsp lo pinte en el cuadro rojo.
            throw new RuntimeException("ERROR EN BASE DE DATOS: " + e.getMessage(), e);
        }
        
        return lista;
    }

    //Funcion para cancelar el pedido y devolver el stock
    public boolean cancelarPedido(int idPedido, int idUsuario) {
        try {
            if (conexionBD == null || conexionBD.isClosed()) abrirConexionBD();
            
            // Apagamos el autoguardado para hacerlo seguro
            conexionBD.setAutoCommit(false);

            // 1. Cambiamos el estado a 4 (Asumiendo que 4 es "Cancelado" en tu BD)
            // OJO: Comprobamos que el estado actual sea 1 (Pendiente) y que el pedido sea del usuario
            String sqlCancelar = "UPDATE pedidos SET estado = 4 WHERE id = ? AND persona = ? AND estado = 1";
            java.sql.PreparedStatement psCancelar = conexionBD.prepareStatement(sqlCancelar);
            psCancelar.setInt(1, idPedido);
            psCancelar.setInt(2, idUsuario);
            
            int filasModificadas = psCancelar.executeUpdate();
            
            // Si no se modificó nada, es porque no era suyo, ya estaba enviado o no existe
            if (filasModificadas == 0) {
                conexionBD.rollback();
                return false;
            }

            // 2. Buscamos qué productos tenía este pedido para devolverlos al almacén
            String sqlDetalle = "SELECT id_producto, cantidad FROM detalle WHERE id_pedido = ?";
            java.sql.PreparedStatement psDetalle = conexionBD.prepareStatement(sqlDetalle);
            psDetalle.setInt(1, idPedido);
            java.sql.ResultSet rs = psDetalle.executeQuery();

            // 3. Devolvemos el stock producto a producto
            String sqlDevolverStock = "UPDATE productos SET existencias = existencias + ? WHERE id = ?";
            java.sql.PreparedStatement psStock = conexionBD.prepareStatement(sqlDevolverStock);

            while (rs.next()) {
                psStock.setInt(1, rs.getInt("cantidad"));
                psStock.setInt(2, rs.getInt("id_producto"));
                psStock.executeUpdate();
            }

            // 4. Guardamos todo definitivamente
            conexionBD.commit();
            return true;

        } catch (Exception e) {
            try { if (conexionBD != null) conexionBD.rollback(); } catch (Exception ex) {}
            e.printStackTrace();
            return false;
        } finally {
            try { if (conexionBD != null) conexionBD.setAutoCommit(true); } catch (Exception ex) {}
        }
    }

    public boolean guardarMensajeContacto(String nombre, String email, String asunto, String mensaje) {
        System.out.println("DEBUG: Intentando guardar mensaje de " + email); // <-- CHIVATO 1
        abrirConexionBD();
        try {
            String sql = "INSERT INTO mensajes (nombre, email, asunto, mensaje, fecha) VALUES (?, ?, ?, ?, CURDATE())";
            java.sql.PreparedStatement ps = conexionBD.prepareStatement(sql);
            ps.setString(1, nombre);
            ps.setString(2, email);
            ps.setString(3, asunto);
            ps.setString(4, mensaje);
            
            int filas = ps.executeUpdate();
            System.out.println("DEBUG: Filas insertadas: " + filas); // <-- CHIVATO 2
            return filas > 0;
            
        } catch (Exception e) {
            System.out.println("DEBUG ERROR SQL: " + e.getMessage()); // <-- ESTE ES EL IMPORTANTE
            e.printStackTrace();
            return false;
        }
    
    }

    public String obtenerNombreUsuario(int id) {
        abrirConexionBD();
        try {
            // Asegúrate de que tu tabla se llama 'usuarios' y la columna 'nombre'
            String sql = "SELECT nombre FROM usuarios WHERE id = ?"; 
            java.sql.PreparedStatement ps = conexionBD.prepareStatement(sql);
            ps.setInt(1, id);
            java.sql.ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("nombre");
            }
        } catch (Exception e) { e.printStackTrace(); }
        return "";
    }

    //guardar la tarjeta del usuario
    public boolean guardarTarjeta(int idUsuario, String numero, String titular, String caducidad) {
        abrirConexionBD();
        try {
            String sql = "INSERT INTO tarjetas (id_usuario, numero, titular, caducidad) VALUES (?, ?, ?, ?)";
            java.sql.PreparedStatement ps = conexionBD.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ps.setString(2, numero);
            ps.setString(3, titular);
            ps.setString(4, caducidad);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // OBTENER TARJETAS DEL USUARIO (Devuelve un ArrayList)
    public java.util.ArrayList<TarjetaBD> obtenerTarjetasUsuario(int idUsuario) {
        abrirConexionBD();
        java.util.ArrayList<TarjetaBD> listaTarjetas = new java.util.ArrayList<>();
        try {
            String sql = "SELECT * FROM tarjetas WHERE id_usuario = ?";
            java.sql.PreparedStatement ps = conexionBD.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            java.sql.ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                TarjetaBD t = new TarjetaBD();
                t.setId(rs.getInt("id"));
                t.setIdUsuario(rs.getInt("id_usuario"));
                t.setNumero(rs.getString("numero"));
                t.setTitular(rs.getString("titular"));
                t.setCaducidad(rs.getString("caducidad"));
                listaTarjetas.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listaTarjetas;
    }

    private int insertarDireccion(String direccion, double lat, double lon) {
        abrirConexionBD();
    int idGenerado = -1;
    try {
        String sql = "INSERT INTO direcciones (calle_texto, latitud, longitud) VALUES (?, ?, ?)";
        PreparedStatement ps = conexionBD.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        
        ps.setString(1, direccion != null ? direccion : "No especificado");
        ps.setDouble(2, lat);
        ps.setDouble(3, lon);
        
        ps.executeUpdate();
        
        ResultSet rs = ps.getGeneratedKeys();
        if (rs.next()) {
            idGenerado = rs.getInt(1);
        }
    } catch (SQLException e) {
        System.out.println("❌ Error guardando dirección: " + e.getMessage());
    }
    return idGenerado;
    }

    public java.util.ArrayList<PedidoBD> obtenerPedidosUsuario(int idUsuario) {
        java.util.ArrayList<PedidoBD> listaPedidos = new java.util.ArrayList<>();
        abrirConexionBD();
        
        try {
            // 1. La consulta SQL con LEFT JOIN 
            String sql = "SELECT p.id, p.fecha, p.importe, p.estado, " +
                         "dir_o.id AS id_origen, dir_o.calle_texto AS txt_origen, dir_o.latitud AS lat_origen, dir_o.longitud AS lon_origen, " +
                         "dir_d.id AS id_destino, dir_d.calle_texto AS txt_destino, dir_d.latitud AS lat_destino, dir_d.longitud AS lon_destino " +
                         "FROM pedidos p " +
                         "LEFT JOIN direcciones dir_o ON p.id_direccion_origen = dir_o.id " +
                         "LEFT JOIN direcciones dir_d ON p.id_direccion_destino = dir_d.id " +
                         "WHERE p.persona = ? " +
                         "ORDER BY p.fecha DESC";
                         
            java.sql.PreparedStatement ps = conexionBD.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            java.sql.ResultSet rs = ps.executeQuery();

            // 2. Recorremos los resultados y montamos los objetos
            while (rs.next()) {
                PedidoBD p = new PedidoBD();
                p.setId(rs.getInt("id"));
                p.setFecha(rs.getDate("fecha"));
                p.setImporteTotal(rs.getFloat("importe"));
                
                // SOLUCIÓN: Convertimos el número (int) a Texto (String)
                p.setEstado(String.valueOf(rs.getInt("estado"))); 
                
                // 3. Fabricamos el objeto Dirección para el ORIGEN
                Direccion origen = new Direccion();
                origen.setId(rs.getInt("id_origen"));
                origen.setTextoDireccion(rs.getString("txt_origen"));
                origen.setLatitud(rs.getDouble("lat_origen"));
                origen.setLongitud(rs.getDouble("lon_origen"));
                p.setOrigen(origen); 
                
                // 4. Fabricamos el objeto Dirección para el DESTINO
                Direccion destino = new Direccion();
                destino.setId(rs.getInt("id_destino"));
                destino.setTextoDireccion(rs.getString("txt_destino"));
                destino.setLatitud(rs.getDouble("lat_destino"));
                destino.setLongitud(rs.getDouble("lon_destino"));
                p.setDestino(destino); 
                
                // 5. Añadimos el pedido completo a la lista
                listaPedidos.add(p);
            }
        } catch (Exception e) {
            System.out.println("❌ ERROR leyendo pedidos: " + e.getMessage());
            e.printStackTrace();
        }
        
        return listaPedidos;
    }

    public boolean asignarRepartidor(int idPedido, int idRepartidor, int nuevoEstado) {
    abrirConexionBD();
    boolean actualizado = false;
    try {
        // Hacemos el UPDATE en la tabla pedidos[cite: 3]
        String sql = "UPDATE pedidos SET id_repartidor = ?, estado = ? WHERE id = ?";
        PreparedStatement s = conexionBD.prepareStatement(sql);
        s.setInt(1, idRepartidor);
        s.setInt(2, nuevoEstado);
        s.setInt(3, idPedido);
        
        int filas = s.executeUpdate();
        if (filas > 0) {
            actualizado = true;
        }
    } catch (Exception e) {
        System.err.println("Error al asignar el repartidor en la BD");
        e.printStackTrace();
    }
    return actualizado;
    }
}