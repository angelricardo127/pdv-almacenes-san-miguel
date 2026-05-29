package com.asm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage escenarioPrincipal) throws Exception {

        // 1. Cargamos tu archivo visual de Login
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent raiz = fxmlLoader.load();

        // 2. Creamos la "Escena" con las medidas de tu login
        Scene escena = new Scene(raiz, 900, 600);

        // 3. Configuramos la ventana de Windows con el título correcto
        escenarioPrincipal.setTitle("Almacenes San Miguel - Inicio de Sesión");
        escenarioPrincipal.setScene(escena);

        // Bloqueamos redimensionar para cuidar el diseño del Login
        escenarioPrincipal.setResizable(false);

        // 4. ¡Luces, cámara, acción!
        escenarioPrincipal.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}