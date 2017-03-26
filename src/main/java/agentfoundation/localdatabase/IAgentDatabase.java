package agentfoundation.localdatabase;

import database.dto.DtoEntityImpl;
import inputdata.inputdataverification.inputdata.ATableDesc;

import javax.annotation.Nonnull;
import java.sql.SQLException;

/**
 * Created by user on 21.02.2017.
 */
public interface IAgentDatabase {

    public void addSolution(@Nonnull DtoEntityImpl dtoEntity) throws SQLException;
    public void updateSolution(@Nonnull DtoEntityImpl dtoEntity) throws SQLException;
    public void clearDatabase() throws SQLException;
    public @Nonnull ATableDesc getLocalDbTableDesc();

}
