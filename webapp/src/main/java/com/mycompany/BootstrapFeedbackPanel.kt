package com.mycompany

import org.apache.wicket.markup.html.panel.FeedbackPanel

/**
 * @author Nikita Gorodilov
 */
class BootstrapFeedbackPanel(id: String): FeedbackPanel(id) {

    override fun onInitialize() {
        super.onInitialize()
        outputMarkupPlaceholderTag = true
    }

    override fun onConfigure() {
        super.onConfigure()
        isVisible = anyMessage()
    }
}