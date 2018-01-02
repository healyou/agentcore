package com.mycompany.base

import org.apache.wicket.ajax.AjaxRequestTarget
import org.apache.wicket.ajax.markup.html.AjaxLink
import org.apache.wicket.model.IModel
import java.io.Serializable
import kotlin.reflect.KFunction1


/**
 * @author Nikita Gorodilov
 */
open class AjaxLambdaLink<T>(id: String, model: IModel<T>?, private val action: KFunction1<AjaxRequestTarget, Unit>)
    : AjaxLink<T>(id, model) {
    constructor(id: String, action: KFunction1<AjaxRequestTarget, Unit>) : this(id, null, action)

    override fun onClick(target: AjaxRequestTarget) {
        action(target)
    }
}