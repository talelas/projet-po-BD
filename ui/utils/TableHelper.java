package ui.utils;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * Helper for common table operations and data binding
 */
public class TableHelper {
    
    /**
     * Generic method to add column to table
     */
    public static <S, T> TableColumn<S, T> createColumn(String columnName, String propertyName, double width) {
        TableColumn<S, T> column = new TableColumn<>(columnName);
        column.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        column.setPrefWidth(width);
        return column;
    }
    
    /**
     * Clear and refresh table data
     */
    public static <S> void refreshTable(TableView<S> table, ObservableList<S> data) {
        table.setItems(data);
        table.refresh();
    }
}
