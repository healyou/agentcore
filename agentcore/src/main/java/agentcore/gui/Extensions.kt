package agentcore.gui

import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.input.MouseEvent
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.javafx.JavaFx

/**
 * @author Nikita Gorodilov
 */

/* Максимум один эвент одновременно */
fun Node.onClick(action: suspend (MouseEvent) -> Unit) {
    // launch one actor to handle all events on this node
    val eventActor = actor<MouseEvent>(JavaFx) {
        for (event in channel) action(event) // pass event to action
    }
    // install a listener to offer events to this actor
    onMouseClicked = EventHandler { event ->
        eventActor.offer(event)
    }
}