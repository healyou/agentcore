package com.mycompany.agent.panels

import com.mycompany.base.WebPageSpecification
import com.mycompany.db.core.file.dslfile.ByteArrayFileContentRef
import com.mycompany.db.core.file.dslfile.DslFileAttachment
import com.mycompany.objects.DslFileAttachmentObjects
import org.apache.wicket.markup.Markup
import org.apache.wicket.markup.html.WebPage
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.model.CompoundPropertyModel
import org.apache.wicket.util.tester.FormTester

/**
 * @author Nikita Gorodilov
 */
class DslFileUploadPanelSpecification extends WebPageSpecification {

    def dao = new TestDao(dslFile: DslFileAttachmentObjects.dslFileAttachment())

    def "Панель отображается в соответствии с моделью"() {
        when:
        tester.startPage(new TestPage(dao))

        then:
        tester.assertModelValue("testForm:dslFile", dao.dslFile)
    }

    def "При сабмите данные записываются в поля переданного объекта модели"() {
        setup:
        tester.startPage(new TestPage(dao))
        FormTester formTester = tester.newFormTester("testForm")
        def dslFile = dao.dslFile

        when:
        formTester.submit();

        then:
        !formTester.getForm().hasError()
        dao.dslFile == dslFile
    }

    class TestPage extends WebPage {
        TestPage(TestDao testDao) {
            Form form = new Form("testForm", new CompoundPropertyModel(testDao))
            add(form)
            form.add(new DslFileUploadPanel("dslFile"));
        }

        @Override
        Markup getAssociatedMarkup() {
            return Markup.of("<form wicket:id='testForm'><div wicket:id='dslFile'></div></form>")
        }
    }

    class TestDao {
        DslFileAttachment dslFile
    }
}
