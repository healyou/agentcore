package com.mycompany.agent.panels

import com.mycompany.BootstrapFeedbackPanel
import com.mycompany.base.AjaxLambdaLink
import com.mycompany.dsl.objects.DslImage
import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink
import org.apache.wicket.extensions.ajax.markup.html.modal.ModalWindow
import org.apache.wicket.markup.html.form.Form
import org.apache.wicket.markup.html.form.FormComponentPanel
import org.apache.wicket.markup.html.form.upload.FileUpload
import org.apache.wicket.markup.html.form.upload.FileUploadField
import org.apache.wicket.model.util.ListModel
import org.apache.wicket.request.cycle.RequestCycle
import org.apache.wicket.util.convert.IConverter
import java.util.*

/**
 * Панель загрузка изображения(DslImage)
 * TODO - общий класс загрузки файликов какой нить выделить
 *
 * @author Nikita Gorodilov
 */
abstract class ModalUploadImagePanel(id: String, private val modal: ModalWindow)
    : FormComponentPanel<DslImage>(id) {

    private lateinit var feedback: BootstrapFeedbackPanel
    private lateinit var upload: FileUploadField

    override fun onInitialize() {
        super.onInitialize()

        feedback = BootstrapFeedbackPanel("feedback")
        add(feedback)

        val form = Form<Any>("form")
        add(form)
        upload = FileUploadField("upload", ListModel(ArrayList()))
        form.add(upload.setRequired(true))

        form.add(object : AjaxSubmitLink("save") {
            override fun onSubmit(target: AjaxRequestTarget, form: Form<*>) {
                super.onSubmit(target, form)
                save(target, getImage())
            }

            override fun onError(target: AjaxRequestTarget, form: Form<*>) {
                super.onError(target, form)
                target.add(feedback)
            }
        })
        form.add(AjaxLambdaLink<Any>("cancel", this::closeClick))
    }

    /**
     * @return загруженное изображение
     */
    private fun getImage(): DslImage {
        val fileUpload = upload.fileUpload ?: throw RuntimeException("Нет данных")

        val filename = fileUpload.clientFileName
        val data = fileUpload.bytes
        return DslImage(filename, data)
    }

    override fun convertInput() {
        val fileUpload = upload.fileUpload
        if (fileUpload == null) {
            convertedInput = modelObject
            return
        }

        val filename = fileUpload.clientFileName
        val data = fileUpload.bytes
        convertedInput = DslImage(filename, data)
    }

    abstract fun save(target: AjaxRequestTarget, image: DslImage)

    private fun closeClick(target: AjaxRequestTarget) {
        modal.close(target)
    }
}