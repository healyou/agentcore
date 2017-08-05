package service

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

/**
 * Компонент хранит экземпляр сесии - 1 экземпляр на все сервисы
 *      Работает только для 1го активного потока
 *      Передавать в каждый метод SessionManager и збс
 *
 * @author Nikita Gorodilov
 */
open class SessionManager(var cookie: MutableList<String> = mutableListOf())