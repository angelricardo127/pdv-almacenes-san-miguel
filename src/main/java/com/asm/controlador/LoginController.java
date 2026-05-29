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
        // 1. Configuramos Hibernate (Asegúrate de registrar las clases de Ángel aquí)
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        configuration.addAnnotatedClass(com.asm.modelo.Usuario.class);
        configuration.addAnnotatedClass(com.asm.modelo.Rol.class);

        SessionFactory factory = configuration.buildSessionFactory();
        this.usuarioService = new UsuarioService(factory);

        // 2. Le damos la acción al botón
        btnIngresar.setOnAction(event -> iniciarSesion());
    }

    private void iniciarSesion() {
        String user = txtUsuario.getText();
        String pass = txtPassword.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            System.out.println("⚠️ Por favor, llena ambos campos.");
            return;
        }

        System.out.println("⏳ Validando credenciales en MySQL...");

        // Vamos a la base de datos a preguntar
        Usuario usuarioValidado = usuarioService.validarUsuario(user, pass);

        if (usuarioValidado != null) {
            System.out.println("✅ ¡Bienvenido! Acceso concedido.");
            abrirSistemaPrincipal();
        } else {
            System.out.println("❌ Credenciales incorrectas. Intenta de nuevo.");
            txtPassword.clear(); // Limpiamos la contraseña para que la vuelva a escribir
            txtPassword.setStyle("-fx-border-color: red; -fx-border-radius: 6;"); // La pintamos de rojo
        }
    }

    private void abrirSistemaPrincipal() {
        try {
            // Cargar tu pantalla principal (NOTA: Cambia "/PuntoVenta.fxml" por el nombre real de tu pantalla base)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PuntoVenta.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Almacenes San Miguel - Sistema POS");
            stage.setScene(new Scene(root));

            // Mostramos el sistema principal
            stage.show();

            // Cerramos la ventana de Login
            Stage loginStage = (Stage) btnIngresar.getScene().getWindow();
            loginStage.close();

        } catch (Exception e) {
            System.err.println("❌ Error al abrir el sistema: " + e.getMessage());
            e.printStackTrace();
        }
    }
}