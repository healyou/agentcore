package testbase

import org.junit.Before
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.transaction.annotation.Transactional

/**
 * @author Nikita Gorodilov
 */
@RunWith(SpringJUnit4ClassRunner::class)
@ContextConfiguration("classpath:testBeans.xml")
@Transactional("transactionManager")
abstract class AbstractServiceTest:  AbstractTransactionalJUnit4SpringContextTests() {

    @Autowired
    private lateinit var createFilesDao: CreateFilesDao

    @Before
    fun init() {
        createFilesDao.createFiles()
    }
}