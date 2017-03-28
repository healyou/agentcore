package agentfoundation;

import database.dto.DtoEntityImpl;
import database.dto.LocalDataDto;
import inputdata.ATableDesc;

import javax.annotation.Nonnull;
import java.sql.SQLException;

/**
 * Created by user on 21.02.2017.
 */
public interface IAgentDatabase {

    public void addSolution(@Nonnull LocalDataDto dtoEntity) throws SQLException;
    public void updateSolution(@Nonnull LocalDataDto dtoEntity) throws SQLException;
    public void clearDatabase() throws SQLException;
    public @Nonnull ATableDesc getLocalDbTableDesc();

}
