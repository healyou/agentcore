package gui.table.columns

import com.sun.javafx.property.PropertyReference
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.PropertyValueFactory
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author Nikita Gorodilov
 */
class DateTimeTableColumn<S : Any>(text: String, property: String): TableColumn<S, String>(text) {

    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss")

    init {
        cellValueFactory = object : PropertyValueFactory<S, String>(property) {

            private var columnClass: Class<*>? = null
            private var propertyRef: PropertyReference<Date>? = null

            override fun call(param: CellDataFeatures<S, String>): ObservableValue<String> {
                return getCellDataReflectively(param.value)
            }

            private fun getCellDataReflectively(rowData: S?): ObservableValue<String> {
                if (getProperty() == null || getProperty().isEmpty() || rowData == null) {
                    return SimpleStringProperty("")
                }

                try {
                    if (columnClass == null || columnClass != rowData::class.java) {
                        this.columnClass = rowData.javaClass
                        this.propertyRef = PropertyReference<Date>(rowData.javaClass, getProperty())
                    }

                    if (propertyRef == null) {
                        return SimpleStringProperty("")
                    }

                    return if (propertyRef!!.hasProperty()) {
                        SimpleStringProperty(dateFormat.format(propertyRef!!.getProperty(rowData)))
                    } else {
                        val value = propertyRef!!.get(rowData)
                        SimpleStringProperty(dateFormat.format(value))
                    }

                } catch (e: IllegalStateException) {
                    return SimpleStringProperty("")
                }
            }
        }
    }
}