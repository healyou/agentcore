package agentcore.agentfoundation

import agentcore.database.dao.InputDataDao
import java.util.*

/**
 * @author Nikita Gorodilov
 */
class TestAgentBrain(mDao: InputDataDao, mDb: AgentDatabaseImpl): AAgentBrain(mDao, mDb) {

    private val random = Random()

    override fun calculateAnswerValue(): String {
        if (random.nextInt(5) == 1) {
            return "1"
        } else
            return "0"
    }
}