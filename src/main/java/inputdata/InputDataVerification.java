package inputdata;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by user on 16.02.2017.
 * Класс необходим для проверки соединения с базой данных пользователя
 * где будут храниться входные данные агента
 *
 * @autor Nikita Gorodilov
 */
public interface InputDataVerification {

    /**
     * @param propPath jdbc настройки .properties
     * @return Вернёт класс, для работы с бд
     * @throws Exception если была ошибка доступа к данным
     */
    public JdbcTemplate getJdbcTemplate(String propPath) throws Exception;

    /**
     * @param descFileName xml файл с данные о таблице в бд
     * @return все данные о таблице в базе данных
     * @throws Exception если при чтении была какая то ошибка
     */
    public InputDataTableDesc getDatabaseTables(String descFileName) throws Exception;

    /**
     * @param jdbcTemplate класс, для работы с jdbc бд
     * @param tableDesc описание таблицы в бд
     * @throws Exception если не смогли считать запись
     */
    public void testReadDbData(JdbcTemplate jdbcTemplate, InputDataTableDesc tableDesc) throws Exception;

}
