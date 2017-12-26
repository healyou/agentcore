package objects.initdbobjects

import db.core.file.dslfile.DslFileContentRef
import db.core.systemagent.SystemAgent
import testbase.CreateDatabaseDataDao

/**
 * @author Nikita Gorodilov
 */
class AgentsObjects {

    companion object {
        @JvmStatic
        fun testAgentWithOneDslAttachment(): SystemAgent {
            return CreateDatabaseDataDao.testAgentWithOneDslAttachment!!
        }

        @JvmStatic
        fun testAgentWithManyDslAttachment(): SystemAgent {
            return CreateDatabaseDataDao.testAgentWithManyDslAttachment!!
        }
    }
}