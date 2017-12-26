package objects;

import db.core.file.DslFileContentRef;
import testbase.CreateFilesDao;

/**
 * @author Nikita Gorodilov
 */
public class FilesObjects {

    public static byte[] testDslFile1Data() {
        return CreateFilesDao.Companion.getTestDskFileContentRef1Data();
    }

    public static DslFileContentRef testDslFile1ContentRef() {
        return CreateFilesDao.Companion.getTestDslFileContentRef1();
    }
}
