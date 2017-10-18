package gui.table.columns

import javafx.scene.control.TableColumn
import javafx.scene.control.cell.PropertyValueFactory

/**
 * @author Nikita Gorodilov
 */
class PropertyTableColumn<S, T>(text: String, property: String): TableColumn<S, T>(text) {

    init {
        cellValueFactory = PropertyValueFactory(property)
    }
}