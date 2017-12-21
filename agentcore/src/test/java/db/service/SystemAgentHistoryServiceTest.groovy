package db.service

import db.core.systemagent.SystemAgent
import db.core.systemagent.SystemAgentEventHistory
import db.core.systemagent.SystemAgentEventHistoryService
import db.core.systemagent.SystemAgentService
import objects.StringObjects
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import testbase.AbstractServiceTest

import static junit.framework.Assert.assertTrue

/**
 * @author Nikita Gorodilov
 */
class SystemAgentHistoryServiceTest extends AbstractServiceTest {

    @Autowired
    private SystemAgentEventHistoryService systemAgentEventHistoryService
    @Autowired
    private SystemAgentService systemAgentService

    /**
     * Создание и получение записи истории
     */
    @Test
    void testCreatedHistory() {
        def historyId = createHistory()
        def historyList = systemAgentEventHistoryService.getLastNumberItems(1L)

        assertTrue(historyList.size() == 1 && historyId == historyList[0].id)
    }

    /**
     * Создание и получение последний записей истории
     */
    @Test
    void testGetLastNumberCreatedHistory() {
        def historySize = 5
        def createHistoryIds = createHistory(historySize)
        def historyList = systemAgentEventHistoryService.getLastNumberItems(historySize)

        assertTrue(historyList.size() == createHistoryIds.size())
        historyList.forEach {
            assertTrue(createHistoryIds.any { createHistoryId ->
                createHistoryId == it.id
            })
        }
    }

    private def createHistory(Long size) {
        List<Long> historyIds = new ArrayList<>()
        for (i in 0..size - 1) {
            historyIds.add(createHistory())
        }
        historyIds
    }

    private def createHistory() {
        def history = new SystemAgentEventHistory(
                createAgent().id,
                StringObjects.randomString()
        )
        systemAgentEventHistoryService.create(history)
    }

    private SystemAgent createAgent() {
        def systemAgent = new SystemAgent(
                StringObjects.randomString(),
                StringObjects.randomString(),
                true
        )
        systemAgent.isDeleted = false

        return systemAgentService.get(systemAgentService.create(systemAgent))
    }
}
