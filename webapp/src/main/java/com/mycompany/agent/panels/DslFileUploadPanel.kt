package com.mycompany.agent.panels

import com.mycompany.base.AjaxLambdaLink
import com.mycompany.db.core.file.dslfile.ByteArrayFileContentRef
import com.mycompany.db.core.file.dslfile.DslFileAttachment
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.markup.html.WebMarkupContainer
import org.apache.wicket.markup.html.basic.Label
import org.apache.wicket.markup.html.form.FormComponentPanel
import org.apache.wicket.markup.html.form.upload.FileUploadField
import org.apache.wicket.model.AbstractReadOnlyModel
import org.apache.wicket.model.util.ListModel
import java.util.*


/**
 * Панель загрузки dsl файла
 *
 * @author Nikita Gorodilov
 */
open class DslFileUploadPanel(id: String): FormComponentPanel<DslFileAttachment>(id) {

    private lateinit var fileInfo: WebMarkupContainer
    private lateinit var upload: FileUploadField

    override fun onInitialize() {
        super.onInitialize()

        fileInfo = object : WebMarkupContainer("fileInfo") {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = isUploadedModelDslFile()
            }
        }
        add(fileInfo.setOutputMarkupPlaceholderTag(true))
        // todo - почему то работает в браузере клик и удаление fileInfo - но в java нажатие не проходит
        fileInfo.add(AjaxLambdaLink<Any>("remove", this::dslFileRemoveClick))
        fileInfo.add(Label("filename", configureFilenameModel()))

        upload = object : FileUploadField("upload", ListModel(ArrayList())) {
            override fun onConfigure() {
                super.onConfigure()
                isVisible = !isUploadedModelDslFile()
            }
        }
        add(upload.setOutputMarkupPlaceholderTag(true))
    }

    override fun convertInput() {
        val fileUpload = upload.fileUpload
        if (modelObject != null || fileUpload == null) {
            convertedInput = modelObject
            return
        }

        val filename = fileUpload.clientFileName
        val data = fileUpload.bytes
        convertedInput = DslFileAttachment(
                filename,
                ByteArrayFileContentRef(filename, data),
                data.size.toLong()
        )
    }

    private fun configureFilenameModel(): AbstractReadOnlyModel<String> {
        return object : AbstractReadOnlyModel<String>() {
            override fun getObject(): String {
                return if (isUploadedModelDslFile()) modelObject.filename else ""
            }
        }
    }

    /**
     * dsl файл загружен в модель
     */
    private fun isUploadedModelDslFile(): Boolean {
        return modelObject != null
    }

    private fun dslFileRemoveClick(target: AjaxRequestTarget) {
        modelObject = null
        updateComponents(target)
    }

    private fun updateComponents(target: AjaxRequestTarget) {
        target.add(fileInfo)
        target.add(upload)
    }
}