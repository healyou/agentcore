package agentcore.agentfoundation

import agentcore.database.dao.InputDataDao
import agentcore.database.dto.InputDataDto
import agentcore.database.dto.LocalDataDto
import java.sql.SQLException

/**
 * Created on 31.03.2017 20:02
 * @autor Nikita Gorodilov
 */
abstract class AAgentBrain(protected val mDao: InputDataDao, protected val mDb: AgentDatabaseImpl): IAgentBrain() {

    protected var mInputData: InputDataDto? = null

    override fun takeInputData() {
        try {
            mInputData = mDao.first
            mDao.delete(mInputData!!)

        } catch (e: Exception) {
            mInputData = null

            setChanged()
            notifyObservers(AgentObserverArg("Ошибка при чтении данных", ObserverArgType.MESSAGE))
        }
    }

    override fun calculateOutput() {
        val localDataDto = LocalDataDto.valueOf(mInputData ?: return)
        val answerValue = calculateAnswerValue()
        localDataDto.setAnswerValue(answerValue)

        try {
            mDb.addSolution(localDataDto)

            setChanged()
            notifyObservers(AgentObserverArg(localDataDto, ObserverArgType.OUTPUT_DATA))

        } catch (e: SQLException) {
            setChanged()
            notifyObservers(AgentObserverArg("ошибка добавления записи при решении задачи в лок бд",
                    ObserverArgType.MESSAGE))
        }
    }

    abstract protected fun calculateAnswerValue(): String
}