package com.asm.controlador;

import com.asm.modelo.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

public class ModalNuevoUsuarioController {

    @FXML private TextField txtNombreCompleto;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private PasswordField txtConfirmPassword;
    @FXML private ComboBox<String> cmbRol;
    @FXML private Button btnCancelar;
    @FXML private Button btnCrearUsuario;
    @FXML private Button btnCerrarIcono;

    private SessionFactory factory;
    private SeguridadController seguridadControllerPadre;

    @FXML
    public void initialize() {
        // 🔥 CORRECCIÓN: Agregar "Almacenista" a las opciones del ComboBox
        cmbRol.setItems(FXCollections.observableArrayList("Administrador", "Cajero", "Almacenista"));
        cmbRol.getSelectionModel().selectFirst();

        // Conectar Hibernate
        try {
            // Solo leemos el XML, ya no forzamos las clases porque ya están registradas ahí
            Configuration configuration = new Configuration().configure("/com/asm/vista/hibernate.cfg.xml");
            factory = configuration.buildSessionFactory();
        } catch (Exception e) {
            // Imprimimos un mensaje súper visible en rojo por si algo más falla
            System.err.println("🚨 ERROR AL INICIAR HIBERNATE EN EL MODAL:");
            e.printStackTrace();
        }

        // Acciones de los botones
        btnCrearUsuario.setOnAction(e -> guardarUsuario());
        btnCancelar.setOnAction(e -> cerrarModal());
        btnCerrarIcono.setOnAction(e -> cerrarModal());
    }

    public void setSeguridadControllerPadre(SeguridadController padre) {
        this.seguridadControllerPadre = padre;
    }

    private void guardarUsuario() {
        String nombreCompleto = txtNombreCompleto.getText().trim();
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        String confirm = txtConfirmPassword.getText();
        String rolSeleccionado = cmbRol.getValue();

        // 1. Validaciones básicas
        if (nombreCompleto.isEmpty() || username.isEmpty() || password.isEmpty() || rolSeleccionado == null) {
            mostrarAlerta("Campos vacíos", "Por favor llena todos los campos obligatorios.");
            return;
        }
        if (!password.equals(confirm)) {
            mostrarAlerta("Error de Contraseña", "Las contraseñas no coinciden.");
            return;
        }

        // 2. Dividir el "Nombre Completo" para guardarlo en la BD
        String[] partesNombre = nombreCompleto.split(" ", 2);
        String nombre = partesNombre[0];
        // Si escribió dos palabras, la segunda se va a apellido_paterno
        String apellido = (partesNombre.length > 1) ? partesNombre[1] : "";

        // 🔥 CORRECCIÓN: Obtener el ID del Rol (1 = Administrador, 2 = Cajero, 3 = Almacenista)
        int idRol;
        if (rolSeleccionado.equals("Administrador")) {
            idRol = 1;
        } else if (rolSeleccionado.equals("Cajero")) {
            idRol = 2;
        } else {
            idRol = 3; // Almacenista
        }

        // 4. Crear el objeto Usuario (Ajustado a tu clase Usuario.java)
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellidoPaterno(apellido);
        // Eliminamos el apellido materno porque no existe en tu entidad
        nuevoUsuario.setUsername(username);
        nuevoUsuario.setContrasena(password); // Corregido a setContrasena
        nuevoUsuario.setIdRol(idRol);
        nuevoUsuario.setEstatus(1);

        // 5. Guardar en Base de Datos con Hibernate
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(nuevoUsuario);
            tx.commit();

            // Refrescar la tabla de la pantalla principal
            if (seguridadControllerPadre != null) {
                seguridadControllerPadre.cargarUsuarios();
            }
            cerrarModal();
        } catch (Exception e) {
            mostrarAlerta("Error en BD", "No se pudo guardar el usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cerrarModal() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}