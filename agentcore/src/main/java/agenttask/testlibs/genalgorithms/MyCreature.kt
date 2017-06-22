package agenttask.testlibs.genalgorithms

import genetics.simplecreature.SimpleCreature
import genetics.simplecreature.SimpleCrossFunction
import genetics.simplecreature.SimpleMutationFunction

/**
 * @author Nikita Gorodilov
 */
class MyCreature(bytes: Int, cross: SimpleCrossFunction, mutation: SimpleMutationFunction):
        SimpleCreature(bytes, cross, mutation) {

    override fun fit(): Double {
        val f = (super.q * super.q + super.q * 2 + 1).toDouble()
        return f
    }
}