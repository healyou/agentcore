package agenttask.testlibs.evoj

import net.sourceforge.evoj.core.annotation.Range

/**
 * Created on 20.05.2017 19:12
 * @author Nikita Gorodilov
 */
interface Solution {

    @get:Range(min = "-10", max = "10")
    val x: Double

    @get:Range(min = "-10", max = "10")
    val y: Double

    /**
     * @ListParams — задающую длину списка и некоторые другие свойства, и
     * @MutationRange — задающую максимальный радиус мутации переменной, т.е. на сколько максимально переменная может измениться за один акт мутации.
     * Так же обратите внимание, что переменные описывающие компоненты цвета имеют жестко заданную область значений (strict=”true”).
     */
}