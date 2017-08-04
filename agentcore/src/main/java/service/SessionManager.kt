package service

import org.springframework.stereotype.Component

/**
 * Компонент хранит экземпляр сесии - 1 экземпляр на все сервисы
 *
 * @author Nikita Gorodilov
 */
@Component
open class SessionManager(var cookie: MutableList<String> = mutableListOf())