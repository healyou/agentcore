package com.mycompany.objects

import com.mycompany.service.objects.Agent
import com.mycompany.service.objects.AgentType

/**
 * @author Nikita Gorodilov
 */
class AgentObjects {

    static final agent(Long id) {
        new Agent(id, StringObjects.randomString, StringObjects.randomString,
                new AgentType(1L, StringObjects.randomString, StringObjects.randomString, false), new Date(), false)
    }
}
