package agenttask.agentbrain

import net.sf.clipsrules.jni.Environment

/**
 * @author Nikita Gorodilov
 */
class ClipsEnvironment: net.sf.clipsrules.jni.Environment() {

    init {
        //val currentDir = System.getProperty("user.dir")
        //val inputFilePath = "$currentDir\\libs\\CLIPSJNI.dll"
        //System.load(inputFilePath)
    }
}