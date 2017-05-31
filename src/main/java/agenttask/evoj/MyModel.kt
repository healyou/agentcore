package agenttask.evoj

import net.sourceforge.evoj.ElementDescriptor
import net.sourceforge.evoj.Model
import net.sourceforge.evoj.ModelType
import net.sourceforge.evoj.core.*
import net.sourceforge.evoj.util.ThreadLocalRandom
import java.util.*

/**
 * Created on 26.05.2017 23:26
 * @autor Nikita Gorodilov
 */
class MyModel() : Model<SimpleDescriptor> {

    lateinit private var rootDescriptor: SimpleDescriptor

    lateinit private var allMutable: List<ElementDescriptor>
    lateinit private var mutableDescriptors: TreeMap<Int, ElementDescriptor>
    lateinit private var simpleDescriptors: MutableList<SimpleDescriptor>

    private val DESCR_COMPARATOR: Comparator<ElementDescriptor> = Comparator { var1, var2 -> var1.offset - var2.offset }
    private var maxRnd = 0

    init {
        // что делать с дескрипторами??? что они делают как их переопределять
        // как они связаны с друг другом вообще?

        initialize()
    }

    private fun initialize() {
        maxRnd = 0
        mutableDescriptors = TreeMap()
        simpleDescriptors = ArrayList()
        scan(this.rootDescriptor)
        allMutable = ArrayList(this.mutableDescriptors.values)
        Collections.sort<SimpleDescriptor>(simpleDescriptors, DESCR_COMPARATOR)
        Collections.sort<ElementDescriptor>(allMutable, DESCR_COMPARATOR)
        allMutable = Collections.unmodifiableList<ElementDescriptor>(allMutable)
        simpleDescriptors = Collections.unmodifiableList<SimpleDescriptor>(simpleDescriptors)
    }

    override fun getSimpleDescriptors(): MutableList<out ElementDescriptor> {
        return simpleDescriptors
    }

    override fun getRootDescriptor(): SimpleDescriptor {
        return rootDescriptor
    }

    override fun getMutableDescriptorCount(): Int {
        return mutableDescriptors.size
    }

    override fun getSize(): Int {
        return rootDescriptor.size
    }

    override fun selectRandomMutableDescriptor(): ElementDescriptor? {
        if (this.mutableDescriptors.isEmpty()) {
            return null
        } else {
            val var1 = ThreadLocalRandom.get()
            val var2 = var1.nextInt(this.maxRnd)
            val var3 = this.mutableDescriptors.floorEntry(Integer.valueOf(var2))
            if (var3 == null) {
                throw IllegalStateException("Invariant is broken")
            } else {
                return var3.value
            }
        }
    }

    override fun getAllMutableDescriptors(): MutableList<ElementDescriptor> {
        return mutableDescriptors.values.toMutableList()
    }

    override fun getModelType(): ModelType {
        return ModelType.CLASSES
    }

    override fun overrideContext(context: MutableMap<String, String>?) {
        rootDescriptor.context.override(context)
        this.rootDescriptor.refreshConfig()
        initialize()
    }

    private fun scan(descriptor: SimpleDescriptor) {
        if (descriptor.isMutable && descriptor.mutationAffinity != 0L) {
            this.mutableDescriptors.put(Integer.valueOf(this.maxRnd), descriptor)
            this.maxRnd = (this.maxRnd.toLong() + descriptor.mutationAffinity).toInt()
        }

        this.simpleDescriptors.add(descriptor)
    }

}