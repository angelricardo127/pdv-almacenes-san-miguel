package com.asm.controlador;

import com.asm.modelo.Usuario;
import com.asm.servicio.UsuarioService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnIngresar;

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

            // ❌ Quitamos el btnIngresar.setOnAction(...) porque tu FXML ya hace ese trabajo con el onAction.

        } catch (Exception e) {
            System.err.println("❌ Error crítico al inicializar Hibernate en el Login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 🔥 Agregamos el @FXML aquí para que el archivo FXML lo pueda ver
    @FXML
    public void iniciarSesion() {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        // Limpiar estilos previos
        txtPassword.setStyle("-fx-background-color: white; -fx-border-color: #bdc3c7; -fx-border-radius: 6;");

        if (user.isEmpty() || pass.isEmpty()) {
            System.out.println("⚠️ Por favor, llena ambos campos.");
            return;
        }

        System.out.println("⏳ Validando credenciales de [" + user + "] en MySQL...");

        // Vamos a la base de datos a preguntar usando el servicio existente
        Usuario usuarioValidado = usuarioService.validarUsuario(user, pass);

        if (usuarioValidado != null) {
            System.out.println("✅ ¡Bienvenido " + usuarioValidado.getNombre() + "! Acceso concedido.");
            abrirSistemaPrincipal();
        } else {
            System.out.println("❌ Credenciales incorrectas. Intenta de nuevo.");
            txtPassword.clear();
            // Ponemos el borde rojo para alertar visualmente al usuario
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
            System.err.println("❌ Error al abrir el sistema principal: " + e.getMessage());
            e.printStackTrace();
        }
    }
}