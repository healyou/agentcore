package com.mycompany.agent.panels

import com.mycompany.base.WebPageSpecification
import com.mycompany.db.core.servicemessage.ServiceMessage
import com.mycompany.objects.ServiceMessageObjects
import org.apache.wicket.markup.Markup
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.model.AbstractReadOnlyModel

/**
 * @author Nikita Gorodilov
 */
class ServiceMessagesPanelSpecification extends WebPageSpecification {

    def "Панель отображается в соответствии с моделью"() {
        when:
        def messages = ServiceMessageObjects.serviceMessages()
        tester.startPage(new TestPage(messages))

        then:
        messages.eachWithIndex { value, index ->
            def message = value
            tester.assertModelValue("panel:listViewContainer:listView:$index:id", message.id)
            tester.assertModelValue("panel:listViewContainer:listView:$index:createDate", message.createDate)
            tester.assertModelValue("panel:listViewContainer:listView:$index:useDate", message.useDate)
            tester.assertModelValue("panel:listViewContainer:listView:$index:localMessageType", message.serviceMessageType.name)
            tester.assertModelValue("panel:listViewContainer:listView:$index:serviceMessageType", message.sendMessageType)
        }

        and:
        !messages.isEmpty()
    }

    class TestPage extends WebPage {
        TestPage(List<ServiceMessage> messages) {
            add(new ServiceMessagesPanel("panel", new AbstractReadOnlyModel<List<ServiceMessage>>() {
                @Override
                List<ServiceMessage> getObject() {
                    return messages
                }
            }))
        }

        @Override
        Markup getAssociatedMarkup() {
            return Markup.of("<div wicket:id='panel'></div>")
        }
    }
}
