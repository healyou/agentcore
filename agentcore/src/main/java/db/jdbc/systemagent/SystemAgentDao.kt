package db.jdbc.systemagent

import db.core.systemagent.SystemAgent

/**
 * @author Nikita Gorodilov
 */
interface SystemAgentDao {

    fun get(isDeleted: Boolean): List<SystemAgent>
}