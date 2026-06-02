package com.asm.controlador;

import com.asm.modelo.Usuario;
import com.asm.servicio.UsuarioService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label; // IMPORTANTE: Agregamos la importación del Label
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnIngresar;

    // Nuestro nuevo Label para mostrar los mensajes de error dinámicos
    @FXML private Label lblError;

    private UsuarioService usuarioService;

    @FXML
    public void initialize() {
        try {
            // 1. Configuramos Hibernate mapeando correctamente las clases
            Configuration configuration = new Configuration();
            configuration.configure("/com/asm/vista/hibernate.cfg.xml");
            configuration.addAnnotatedClass(com.asm.modelo.Usuario.class);
            configuration.addAnnotatedClass(com.asm.modelo.Rol.class);

            SessionFactory factory = configuration.buildSessionFactory();
            this.usuarioService = new UsuarioService(factory);

        } catch (Exception e) {
            System.err.println(" Error crítico al inicializar Hibernate en el Login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    public void iniciarSesion() {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        // Limpiar estilos y mensajes previos por si es el segundo intento del usuario
        txtPassword.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 6;");
        if (lblError != null) {
            lblError.setText("");
        }

        if (user.isEmpty() || pass.isEmpty()) {
            System.out.println(" Por favor, llena ambos campos.");
            if (lblError != null) lblError.setText("Por favor, llena ambos campos.");
            return;
        }

        System.out.println(" Validando credenciales de [" + user + "] en MySQL...");

        try {
            // usamos el metodo detallado que lanza las excepciones precisas
            Usuario usuarioValidado = usuarioService.validarUsuarioDetallado(user, pass);

            // si llegamos a esta línea, el login fue exitoso
            System.out.println(" ¡Bienvenido " + usuarioValidado.getNombre() + "! Acceso concedido.");

            // 1 primero Guardamos la sesión usando la variable usuario validado
            com.asm.modelo.SesionGlobal.setUsuarioActual(usuarioValidado);

            // 2 segundo abrimos la ventana, así el Dashboard ya podrá leer el rol
            abrirSistemaPrincipal();

        } catch (Exception e) {
            // si algo fallo en MySQL (usuario no existe, contraseña mal, inactivo) lo atrapamos aquí
            System.out.println(" Error de login: " + e.getMessage());

            // le pintamos el texto rojo exacto al usuario en pantalla
            if (lblError != null) {
                lblError.setText(e.getMessage());
            }

            txtPassword.clear();
            // mantenemos tu alerta visual del borde rojo
            txtPassword.setStyle("-fx-background-color: white; -fx-border-color: red; -fx-border-radius: 6; -fx-background-radius: 6;");
        }
    }

    private void abrirSistemaPrincipal() {
        try {
            // Aquí cargamos el FXML del MARCO
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/asm/vista/ContenedorBase.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Almacenes San Miguel - Sistema POS");
            stage.setScene(new Scene(root));
            stage.setMaximized(true); // Se abre en pantalla completa ideal para puntos de venta

            // Mostramos el sistema principal
            stage.show();

            // Cerramos la ventana de Login actual
            Stage loginStage = (Stage) btnIngresar.getScene().getWindow();
            loginStage.close();

        } catch (Exception e) {
            System.err.println(" Error al abrir el sistema principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}