package agentfoundation;

import database.dao.InputDataDao;
import database.dto.DtoEntityImpl;

import java.sql.SQLException;
import java.util.Random;

/**
 * Created by user on 21.02.2017.
 */
public class TestAgentBrainImpl extends IAgentBrain {

    private InputDataDao mDao;
    private DtoEntityImpl mEntity;
    private String mOutputValue;

    public TestAgentBrainImpl(InputDataDao dao) {
        mDao = dao;
    }

    @Override
    public void takeInputData() {
        try {
            mEntity = mDao.getFirst();
            //if (mEntity != null)
            //    mDao.delete(mEntity);
        } catch (SQLException e) {
            System.out.println(e.toString());
            setChanged();
            notifyObservers(new AgentObserverArg("Ошибка при чтении данных", ObserverArgType.MESSAGE));
        }
    }

    @Override
    public void calculateOutput() {
        Random random = new Random();
        if (random.nextInt(5) == 1) {
            if (mEntity != null)
                mOutputValue = "1 - id=" + mEntity.getValueByColumnName("id");
            else
                mOutputValue = "1";
        }
        else
            mOutputValue = "0";

        setChanged();
        notifyObservers(new AgentObserverArg(mOutputValue, ObserverArgType.OUTPUT_DATA));
    }
}
