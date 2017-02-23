package agentfoundation.agentcomandanalizer;

import agentcommunication.AgentCommunicationImpl;
import agentcommunication.message.ServerMessage;
import agentfoundation.agentbrain.AgentBrainImpl;
import agentfoundation.agentcomandanalizer.base.IComAnalizer;
import database.dao.LocalDataDao;
import database.dto.DtoEntityImpl;
import inputdata.inputdataverification.inputdata.InputDataTableDesc;

import javax.naming.OperationNotSupportedException;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by user on 21.02.2017.
 */
public class ComAnalizerImpl implements IComAnalizer, Observer {

    private InputDataTableDesc tableDesc;

    public ComAnalizerImpl(InputDataTableDesc tableDesc) {
        this.tableDesc = tableDesc;
    }

    /**
     * Получение выходного сигнала с мозга агента
     * @param o от кого получен(AgentCommunicationImpl or AgentBrainImpl)
     * @param arg аргемент(DtoEntityImpl)
     */
    @Override
    public void update(Observable o, Object arg) {
        if (!(arg instanceof DtoEntityImpl))
            return;

        DtoEntityImpl dtoEntity = (DtoEntityImpl) arg;
        if (o instanceof AgentCommunicationImpl)
            updateAgentCommunication(dtoEntity);
        if (o instanceof AgentBrainImpl)
            updateAgentOutput(dtoEntity);
    }

    /**
     * Сигнал пришёл от модуля вз-ия с сервером
     * @param entity данные с сервера
     */
    private void updateAgentCommunication(DtoEntityImpl entity) {
        throw new UnsupportedOperationException("" +
                "получение общего решения с сервера - сохранения в локальной бд");
    }

    /**
     * Сигнал пришёлот мозга агента
     * @param entity данные решения мозга агента
     */
    private void updateAgentOutput(DtoEntityImpl entity) {
        if (isSendComMessage(entity))
            sendComMassage(entity);
    }

    /**
     * отправка сигнала на модуль вз-ия с сервером
     */
    private void sendComMassage(DtoEntityImpl entity) {
        AgentCommunicationImpl agentCom = AgentCommunicationImpl.getInstance();

        throw new UnsupportedOperationException("отправка сообщения на сервер");
    }

    /**
     * Проерка необходимости отправки сообщения на сервак
     * @param entity данные для отправки
     * @return true - отправляем за помощью - найдём общее решение
     */
    private boolean isSendComMessage(DtoEntityImpl entity) {
        throw new UnsupportedOperationException("" +
                "проверка возможности отправки сообщения на сервер(групповое решение)");
    }

}
