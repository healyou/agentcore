package agentcore.database.dao

import agentcore.database.dto.ABaseDtoEntity

/**
 * Created on 17.02.2017 20:46
 * Нужен для чтение объектов из базы данных,
 * таблица которой будет известна во время исполнения

 * @author Nikita Gorodilov
 */
abstract class ABaseDao<T : ABaseDtoEntity>
