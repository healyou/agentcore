package com.mycompany.service

import com.mycompany.service.objects.Agent
import com.mycompany.service.objects.GetAgentsData

/**
 * @author Nikita Gorodilov
 */
// todo - serviceAgentService - переименовать
interface ServerAgentService {

    fun isExistsAgent(sessionManager: SessionManager, masId: String): Boolean?

    fun getCurrentAgent(sessionManager: SessionManager): Agent?

    fun getAgents(sessionManager: SessionManager, data: GetAgentsData): List<Agent>?

    fun getAgent(sessionManager: SessionManager, masId: String): Agent?
}