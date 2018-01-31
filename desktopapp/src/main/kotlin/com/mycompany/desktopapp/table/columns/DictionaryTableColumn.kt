package com.mycompany.desktopapp.table.columns

import com.mycompany.db.base.Codable
import com.sun.javafx.property.PropertyReference
import com.mycompany.db.base.IDictionary
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.ObservableValue
import javafx.scene.control.TableColumn
import javafx.scene.control.cell.PropertyValueFactory

/**
 * @author Nikita Gorodilov
 */
class DictionaryTableColumn<S : Any, out T: IDictionary<Codable<out Any>>>(text: String, property: String): TableColumn<S, String>(text) {

    init {
        cellValueFactory = object : PropertyValueFactory<S, String>(property) {

            private var columnClass: Class<*>? = null
            private var propertyRef: PropertyReference<T>? = null

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
                        this.propertyRef = PropertyReference<T>(rowData.javaClass, getProperty())
                    }

                    if (propertyRef == null) {
                        return SimpleStringProperty("")
                    }

                    return if (propertyRef!!.hasProperty()) {
                        SimpleStringProperty(propertyRef!!.getProperty(rowData).value.name)
                    } else {
                        val value = propertyRef!!.get(rowData)
                        SimpleStringProperty(value.name)
                    }

                } catch (e: IllegalStateException) {
                    return SimpleStringProperty("")
                }
            }
        }
    }
}