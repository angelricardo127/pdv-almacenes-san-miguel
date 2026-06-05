package com.asm.controlador;

import com.asm.modelo.Producto;
import com.asm.servicio.InventarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class InventarioController {

    //  inyeccion de componentes fxml
    @FXML private TextField txtBuscador;
    @FXML private Label lblBienvenida;
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

    // servicios y estado de la vista
    private InventarioService servicio;

    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private FilteredList<Producto> productosFiltrados;

    @FXML
    public void initialize() {
        System.out.println("Iniciando el Módulo de Inventario");

        String fechaHoy = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy", new java.util.Locale("es", "ES")));
        com.asm.modelo.Usuario usuarioLogueado = com.asm.modelo.SesionGlobal.getUsuarioActual();

        if (lblBienvenida != null && usuarioLogueado != null) {
            lblBienvenida.setText("Bienvenido, " + usuarioLogueado.getNombre() + " " + usuarioLogueado.getApellidoPaterno() + " • " + fechaHoy);
        } else if (lblBienvenida != null) {
            lblBienvenida.setText("Bienvenido • " + fechaHoy);
        }

        try {
            Configuration configuration = new Configuration();
            configuration.configure("com/asm/vista/hibernate.cfg.xml");

            configuration.addAnnotatedClass(com.asm.modelo.Producto.class);
            configuration.addAnnotatedClass(com.asm.modelo.Talla.class);
            configuration.addAnnotatedClass(com.asm.modelo.Genero.class);

            SessionFactory factory = configuration.buildSessionFactory();
            servicio = new InventarioService(factory);

            configurarColumnas();

            configurarBuscador();

            cargarDatosEnTabla();

        } catch (Exception e) {
            System.err.println("Error al cargar el Inventario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarBuscador() {
        productosFiltrados = new FilteredList<>(listaProductos, b -> true);

        if (txtBuscador != null) {
            txtBuscador.textProperty().addListener((observable, oldValue, newValue) -> {
                productosFiltrados.setPredicate(producto -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String busqueda = newValue.toLowerCase();

                    if (producto.getNombreProducto().toLowerCase().contains(busqueda)) {
                        return true;
                    } else if (producto.getSku() != null && producto.getSku().toLowerCase().contains(busqueda)) {
                        return true;
                    }
                    return false;
                });
            });
        }

        SortedList<Producto> productosOrdenados = new SortedList<>(productosFiltrados);
        productosOrdenados.comparatorProperty().bind(tablaInventario.comparatorProperty());
        tablaInventario.setItems(productosOrdenados);
    }

    public void cargarDatosEnTabla() {
        List<Producto> productosBD = servicio.obtenerCatalogoCompleto();

        if (productosBD == null) {
            System.out.println(" Módulo Inventario: ¡La lista que regresó Hibernate es NULL!");
        } else {
            System.out.println(" Módulo Inventario: Hibernate encontró " + productosBD.size() + " productos en MySQL.");

            listaProductos.setAll(productosBD);
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

    @FXML
    public void abrirAjusteStock() {
        System.out.println(" El botón sí conectó con el método.");
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/asm/vista/FormularioAjusteStock.fxml"));
            javafx.scene.Parent root = loader.load();

            FormularioAjusteStockController formCtrl = loader.getController();
            formCtrl.setDependencias(this, this.servicio);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Ajuste de Stock");
            stage.setScene(new javafx.scene.Scene(root));

            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            System.err.println(" ERROR AL ABRIR LA VENTANA DE AJUSTE DE STOCK:");
            e.printStackTrace();
        }
    }

    @FXML
    public void abrirGestionVariantes() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/asm/vista/FormularioVariantes.fxml"));
            javafx.scene.Parent root = loader.load();

            FormularioVariantesController formCtrl = loader.getController();
            formCtrl.setDependencias(this, this.servicio);

            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Gestión de Variantes");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            System.err.println("Error al abrir Variantes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}