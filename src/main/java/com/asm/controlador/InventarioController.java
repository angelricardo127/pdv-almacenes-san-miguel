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

public class InventarioController {

    // 1. Conectamos la tabla y sus columnas
    @FXML private TableView<Producto> tablaInventario;
    @FXML private TableColumn<Producto, String> colSku;
    @FXML private TableColumn<Producto, String> colProducto;
    @FXML private TableColumn<Producto, String> colCategoria;
    @FXML private TableColumn<Producto, String> colVariantes;
    @FXML private TableColumn<Producto, Double> colPrecio;
    @FXML private TableColumn<Producto, Integer> colStock;
    @FXML private TableColumn<Producto, String> colEstado;
    @FXML private TableColumn<Producto, Void> colAcciones;
    @FXML private Button btnRegistrarNuevo; //nuevo boton

    private InventarioService servicio;

    // Lista observable que JavaFX necesita para actualizar la tabla en vivo
    private ObservableList<Producto> listaProductos;

    @FXML
    public void initialize() {
        System.out.println("Iniciando el Módulo de Inventario...");

        try {
            // Configuración de base de datos
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            configuration.addAnnotatedClass(com.asm.modelo.Producto.class);
            configuration.addAnnotatedClass(com.asm.modelo.Talla.class);
            configuration.addAnnotatedClass(com.asm.modelo.Genero.class);

            SessionFactory factory = configuration.buildSessionFactory();
            servicio = new InventarioService(factory);

            // 2. Configuramos cómo se van a llenar las celdas
            configurarColumnas();

            // 3. Cargamos los datos de MySQL a la tabla
            cargarDatosEnTabla();

        } catch (Exception e) {
            System.err.println("Error al cargar el Inventario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void configurarColumnas() {
        // 1. Conectamos TODO con los nombres EXACTOS de tu nueva clase Producto
        colSku.setCellValueFactory(new PropertyValueFactory<>("sku"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colVariantes.setCellValueFactory(new PropertyValueFactory<>("variantes"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio")); // Antes decía precioVenta
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock")); // Antes decía stockActual

        // 2. Calculamos el Estado usando el nuevo metodo getStockActual()
        colEstado.setCellValueFactory(cellData -> {
            // AQUÍ ESTABA EL ERROR: Cambiamos getStock() por getStockActual()
            int cantidad = cellData.getValue().getStock(); // Volvemos a getStock()
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

        // 3. Magia visual: Pintar las píldoras de Estado...
        // (DEJA INTACTO EL CÓDIGO QUE YA TENÍAS AQUÍ ABAJO PARA LOS COLORES Y EL BOTÓN DE MODIFICAR)
        // Magia visual: Pintar las píldoras de Estado según el nivel de inventario
        colEstado.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null); // Si la fila está vacía, no dibuja nada
                } else {
                    // Creamos la "píldora" visual
                    javafx.scene.control.Label etiqueta = new javafx.scene.control.Label(item);
                    String estiloBase = "-fx-font-weight: bold; -fx-padding: 4 12; -fx-background-radius: 20; ";

                    // Aplicamos los colores hexadecimales de tu Figma
                    if (item.equals("Disponible")) {
                        etiqueta.setStyle(estiloBase + "-fx-background-color: #E6FFFA; -fx-text-fill: #38A169;");
                    } else if (item.equals("Stock Bajo")) {
                        etiqueta.setStyle(estiloBase + "-fx-background-color: #FFFAF0; -fx-text-fill: #DD6B20;");
                    } else {
                        etiqueta.setStyle(estiloBase + "-fx-background-color: #FFF5F5; -fx-text-fill: #E53E3E;");
                    }

                    // Centramos la píldora en la celda
                    setGraphic(etiqueta);
                    setStyle("-fx-alignment: CENTER;");
                }
            }
        });
        // Magia de JavaFX: Agregar botones de "Modificar" a la columna de Acciones
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnModificar = new Button("Modificar");
            {
                btnModificar.setStyle("-fx-background-color: #4299E1; -fx-text-fill: white; -fx-background-radius: 4; -fx-cursor: hand; -fx-font-size: 11px; -fx-font-weight: bold;");
                btnModificar.setOnAction(event -> {
                    Producto prod = getTableView().getItems().get(getIndex());
                    System.out.println("Has hecho clic en modificar el producto: " + prod.getNombreProducto());
                    // Aquí irá el RF10 (Modificar)
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
        // Obtenemos la lista de la base de datos
        List<Producto> productosBD = servicio.obtenerCatalogoCompleto();

        // --- LÍNEAS DE DIAGNÓSTICO ---
        if (productosBD == null) {
            System.out.println("🚨 Módulo Inventario: ¡La lista que regresó Hibernate es NULL!");
        } else {
            System.out.println("📦 Módulo Inventario: Hibernate encontró " + productosBD.size() + " productos en MySQL.");
        }
        // -----------------------------

        if (productosBD != null) {
            // Convertimos la lista normal de Java a una ObservableList de JavaFX
            listaProductos = FXCollections.observableArrayList(productosBD);
            // Inyectamos la lista en la tabla
            tablaInventario.setItems(listaProductos);
        }
    }

    @FXML //metodo para agregar un nuevo producto
    public void abrirFormularioRegistro() {
        try {
            // Cargamos el diseño del formulario real
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/com/asm/vista/FormularioProducto.fxml"));
            javafx.scene.Parent root = loader.load();

            // Le pasamos las conexiones al formulario (para que pueda actualizar la tabla al guardar)
            FormularioProductoController formCtrl = loader.getController();
            formCtrl.setDependencias(this, this.servicio);

            // Creamos y mostramos la ventana flotante
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.setTitle("Registrar Nuevo Producto");
            stage.setScene(new javafx.scene.Scene(root));
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL); // Esto bloquea la pantalla de atrás hasta que cierres el formulario
            stage.showAndWait();

        } catch (Exception e) {
            System.err.println("Error al abrir el formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }
}