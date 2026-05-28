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
    private InventarioService servicio;
    private ObservableList<Producto> listaProductos;

    @FXML
    public void initialize() {
        System.out.println("Iniciando el Módulo de Inventario");

        try {
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");

            configuration.addAnnotatedClass(com.asm.modelo.Producto.class);
            configuration.addAnnotatedClass(com.asm.modelo.Talla.class);
            configuration.addAnnotatedClass(com.asm.modelo.Genero.class);

            SessionFactory factory = configuration.buildSessionFactory();
            servicio = new InventarioService(factory);

            configurarColumnas();
            cargarDatosEnTabla();

        } catch (Exception e) {
            System.err.println("Error al cargar el Inventario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarColumnas() {
        colSku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colVariantes.setCellValueFactory(new PropertyValueFactory<>("variantes"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

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

        colEstado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    javafx.scene.control.Label etiqueta = new javafx.scene.control.Label(item);
                    String estiloBase = "-fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 20; ";

                    if (item.equals("Disponible")) {
                        etiqueta.setStyle(estiloBase + "-fx-background-color: #E6FFFA; -fx-text-fill: #38A169;");
                    } else if (item.equals("Stock Bajo")) {
                        etiqueta.setStyle(estiloBase + "-fx-background-color: #FFFAF0; -fx-text-fill: #DD6B20;");
                    } else {
                        etiqueta.setStyle(estiloBase + "-fx-background-color: #FFF5F5; -fx-text-fill: #E53E3E;");
                    }

                    setGraphic(etiqueta);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });

        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnModificar = new Button("Modificar");

            {
                btnModificar.setStyle("-fx-background-color: #4299E1; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand; -fx-font-size: 11px; -fx-font-weight: bold;");
                btnModificar.setOnAction(event -> {
                    Producto prodSeleccionado = getTableView().getItems().get(getIndex());

                    try {
                        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/asm/vista/FormularioModificarProducto.fxml"));
                        javafx.scene.Parent root = loader.load();

                        FormularioModificarProductoController formCtrl = loader.getController();
                        formCtrl.cargarDatosProducto(prodSeleccionado, InventarioController.this, servicio);

                        javafx.stage.Stage stage = new javafx.stage.Stage();
                        stage.setTitle("Modificar Producto - " + prodSeleccionado.getSku());
                        stage.setScene(new javafx.scene.Scene(root));
                        stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                        stage.showAndWait();

                    } catch (Exception e) {
                        System.err.println("Error al abrir ventana de modificación: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(btnModificar);
                    box.setStyle("-fx-alignment: center;");
                    setGraphic(box);
                }
            }
        });
    }

    public void cargarDatosEnTabla() {
        List<Producto> productosBD = servicio.obtenerCatalogoCompleto();

        if (productosBD == null) {
            System.out.println(" Módulo Inventario: ¡La lista que regresó Hibernate es NULL!");
        } else {
            System.out.println(" Módulo Inventario: Hibernate encontró " + productosBD.size() + " productos en MySQL.");
        }

        if (productosBD != null) {
            listaProductos = FXCollections.observableArrayList(productosBD);
            tablaInventario.setItems(listaProductos);
        }
    }

    @FXML
    public void abrirFormularioRegistro() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/asm/vista/FormularioProducto.fxml"));
            javafx.scene.Parent root = loader.load();

            FormularioProductoController formCtrl = loader.getController();
            formCtrl.setDependencias(this, this.servicio);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Registrar Nuevo Producto");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            System.err.println("Error al abrir el formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }
}