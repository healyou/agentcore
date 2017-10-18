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

    private val columns = arrayListOf<TableColumn<S, out Any>>()
    private var data = FXCollections.observableArrayList<S>()
    private var table = TableView<S>()

    fun addColumn(column: TableColumn<S, out Any>): CustomTableBuilder<S> {
        columns.add(column)
        return this
    }

    fun withItems(data: ObservableList<S>): CustomTableBuilder<S> {
        this.data = data
        return this
    }

    fun withTable(table: TableView<S>): CustomTableBuilder<S> {
        this.table = table;
        return this
    }

    fun build(): TableView<S> {
        table.columns.addAll(columns)
        table.items = data

        return table
    }
}