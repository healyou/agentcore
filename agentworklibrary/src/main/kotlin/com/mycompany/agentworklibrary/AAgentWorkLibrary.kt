package com.mycompany.agentworklibrary


/**
 * Класс, предоставляющий возможность отправки локальных сообщений агента в dsl
 * @author Nikita Gorodilov
 */
open class AAgentWorkLibrary {

    companion object {
        /**
         * Класс, который будет реализован сторонним разработчиком для выполнения функций агента
         */
        protected val AGENT_WORK_LIBRARY_CLASS_NAME = "com.mycompany.AgentWorkLibraryImpl"
        /**
         * Класс, который будет реализован в многоагентной системе для управления работой агента
         */
        protected val WORK_CONTROL_CLASS_NAME = "com.mycompany.dsl.loader.RuntimeAgentWorkControl"

        private var workControl: ILibraryAgentWorkControl? = null

        /**
         * @return класс с функциями работы агента
         */
        @JvmStatic
        @Throws(ClassNotFoundException::class)
        fun findAgentWorkLibraryClass(): Class<*> {
            return Class.forName(AGENT_WORK_LIBRARY_CLASS_NAME)
        }

        /**
         * Локальное событие агента
         * Данные события будут переданы в dsl.onGetLocalMessage
         * @param event событие, произошедшее с агентом(в dsl надо будет идентифицировать данную строку)
         */
        fun onAgentEvent(agentId: Long, event: String) {
            workControl?.onAgentEvent(agentId, event)
        }

        /**
         * Поиск класса, для передачи управления в многоагентную систему из библиотеки функций агента
         * @return класс, отвечающий за работу с агентами в многоагентной системе
         */
        @Throws(ClassNotFoundException::class)
        fun getAgentWorkControl(): ILibraryAgentWorkControl {
            if (workControl == null) {
                val getInstance = Class.forName(WORK_CONTROL_CLASS_NAME).getMethod("getInstance")
                workControl = getInstance.invoke(null) as ILibraryAgentWorkControl
                return workControl as ILibraryAgentWorkControl
            }
            return workControl as ILibraryAgentWorkControl
        }
    }
}