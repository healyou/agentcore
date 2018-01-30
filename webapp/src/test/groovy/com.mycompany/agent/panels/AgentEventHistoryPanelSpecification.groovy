package com.mycompany.agent.panels

import com.mycompany.base.WebPageSpecification
import com.mycompany.db.core.systemagent.SystemAgentEventHistory
import com.mycompany.objects.SystemAgentEventHistoryObjects
import org.apache.wicket.markup.Markup
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.model.AbstractReadOnlyModel

/**
 * @author Nikita Gorodilov
 */
class AgentEventHistoryPanelSpecification extends WebPageSpecification {

    def "Панель отображается в соответствии с моделью"() {
        when:
        def historys = SystemAgentEventHistoryObjects.eventHistorys()
        tester.startPage(new TestPage(historys))

        then:
        historys.eachWithIndex { value, index ->
            def history = value
            tester.assertModelValue("panel:listViewContainer:listView:$index:id", history.id)
            tester.assertModelValue("panel:listViewContainer:listView:$index:createDate", history.createDate)
            tester.assertModelValue("panel:listViewContainer:listView:$index:message", history.message)
        }

        and:
        !historys.isEmpty()
    }

    class TestPage extends WebPage {
        TestPage(List<SystemAgentEventHistory> historys) {
            add(new AgentEventHistoryPanel("panel", new AbstractReadOnlyModel<List<SystemAgentEventHistory>>() {
                @Override
                List<SystemAgentEventHistory> getObject() {
                    return historys
                }
            }))
        }

        @Override
        Markup getAssociatedMarkup() {
            return Markup.of("<div wicket:id='panel'></div>")
        }
    }
}

