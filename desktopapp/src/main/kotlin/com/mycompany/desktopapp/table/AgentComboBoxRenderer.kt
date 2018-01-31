package com.mycompany.desktopapp.table

import com.mycompany.db.core.systemagent.SystemAgent
import javafx.scene.control.ListCell

/**
 * Отображение наименования агента в комбо боксе
 *
 * @author Nikita Gorodilov
 */
class AgentComboBoxRenderer : ListCell<SystemAgent>() {

    override fun updateItem(item: SystemAgent?, empty: Boolean) {
        super.updateItem(item, empty)

        text = if (!empty && item != null) {
            "Агент №${item.id} - ${item.serviceLogin}"
        } else {
            null
        }
    }
}