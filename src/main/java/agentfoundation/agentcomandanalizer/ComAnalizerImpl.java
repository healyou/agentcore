package agentfoundation.agentcomandanalizer;

import agentcommunication.AgentCommunicationImpl;
import agentcommunication.message.ClientMessage;
import agentcommunication.message.ServerMessage;
import agentfoundation.agentbrain.AgentBrainImpl;
import agentfoundation.agentcomandanalizer.base.IComAnalizer;
import agentfoundation.localdatabase.AgentDatabaseImpl;
import database.dao.LocalDataDao;
import database.dto.DtoEntityImpl;
import inputdata.inputdataverification.inputdata.InputDataTableDesc;

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

    private AgentCommunicationImpl agentCom;
    private InputDataTableDesc tableDesc;
    private LocalDataDao dao;

    public ComAnalizerImpl(InputDataTableDesc tableDesc, AgentCommunicationImpl agentCom,
                           LocalDataDao dao) {
        this.tableDesc = tableDesc;
        this.agentCom = agentCom;
        this.dao = dao;
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

        if (o instanceof AgentCommunicationImpl && arg instanceof ServerMessage)
            updateAgentCommunication((ServerMessage) arg);
        if (o instanceof AgentBrainImpl && arg instanceof DtoEntityImpl)
            updateAgentOutput((DtoEntityImpl) arg);
    }

    /**
     * Сигнал пришёл от модуля вз-ия с сервером
     * сохраняем данные в локальной бд
     * @param message данные с сервера
     */
    private void updateAgentCommunication(ServerMessage message) {
        try {
            DtoEntityImpl dtoEntity = message.getDtoEntity();
            dao.update(dtoEntity);
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
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
     * Проерка необходимости отправки сообщения на сервак
     * @param entity данные для отправки
     * @return true - отправляем за помощью - найдём общее решение
     */
    private boolean isSendComMessage(DtoEntityImpl entity) {
        // находим выходное значение и проверяем его с регулярным выражением
        Object value = entity.getValueByColumnName(AgentDatabaseImpl.ANSWER_COLUMN_NAME);

        Pattern pattern = tableDesc.getComRegExp();
        Matcher matcher = pattern.matcher(value.toString());

        return matcher.matches();
    }

    /**
     * отправка сигнала на модуль вз-ия с сервером
     */
    private void sendComMassage(DtoEntityImpl entity) {
        if (!agentCom.isConnect())
            return;

        try {
            ClientMessage clientMessage = new ClientMessage(entity);
            agentCom.sendMassege(clientMessage);
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

}
