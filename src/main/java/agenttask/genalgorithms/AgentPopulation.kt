package agenttask.genalgorithms

import genetics.population.*

/**
 * @author Nikita Gorodilov
 */
class AgentPopulation<out T : Creature>(n: Int, chooses: Double, mutates: Double, c: T, ch: Choosing, sel: Selecting, st: Stopping)
    : Population(n, chooses, mutates, c, ch, sel, st) {

    @Suppress("UNCHECKED_CAST")
    override fun getAnswerCreature(): T {
        return super.getAnswerCreature() as T
    }
}