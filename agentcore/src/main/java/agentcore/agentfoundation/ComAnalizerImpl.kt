package agentcore.agentfoundation

import agentcore.agentcommunication.AgentCommunicationImpl
import agentcore.agentcommunication.IAgentCommunication
import agentcore.agentcommunication.Message
import agentcore.agentcommunication.MCollectiveSolution
import agentcore.agentcommunication.MSearchSolution
import agentcore.database.dto.LocalDataDto
import agentcore.database.dto.MessageLocalDataDto
import agentcore.inputdata.InputDataTableDesc

import java.io.IOException
import java.sql.SQLException
import java.util.Observable
import java.util.Observer
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * @author Nikita Gorodilov
 */
class ComAnalizerImpl(private val mTableDesc: InputDataTableDesc, private val mAgentCom: AgentCommunicationImpl,
                      private val mAgentDatabase: AgentDatabaseImpl) : IComAnalizer, Observer {

    /**
     * Получение выходного сигнала с мозга агента
     * @param o от кого получен(AgentCommunicationImpl or TestAgentBrainImpl)
     * *
     * @param arg аргемент(ConfigureEntityImpl)
     */
    override fun update(o: Observable, arg: Any) {
        if (o is IAgentCommunication && arg is Message)
            updateAgentCommunication(arg)
        if (o is IAgentBrain && arg is AgentObserverArg) {
            val (arg1) = arg

            if (arg1 is LocalDataDto)
                updateAgentOutput(arg1)
        }
    }

    /**
     * Сигнал пришёл от модуля вз-ия с сервером
     * сохраняем данные в локальной бд
     * @param message данные с сервера
     */
    private fun updateAgentCommunication(message: Message) {
        if (message is MSearchSolution) {
            // пришёл ответ на с сервера-общее решение
            try {
                val entity = LocalDataDto.valueOf(message.dtoEntity)
                mAgentDatabase.updateSolution(entity)
            } catch (e: SQLException) {
                println(e.toString() + " ошибка обновления данных локальной бд")
            }

        }

        if (message is MCollectiveSolution) {
            // ищем своё решение по входным данным
            val dtoEntity = LocalDataDto.valueOf(message.dtoEntity)
            updateSolution(dtoEntity)
            sendComMassage(MCollectiveSolution(MessageLocalDataDto.valueOf(dtoEntity),
                    message.solutionId))
        }
    }

    /**
     * Агент вносит свой ответ в collective_answer и отправляет на сервер
     * @param dtoEntity данные, которые надо пересмотреть агенту
     */
    private fun updateSolution(dtoEntity: LocalDataDto) {
        dtoEntity.setColAnswerValue("1")
        //throw new UnsupportedOperationException("Операция обновления данных для коллективного решения не поддерживается");
    }

    /**
     * Сигнал пришёл от мозга агента
     * @param entity данные решения мозга агента
     */
    private fun updateAgentOutput(entity: LocalDataDto) {
        if (isSendComMessage(entity)) {
            println("отправка на сервак смс")
            sendComMassage(MSearchSolution(MessageLocalDataDto.valueOf(entity)))
        }
    }

    /**
     * Проерка необходимости отправки сообщения на сервак
     * @param entity данные для отправки
     * *
     * @return true - отправляем за помощью - найдём общее решение
     */
    private fun isSendComMessage(entity: LocalDataDto): Boolean {
        // находим выходное значение и проверяем его с регулярным выражением
        val value = entity.getValueByColumnName(AgentDatabaseImpl.ANSWER_COLUMN_NAME)

        val pattern = mTableDesc.comRegExp
        val matcher = pattern.matcher(value!!.toString())

        return matcher.matches()
    }

    /**
     * отправка сигнала на модуль вз-ия с сервером
     */
    private fun sendComMassage(message: Message) {
        if (!mAgentCom.isConnect())
            return

        try {
            mAgentCom.sendMassege(message)
        } catch (e: IOException) {
            println(e.toString())
        }

    }
}
