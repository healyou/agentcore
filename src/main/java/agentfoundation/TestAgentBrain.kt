package agentfoundation

import database.dao.InputDataDao
import java.util.*

/**
 * Created on 31.03.2017 20:36
 * @autor Nikita Gorodilov
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