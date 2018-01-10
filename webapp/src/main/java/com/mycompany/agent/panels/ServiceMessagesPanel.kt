package com.mycompany.agent.panels

import com.mycompany.base.converter.SqliteDateConverter
import com.mycompany.db.core.servicemessage.ServiceMessage
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
class ServiceMessagesPanel(id: String, private val model: IModel<List<ServiceMessage>>): Panel(id) {

    override fun onInitialize() {
        super.onInitialize()
        val listViewContainer = WebMarkupContainer("listViewContainer")
        listViewContainer.add(object : ListView<ServiceMessage>("listView", model) {
            override fun populateItem(item: ListItem<ServiceMessage>) {
                val message = item.modelObject

                item.add(Label("id", PropertyModel.of<String>(message, "id")))
                item.add(object : Label("createDate", PropertyModel.of<Date>(message, "createDate")) {
                    override fun <C : Any> getConverter(type: Class<C>): IConverter<C> {
                        return SqliteDateConverter() as IConverter<C>
                    }
                })
                item.add(object : Label("useDate", PropertyModel.of<Date>(message, "useDate")) {
                    override fun <C : Any> getConverter(type: Class<C>): IConverter<C> {
                        return SqliteDateConverter() as IConverter<C>
                    }
                })
                item.add(Label("localMessageType", PropertyModel.of<String>(message, "serviceMessageType.name")))
                item.add(Label("serviceMessageType", PropertyModel.of<String>(message, "sendMessageType")))
            }
        })

        add(listViewContainer.setOutputMarkupId(true))
    }
}