package com.asm.controlador;

import com.asm.modelo.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import javafx.scene.layout.HBox;
import org.hibernate.Transaction;

import java.util.Optional;
import java.util.List;

public class SeguridadController {

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colUsername;
    @FXML private TableColumn<Usuario, String> colNombreCompleto;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colUltimoAcceso;
    @FXML private TableColumn<Usuario, String> colEstado;
    @FXML private TableColumn<Usuario, String> colAcciones;

    @FXML private TextField txtBuscarUsuario;
    @FXML private Button btnNuevoUsuario;

    private SessionFactory factory;
    private ObservableList<Usuario> listaUsuariosObservable = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Configurar columnas directas
        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));

        // 2. Unir Nombre y Apellido
        colNombreCompleto.setCellValueFactory(cellData -> {
            Usuario u = cellData.getValue();
            return new SimpleStringProperty(u.getNombre() + " " + u.getApellidoPaterno());
        });

        // 3. Traducir el ID del Rol a Texto
        colRol.setCellValueFactory(cellData -> {
            int rolId = cellData.getValue().getIdRol();
            String nombreRol = (rolId == 1) ? "Administrador" : "Cajero";
            return new SimpleStringProperty(nombreRol);
        });

        // 4. Último acceso (Por ahora texto estático)
        colUltimoAcceso.setCellValueFactory(cellData -> new SimpleStringProperty("2026-05-29"));

        // 5. Traducir Estatus (1 o 0) a "Activo" o "Inactivo"
        colEstado.setCellValueFactory(cellData -> {
            int estatus = cellData.getValue().getEstatus();
            String estadoTexto = (estatus == 1) ? "🟢 Activo" : "🔴 Inactivo";
            return new SimpleStringProperty(estadoTexto);
        });

        // 6. Configurar la columna de Acciones (Botones de Baja y Llave)
        colAcciones.setCellFactory(param -> new TableCell<Usuario, String>() {
            private final Button btnBaja = new Button("🚫");
            private final Button btnLlave = new Button("🔑");
            private final HBox panelAcciones = new HBox(10, btnBaja, btnLlave);

            {
                // Estilos para los botones (transparentes como iconos)
                btnBaja.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px;");
                btnLlave.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-font-size: 14px;");

                // Acción: Dar de baja
                btnBaja.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    darDeBajaUsuario(usuario);
                });

                // Acción: Restablecer Contraseña
                btnLlave.setOnAction(event -> {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    restablecerContrasena(usuario);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(panelAcciones);
                }
            }
        });

        // 7. Configurar Hibernate y cargar datos a la tabla
        conectarHibernate();

        // 8. Evento del botón para abrir el modal
        if (btnNuevoUsuario != null) {
            btnNuevoUsuario.setOnAction(e -> abrirModalRegistro());
        }
    }

    private void conectarHibernate() {
        try {
            Configuration configuration = new Configuration().configure("/com/asm/vista/hibernate.cfg.xml");
            // Nota: Si esto te da error de duplicado en el futuro, solo comenta las siguientes dos líneas.
            configuration.addAnnotatedClass(com.asm.modelo.Usuario.class);
            configuration.addAnnotatedClass(com.asm.modelo.Rol.class);
            factory = configuration.buildSessionFactory();

            cargarUsuarios();
        } catch (Exception e) {
            System.err.println("Error al cargar Hibernate en Seguridad: " + e.getMessage());
        }
    }

    // Este método ahora es PUBLIC para que el modal pueda llamarlo al terminar de guardar
    public void cargarUsuarios() {
        listaUsuariosObservable.clear();
        try (Session session = factory.openSession()) {
            List<Usuario> usuariosBD = session.createQuery("from Usuario", Usuario.class).list();
            listaUsuariosObservable.addAll(usuariosBD);
            tablaUsuarios.setItems(listaUsuariosObservable);
        } catch (Exception e) {
            System.err.println("Error al consultar usuarios: " + e.getMessage());
        }
    }

    // Método que levanta la ventana flotante de registro
    private void abrirModalRegistro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/asm/vista/ModalNuevoUsuario.fxml"));
            Parent root = loader.load();

            // Pasamos la referencia de este controlador para que el modal pueda actualizar la tabla
            ModalNuevoUsuarioController modalController = loader.getController();
            modalController.setSeguridadControllerPadre(this);

            Stage modalStage = new Stage();
            modalStage.setTitle("Registrar Nuevo Usuario");
            modalStage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana de atrás hasta que se cierre el modal
            modalStage.setResizable(false);
            modalStage.setScene(new Scene(root));
            modalStage.showAndWait();

        } catch (Exception e) {
            System.err.println("Error al abrir el modal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==========================================
    // MÉTODOS DE ACCIONES PARA LA TABLA
    // ==========================================

    // Método para Inactivar (Dar de baja)
    private void darDeBajaUsuario(Usuario usuario) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Baja");
        confirmacion.setHeaderText("¿Estás seguro de dar de baja a " + usuario.getUsername() + "?");
        confirmacion.setContentText("El usuario ya no podrá iniciar sesión en el sistema.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try (Session session = factory.openSession()) {
                Transaction tx = session.beginTransaction();
                usuario.setEstatus(0); // 0 = Inactivo
                session.merge(usuario); // Actualiza en BD
                tx.commit();
                cargarUsuarios(); // Refresca la tabla automáticamente
            } catch (Exception e) {
                System.err.println("Error al dar de baja: " + e.getMessage());
            }
        }
    }

    // Método para Restablecer Contraseña
    private void restablecerContrasena(Usuario usuario) {
        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Restablecer Contraseña");
        dialogo.setHeaderText("Nueva contraseña para: " + usuario.getUsername());
        dialogo.setContentText("Contraseña:");

        Optional<String> resultado = dialogo.showAndWait();
        if (resultado.isPresent()) {
            String nuevaContrasena = resultado.get();
            if (!nuevaContrasena.trim().isEmpty()) {
                try (Session session = factory.openSession()) {
                    Transaction tx = session.beginTransaction();
                    usuario.setContrasena(nuevaContrasena);
                    session.merge(usuario);
                    tx.commit();

                    Alert exito = new Alert(Alert.AlertType.INFORMATION);
                    exito.setTitle("Éxito");
                    exito.setHeaderText(null);
                    exito.setContentText("La contraseña se actualizó correctamente.");
                    exito.showAndWait();
                } catch (Exception e) {
                    System.err.println("Error al cambiar contraseña: " + e.getMessage());
                }
            }
        }
    }
}