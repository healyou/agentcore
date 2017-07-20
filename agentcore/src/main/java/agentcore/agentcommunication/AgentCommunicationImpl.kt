package agentcore.agentcommunication

import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.Socket
import java.util.*

import java.lang.Thread.interrupted

/**
 * @author Nikita Gorodilov
 */
class AgentCommunicationImpl : Observable(), IAgentCommunication, Runnable {

    private var outputStream: ObjectOutputStream? = null
    private var inputStream: ObjectInputStream? = null
    private var socket: Socket? = null
    private var isConnect = false
    private var thread: Thread? = null

    @Throws(IOException::class)
    override fun sendMassege(message: Message) {
        if (!isConnect)
            return

        outputStream!!.writeObject(message)
        outputStream!!.flush()
    }

    @Throws(IOException::class)
    override fun connect(host: String, port: Int) {
        if (isConnect)
            return

        // создаём подключение
        try {
            socket = Socket(host, port)
        } catch (e: IOException) {
            println(e.toString())
            return
        }

        // получаем потоки ввода вывода
        try {
            val input = socket!!.getInputStream()
            val out = socket!!.getOutputStream()
            outputStream = ObjectOutputStream(out)
            inputStream = ObjectInputStream(input)
        } catch (e: Exception) {
            throw IOException(e.toString() + " Не удалось получить поток вывода.")
        }

        isConnect = true
        startThread()
    }

    @Throws(IOException::class)
    override fun disconnect() {
        if (!isConnect)
            return

        try {
            isConnect = false
            thread!!.interrupt()
            outputStream!!.close()
            inputStream!!.close()
            socket!!.close()
        } catch (e: IOException) {
            throw IOException(e.toString())
        }
    }

    override fun isConnect(): Boolean {
        return isConnect
    }

    /**
     * Получение сообщений от сервера
     */
    override fun run() {
        // наследоваться от потока + сделать чтение нормальным(чтобы читать данные, когда они есть)
        try {
            while (!interrupted()) {
                val obj = inputStream!!.readObject()
                checkServerObject(obj)
            }
        } catch (e: Exception) {
            println(e.toString() + " ошибка чтения данных с сервера")
        }
    }

    /**
     * Проверяет и записывает данные о пришедшем сообщении с сервера
     * @param object присланный объект
     */
    private fun checkServerObject(obj: Any) {
        if (obj is Message) {
            println("Пришло сообщение с сервера AgentCommunicationImpl")
            setChanged()
            notifyObservers(obj)
        }
    }

    /**
     * запускает выполнение в новом потоке
     */
    private fun startThread() {
        thread = Thread(this)
        thread!!.isDaemon = true
        thread!!.start()
    }
}
