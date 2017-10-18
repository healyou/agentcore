package gui.table

import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView

/**
 * Строитель таблички по классу
 *
 * @author Nikita Gorodilov
 */
class CustomTableBuilder<S> {

    // TODO проверить работы builder-а

    private val columns = listOf<TableColumn<S, Any>>()
    private var data = FXCollections.observableArrayList<S>()

    fun addColumn(column: TableColumn<S, Any>): CustomTableBuilder<S> {
        columns.plus(column)
        return this
    }

    fun withItems(data: ObservableList<S>): CustomTableBuilder<S> {
        this.data = data
        return this
    }

    fun build(): TableView<S> {
        val table = TableView<S>()

        table.columns.addAll(columns)
        table.items = data

        return table
    }
}