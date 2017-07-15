package agentcore.agentfoundation;

import agentcore.agentcommunication.AgentCommunicationImpl;
import agentcore.agentcommunication.IAgentCommunication;
import agentcore.agentcommunication.Message;
import agentcore.agentcommunication.MCollectiveSolution;
import agentcore.agentcommunication.MSearchSolution;
import agentcore.database.dto.LocalDataDto;
import agentcore.database.dto.MessageLocalDataDto;
import agentcore.inputdata.InputDataTableDesc;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 21.02.2017.
 */
public class ComAnalizerImpl implements IComAnalizer, Observer {

    private AgentCommunicationImpl mAgentCom;
    private InputDataTableDesc mTableDesc;
    private AgentDatabaseImpl mAgentDatabase;

    public ComAnalizerImpl(InputDataTableDesc tableDesc, AgentCommunicationImpl agentCom,
                           AgentDatabaseImpl agentDatabase) {
        this.mTableDesc = tableDesc;
        this.mAgentCom = agentCom;
        this.mAgentDatabase = agentDatabase;
    }

    /**
     * Получение выходного сигнала с мозга агента
     * @param o от кого получен(AgentCommunicationImpl or TestAgentBrainImpl)
     * @param arg аргемент(ConfigureEntityImpl)
     */
    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof IAgentCommunication && arg instanceof Message)
            updateAgentCommunication((Message) arg);
        if (o instanceof IAgentBrain && arg instanceof AgentObserverArg) {
            AgentObserverArg a = (AgentObserverArg) arg;

            if (a.getArg() instanceof LocalDataDto)
                updateAgentOutput((LocalDataDto) a.getArg());
        }
    }

    /**
     * Сигнал пришёл от модуля вз-ия с сервером
     * сохраняем данные в локальной бд
     * @param message данные с сервера
     */
    private void updateAgentCommunication(Message message) {
        if (message instanceof MSearchSolution) {
            // пришёл ответ на с сервера-общее решение
            try {
                MSearchSolution solution = (MSearchSolution) message;
                LocalDataDto entity = LocalDataDto.Companion.valueOf(solution.getDtoEntity());
                mAgentDatabase.updateSolution(entity);
            } catch (SQLException e) {
                System.out.println(e.toString() + " ошибка обновления данных локальной бд");
            }
        }

        if (message instanceof MCollectiveSolution) {
            // ищем своё решение по входным данным
            LocalDataDto dtoEntity = LocalDataDto.Companion.valueOf(((MCollectiveSolution) message).getDtoEntity());
            updateSolution(dtoEntity);
            sendComMassage(new MCollectiveSolution(MessageLocalDataDto.Companion.valueOf(dtoEntity),
                    ((MCollectiveSolution) message).getSolutionId()));
        }
    }

    /**
     * Агент вносит свой ответ в collective_answer и отправляет на сервер
     * @param dtoEntity данные, которые надо пересмотреть агенту
     */
    private void updateSolution(LocalDataDto dtoEntity) {
        dtoEntity.setColAnswerValue("1");
        //throw new UnsupportedOperationException("Операция обновления данных для коллективного решения не поддерживается");
    }

    /**
     * Сигнал пришёл от мозга агента
     * @param entity данные решения мозга агента
     */
    private void updateAgentOutput(LocalDataDto entity) {
        if (isSendComMessage(entity)) {
            System.out.println("отправка на сервак смс");
            sendComMassage(new MSearchSolution(MessageLocalDataDto.Companion.valueOf(entity)));
        }
    }

    /**
     * Проерка необходимости отправки сообщения на сервак
     * @param entity данные для отправки
     * @return true - отправляем за помощью - найдём общее решение
     */
    private boolean isSendComMessage(LocalDataDto entity) {
        // находим выходное значение и проверяем его с регулярным выражением
        Object value = entity.getValueByColumnName(AgentDatabaseImpl.ANSWER_COLUMN_NAME);

        Pattern pattern = mTableDesc.getComRegExp();
        Matcher matcher = pattern.matcher(value.toString());

        return matcher.matches();
    }

    /**
     * отправка сигнала на модуль вз-ия с сервером
     */
    private void sendComMassage(Message message) {
        if (!mAgentCom.isConnect())
            return;

        try {
            mAgentCom.sendMassege(message);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }
}
