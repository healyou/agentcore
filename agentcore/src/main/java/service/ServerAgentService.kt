package service

import service.objects.Agent
import service.objects.GetAgentsData

/**
 * @author Nikita Gorodilov
 */
interface ServerAgentService {

    fun getCurrentAgent(sessionManager: SessionManager): Agent?

    fun getAgents(sessionManager: SessionManager, data: GetAgentsData): List<Agent>?
}