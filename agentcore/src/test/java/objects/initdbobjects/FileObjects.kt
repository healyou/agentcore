package objects.initdbobjects

import com.mycompany.db.core.file.dslfile.DslFileContentRef
import com.mycompany.CreateDatabaseDataDao

/**
 * @author Nikita Gorodilov
 */
class FileObjects {

    companion object {
        @JvmStatic
        fun testDslFile1Data(): ByteArray {
            return CreateDatabaseDataDao.testDskFileContentRef1Data
        }

        @JvmStatic
        fun testDslFile1ContentRef(): DslFileContentRef? {
            return CreateDatabaseDataDao.testDslFileContentRef1
        }

        @JvmStatic
        fun testDslFile2Data(): ByteArray {
            return CreateDatabaseDataDao.testDskFileContentRef2Data
        }

        @JvmStatic
        fun testDslFile2ContentRef(): DslFileContentRef? {
            return CreateDatabaseDataDao.testDslFileContentRef2
        }
    }
}
