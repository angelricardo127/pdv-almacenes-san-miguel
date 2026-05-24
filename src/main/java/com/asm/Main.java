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
        // 1. Cargamos el archivo FXML que acabas de diseñar
        Parent raiz = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/PuntoVenta.fxml")));

        // 2. Creamos la "Escena" y le damos las dimensiones de tu diseño (1366 x 768)
        Scene escena = new Scene(raiz, 1366, 768);

        // 3. Configuramos la ventana de Windows
        escenarioPrincipal.setTitle("Almacenes San Miguel - Punto de Venta");
        escenarioPrincipal.setScene(escena);

        // Opcional: Para que no la puedan hacer chiquita y romper el diseño
        escenarioPrincipal.setResizable(false);

        // 4. ¡Luces, cámara, acción!
        escenarioPrincipal.show();
    }

    public static void main(String[] args) {
        // Este es el método que arranca toda la aplicación
        launch(args);
    }
}