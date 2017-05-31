package agenttask.evoj

import net.sourceforge.evoj.Context
import net.sourceforge.evoj.ElementDescriptor
import net.sourceforge.evoj.PropertyMethods
import net.sourceforge.evoj.core.*
import net.sourceforge.evoj.reflection.TypeInfo
import java.lang.reflect.Method
import java.util.*

/**
 * Created on 26.05.2017 21:21
 * @autor Nikita Gorodilov
 */
class MyClassDescriptor(p0: ElementDescriptor?, p1: TypeInfo?, p2: MutableMap<Class<Any>, Annotation>?, p3: Context?, p4: Int, p5: String?, p6: PropertyMethods?):
        ComplexClassDescriptor(p0, p1, p2, p3, p4, p5, p6) {

    private val METHOD_COMPARATOR = Comparator<Method> { var1, var2 -> var1.toGenericString().compareTo(var2.toGenericString()) }
    private val memberMap = mutableMapOf<Method, ElementDescriptor>()

    constructor(context: Map<String, String>): this(createTypeInfo(), context)
    constructor(var1: TypeInfo, var2: Map<String, String>): this(var1, var2, null)
    constructor(var1: TypeInfo, var2: Map<String, String>, var3: String?): this(null, var1, null, Context(var2), 0, var3, null) {
        initialize(var1)
    }

    companion object {
        private fun createTypeInfo(): TypeInfo {
            return TypeInfo(MyModel::class.java, null)
        }
    }

    private fun initialize(typeInfo: TypeInfo) {
        this.pojo = !typeInfo.isInterface

        this.scanMembers()
        this.scanForPojos()
    }

    // TODO надо дописать этот метод
    // смотреть public class InterfaceDescriptor<E> extends ComplexClassDescriptor
    private fun scanMembers() {
        val var1 = LinkedHashMap()
        this.log("Scanning for properties")
        this.fillProps(var1)
        this.log("Validating properties")
        this.validateProps(var1)
        this.log("Creating members")
        this.createMembers(var1)
        this.log("Calculating size")
        this.calcSize()
    }
}