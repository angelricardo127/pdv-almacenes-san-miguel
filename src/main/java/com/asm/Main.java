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
        // 1. Cargamos DIRECTAMENTE tu archivo de inventario en la raíz
        Parent raiz = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/asm/vista/inventario.fxml")));

        // 2. Creamos la "Escena" con las dimensiones de tu diseño
        Scene escena = new Scene(raiz, 900, 600);

        // 3. Configuramos la ventana de Windows con tus títulos
        escenarioPrincipal.setTitle("Almacenes San Miguel - Almacén e Inventario");
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