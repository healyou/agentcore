package com.mycompany.agent.panels

import com.mycompany.base.converter.SqliteDateConverter
import com.mycompany.db.core.servicemessage.ServiceMessage
import com.mycompany.db.core.systemagent.SystemAgentEventHistory
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.list.ListItem
import org.apache.wicket.markup.html.list.ListView
import org.apache.wicket.markup.html.panel.Panel
import org.apache.wicket.model.IModel
import org.apache.wicket.model.PropertyModel
import org.apache.wicket.util.convert.IConverter
import java.util.*

/**
 * Панель отображения списка системных сообщений агента на одной странице
 *
 * @author Nikita Gorodilov
 */
class AgentEventHistoryPanel(id: String, private val model: IModel<List<SystemAgentEventHistory>>): Panel(id) {

    override fun onInitialize() {
        super.onInitialize()
        val listViewContainer = WebMarkupContainer("listViewContainer")
        listViewContainer.add(object : ListView<SystemAgentEventHistory>("listView", model) {
            override fun populateItem(item: ListItem<SystemAgentEventHistory>) {
                val history = item.modelObject

                item.add(Label("id", PropertyModel.of<String>(history, "id")))
                item.add(object : Label("createDate", PropertyModel.of<Date>(history, "createDate")) {
                    override fun <C : Any> getConverter(type: Class<C>): IConverter<C> {
                        return SqliteDateConverter() as IConverter<C>
                    }
                })
                item.add(Label("message", PropertyModel.of<String>(history, "message")))
            }
        })

        add(listViewContainer.setOutputMarkupId(true))
    }
}