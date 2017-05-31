package agenttask.evoj

import net.sourceforge.evoj.ModelType
import net.sourceforge.evoj.core.AbstractModelImpl
import net.sourceforge.evoj.core.InterfaceDescriptor

/**
 * Created on 26.05.2017 21:20
 * @autor Nikita Gorodilov
 */
class InputDataModel(var1: Class<InputDataModel>, var2: Map<String, String>?):
        AbstractModelImpl<MyClassDescriptor>(MyClassDescriptor(var1, var2)) {

    override fun getModelType(): ModelType {
        return ModelType.CLASSES
    }
}