package agentfoundation;

import database.dao.InputDataDao;
import database.dto.DtoEntityImpl;
import database.dto.InputDataDto;
import database.dto.LocalDataDto;
import inputdata.ATableDesc;
import inputdata.InputDataTableDesc;

import java.sql.SQLException;
import java.util.Random;

/**
 * Created by user on 21.02.2017.
 */
public class TestAgentBrainImpl extends IAgentBrain {

    private InputDataDao mDao;
    private AgentDatabaseImpl mDb;
    private InputDataDto mInputData;

    public TestAgentBrainImpl(InputDataDao dao, AgentDatabaseImpl db) {
        mDao = dao;
        mDb = db;
    }

    @Override
    public void takeInputData() {
        try {
            mInputData = mDao.getFirst();
            if (mInputData != null)
                mDao.delete(mInputData);
        } catch (Exception e) {
            mInputData = null;
            System.out.println(e.toString());
            setChanged();
            notifyObservers(new AgentObserverArg("Ошибка при чтении данных", ObserverArgType.MESSAGE));
        }
    }

    @Override
    public void calculateOutput() {
        if (mInputData == null)
            return;

        String outValue = "";
        Random random = new Random();
        if (random.nextInt(5) == 1) {
            outValue = "1";
        }
        else
            outValue = "0";

        LocalDataDto localDataDto = LocalDataDto.Companion.valueOf(mInputData);
        localDataDto.setAnswerValue(outValue);

        try {
            mDb.addSolution(localDataDto);
        } catch (SQLException e) {
            System.out.println("ошибка добавления записи при решении задачи в лок бд");
        }

        setChanged();
        notifyObservers(new AgentObserverArg(localDataDto, ObserverArgType.OUTPUT_DATA));
    }
}
