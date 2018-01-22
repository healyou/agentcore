package com.mycompany.agentworklibrary

/**
 * Интерфейс, предоставляющий работу с агентами
 *
 * @author Nikita Gorodilov
 */
interface ILibraryAgentWorkControl {

    /**
     * Локальное событие агента
     * Данные события будут переданы в dsl.onGetLocalMessage
     * @param event событие, произошедшее с агентом(в dsl надо будет идентифицировать данную строку)
     */
    fun onAgentEvent(agentId: Long, event: String)
}