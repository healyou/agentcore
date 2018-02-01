package com.mycompany.desktopapp.page

import com.mycompany.desktopapp.AgentGui
import com.mycompany.desktopapp.AgentSpringFxmlLoader
import com.mycompany.desktopapp.AuthenticatedJavaFxSession
import javafx.event.ActionEvent
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.stage.Stage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import com.mycompany.user.Authority

/**
 * Сцена авторизации пользователя
 *
 * @author Nikita Gorodilov
 */
@Component
class LoginGuiController {

    @Autowired
    private lateinit var authenticatedSession: AuthenticatedJavaFxSession

    @FXML
    lateinit var loginTextField: TextField;
    @FXML
    lateinit var passwordTextField: TextField;
    @FXML
    lateinit var loginButton: Button
    @FXML
    lateinit var errorLabel: Label

    companion object {
        val LOGIN_ERROR_MESSAGE = "Ошибка авторизации"
        val NO_AUTHORITY_ERROR_MESSAGE = "Недостаточно прав для работы"
    }

    fun initialize() {
        configureLoginButton()
    }

    private fun configureLoginButton() {
        loginButton.setOnAction { event ->
            if (isSuccessLogin()) {
                showAgentSceneIfHasAuthorityOrShowErrorMessage(event)
            } else {
                showErrorMessage(LOGIN_ERROR_MESSAGE)
            }
        }
    }

    private fun showAgentSceneIfHasAuthorityOrShowErrorMessage(event: ActionEvent) {
        if (isHasAgentSceneAuthority()) {
            clearErrorMessage()
            showAgentScene(event)
        } else {
            showErrorMessage(NO_AUTHORITY_ERROR_MESSAGE)
        }
    }

    private fun isHasAgentSceneAuthority(): Boolean {
        return authenticatedSession.principal.authorities.containsAll(
                arrayListOf(Authority.CREATE_OWN_AGENT, Authority.EDIT_OWN_AGENT, Authority.VIEW_OWN_AGENT, Authority.VIEW_ALL_AGENTS)
        )
    }

    private fun isSuccessLogin(): Boolean {
        return try {
            val login = loginTextField.text
            val password = passwordTextField.text
            authenticatedSession.authenticate(login, password)
        } catch (e: Exception) {
            false
        }
    }

    private fun showErrorMessage(message: String) {
        errorLabel.text = message
        errorLabel.isVisible = true
    }

    private fun clearErrorMessage() {
        errorLabel.text = ""
        errorLabel.isVisible = false
    }

    private fun showAgentScene(event: ActionEvent) {
        val loader = AgentGui.applicationContext.getBean(AgentSpringFxmlLoader::class.java)
        val parent = loader.load(javaClass.getResourceAsStream("gui.fxml"))
        val appWindow = (event.source as Node).scene.window as Stage
        appWindow.title = "Сообщения агента"
        appWindow.scene = Scene(parent, 800.0, 600.0)
        appWindow.show()
    }
}