package com.asm.controlador;

import com.asm.modelo.Producto;
import com.asm.servicio.InventarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

/**
 * Controlador principal del Módulo de Inventario.
 * Se encarga de gestionar la vista de la tabla de productos, conectar con la base de datos
 * a través de Hibernate y manejar la apertura de ventanas secundarias (como el registro).
 */
public class InventarioController {

    // ---inyeccion de componentes fxml
    // estas variables se enlazan directamente con los IDs definidos en el archivo de diseño
    @FXML private TableView<Producto> tablaInventario;
    @FXML private TableColumn<Producto, String> colSku;
    @FXML private TableColumn<Producto, String> colProducto;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, String> colVariantes;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, String> colEstado;
    @FXML private TableColumn<Producto, Void> colAcciones;
    @FXML private Button btnRegistrarNuevo;

    // --- servicios y estado de la vista
    // clase que contiene la lógica de negocio
    private InventarioService servicio;

    // Lista especial de JavaFx. cualquier cambio en esta lista se refleja automáticamente en la tabla visual.
    private ObservableList<Producto> listaProductos;

    /**
     * Metodo initialize(), es el constructor de la vista.
     * JavaFX lo ejecuta automáticamente justo después de cargar el archivo FXML y enlazar las variables.
     */
    @FXML
    public void initialize() {
        System.out.println("Iniciando el Módulo de Inventario");

        try {
            //. configuracion de la conexión a MySQL usando Hibernate
            Configuration configuration = new Configuration();
            configuration.configure("com/asm/vista/hibernate.cfg.xml"); // lee credenciales y URL

            // registramos las clases que representan tablas en la base de datos
            configuration.addAnnotatedClass(com.asm.modelo.Producto.class);
            configuration.addAnnotatedClass(com.asm.modelo.Talla.class);
            configuration.addAnnotatedClass(com.asm.modelo.Genero.class);

            // construimos la fabrica de sesiones y se la pasamos al servicio
            SessionFactory factory = configuration.buildSessionFactory();
            servicio = new InventarioService(factory);

            //lee decimos a la tabla de JavaFX de dónde sacar la información para cada columna
            configurarColumnas();

            //  hacemos la consulta a la BD y llenamos la tabla
            cargarDatosEnTabla();

        } catch (Exception e) {
            System.err.println("Error al cargar el Inventario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * configura el comportamiento y el diseño interno de cada columna de la tabla.
     */
    private void configurarColumnas() {
        // mapeo básico,conectamos las columnas visuales con los atributos de la clase Producto.java
        // los textos en comillas deben coincidir exactamente con los nombres de las variables en la clase.
        colSku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colVariantes.setCellValueFactory(new PropertyValueFactory<>("variantes"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        // logica de la columna estado gGeneración de texto dinámico)
        // Como 'Estado' no existe en la BD, lo calculamos al vuelo dependiendo de la cantidad de stock.
        colEstado.setCellValueFactory(cellData -> {
            int cantidad = cellData.getValue().getStock();
            String estado;
            if (cantidad > 10) {
                estado = "Disponible";
            } else if (cantidad > 0) {
                estado = "Stock Bajo";
            } else {
                estado = "Agotado";
            }
            return new javafx.beans.property.SimpleStringProperty(estado);
        });

        // 3. transformamos el texto plano de estado en píldoras de colores
        colEstado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null); // Limpia la celda si la fila no tiene datos
                } else {
                    // creamos una etiqueta visual para el estado
                    javafx.scene.control.Label etiqueta = new javafx.scene.control.Label(item);
                    String estiloBase = "-fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 20; ";

                    // aplicamos colores (Fondo y Texto) según el estado calculado
                    if (item.equals("Disponible")) {
                        etiqueta.setStyle(estiloBase + "-fx-background-color: #E6FFFA; -fx-text-fill: #38A169;"); // Verde
                    } else if (item.equals("Stock Bajo")) {
                        etiqueta.setStyle(estiloBase + "-fx-background-color: #FFFAF0; -fx-text-fill: #DD6B20;"); // Naranja
                    } else {
                        etiqueta.setStyle(estiloBase + "-fx-background-color: #FFF5F5; -fx-text-fill: #E53E3E;"); // Rojo
                    }

                    setGraphic(etiqueta); // mostramos la etiqueta en la celda
                    setStyle("-fx-alignment: CENTER;"); // centramos el contenido
                }
            }
        });

        //  botones de accion, creamos botones interactivos dentro de la tabla
        colAcciones.setCellFactory(param -> new TableCell<>() {
            // Creamos el botón una sola vez por celda
            private final Button btnModificar = new Button("Modificar");

            // bloque de inicialización le damos estilo y funcionalidad al botón
            {
                btnModificar.setStyle("-fx-background-color: #4299E1; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand; -fx-font-size: 11px; -fx-font-weight: bold;");
                btnModificar.setOnAction(event -> {
                    // al hacer clic, detectamos en qué fila estamos y obtenemos ese Producto en específico
                    Producto prod = getTableView().getItems().get(getIndex());
                    System.out.println("Has hecho clic en modificar el producto: " + prod.getNombreProducto());
                    // TODO: Aquí se invocará la ventana para el RF10 (Modificar Producto)
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // envolvemos el botón en un contenedor HBox para centrarlo correctamente
                    HBox box = new HBox(btnModificar);
                    box.setStyle("-fx-alignment: center;");
                    setGraphic(box);
                }
            }
        });
    }

    /**
     * Consulta la base de datos a través del servicio y actualiza la vista.
     */
    public void cargarDatosEnTabla() {
        // 1. Pedimos todos los productos (Esto ejecuta un 'SELECT * FROM productos' internamente)
        List<Producto> productosBD = servicio.obtenerCatalogoCompleto();

        // --- LÍNEAS DE DIAGNÓSTICO (Logs para la consola) ---
        if (productosBD == null) {
            System.out.println(" Módulo Inventario: ¡La lista que regresó Hibernate es NULL!");
        } else {
            System.out.println(" Módulo Inventario: Hibernate encontró " + productosBD.size() + " productos en MySQL.");
        }
        // -----------------------------

        // 2. si la consulta fue exitosa, actualizamos la tabla visual
        if (productosBD != null) {
            listaProductos = FXCollections.observableArrayList(productosBD); // Adaptador a JavaFX
            tablaInventario.setItems(listaProductos); // Inyección de datos
        }
    }

    /**
     * Metodo enlazado al botón "Registrar Nuevo Producto"  es el RF09
     * Abre una ventana modal flotante basada en el diseñoque relaizamos en figma
     */
    @FXML
    public void abrirFormularioRegistro() {
        try {
            // 1. Buscamos y cargamos el archivo FXML de la nueva ventana
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/asm/vista/FormularioProducto.fxml"));
            javafx.scene.Parent root = loader.load();

            // 2. Obtenemos el controlador de esa nueva ventana
            FormularioProductoController formCtrl = loader.getController();

            // Le pasamos referencias a ESTE controlador y al servicio.
            // Esto permite que cuando el formulario guarde un producto, pueda decirle a esta tabla que se recargue.
            formCtrl.setDependencias(this, this.servicio);

            // 3. Preparamos el escenario (Stage) para mostrar la ventana
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Registrar Nuevo Producto");
            stage.setScene(new javafx.scene.Scene(root));

            // APPLICATION_MODAL congela la ventana principal que está atrás obligando al usuario a atender el formulario
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);

            // 4. Mostramos la ventana y pausamos la ejecución aquí hasta que el usuario la cierre
            stage.showAndWait();

        } catch (Exception e) {
            System.err.println("Error al abrir el formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }
}