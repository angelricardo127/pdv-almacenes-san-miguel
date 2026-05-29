package com.asm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class Main extends Application {

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {
        // 1. Cargamos DIRECTAMENTE tu nuevo archivo de Seguridad
        Parent raiz = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/asm/vista/Seguridad.fxml")));

        // 2. Creamos la "Escena" con las dimensiones grandes de tu Figma (1366x768)
        Scene escena = new Scene(raiz, 1366, 768);

        // 3. Configuramos la ventana de Windows con el título de tu nuevo módulo
        escenarioPrincipal.setTitle("Almacenes San Miguel - Gestión de Accesos");
        escenarioPrincipal.setScene(escena);

        // Permitimos redimensionar temporalmente para ver que todo se acomode bien
        escenarioPrincipal.setResizable(true);

        // 4. ¡Luces, cámara, acción!
        escenarioPrincipal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}