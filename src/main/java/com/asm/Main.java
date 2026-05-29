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
        // 1. Cargamos el CHASIS principal (que contiene la barra lateral y el hueco para inyectar)
        Parent raiz = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/asm/vista/ContenedorBase.fxml")));

        // 2. Creamos la "Escena" con las dimensiones reales de tu diseño de Figma
        Scene escena = new Scene(raiz, 1366, 768);

        // 3. Configuramos la ventana de Windows
        escenarioPrincipal.setTitle("Almacenes San Miguel - Sistema de Gestión");
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