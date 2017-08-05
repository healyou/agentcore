package service

import org.springframework.stereotype.Component

/**
 * Компонент хранит экземпляр сесии - 1 экземпляр на все сервисы
 *      Работает только для 1го активного потока - todo переписать куки под многопоточность
 *      Передавать в каждый метод SessionManager и збс
 *
 * @author Nikita Gorodilov
 */
@Component
open class SessionManager(var cookie: MutableList<String> = mutableListOf())