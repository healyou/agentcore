package objects.initdbobjects

import testbase.CreateDatabaseDataDao
import user.User

/**
 * @author Nikita Gorodilov
 */
class UserObjects {

    companion object {

        @JvmStatic
        fun testDeletedUser(): User {
            return CreateDatabaseDataDao.testDeletedUser!!
        }

        @JvmStatic
        fun testNotDeletedUser(): User {
            return CreateDatabaseDataDao.testNotDeletedUser!!
        }
    }
}