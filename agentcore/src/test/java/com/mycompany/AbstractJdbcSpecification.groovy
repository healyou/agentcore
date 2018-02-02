package com.mycompany

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.support.AnnotationConfigContextLoader
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

/**
 * @author Nikita Gorodilov
 */
@ContextConfiguration(loader = AnnotationConfigContextLoader, classes = JdbcTestConfig)
@Transactional
class AbstractJdbcSpecification extends Specification {

    @Autowired
    CreateDatabaseDataDao createDataDao

    def setup() {
        createDataDao.createData()
    }

    def cleanup() {
        createDataDao.clearData()
    }
}
