package agentfoundation.localdatabase.base;

import java.sql.SQLException;

/**
 * Created by user on 21.02.2017.
 */
public interface IAgentDatabase {

    public void addSolution() throws SQLException;
    public void addCollectiveSolution() throws SQLException;

}
