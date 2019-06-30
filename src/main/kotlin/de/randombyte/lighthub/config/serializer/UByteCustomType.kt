package de.randombyte.lighthub.config.serializer

import com.typesafe.config.Config
import io.github.config4k.ClassContainer
import io.github.config4k.CustomType
import io.github.config4k.toConfig

object UByteCustomType : CustomType {
    override fun testParse(clazz: ClassContainer) = clazz.mapperClass == UByte::class

    override fun testToConfig(obj: Any) = UByte::class.isInstance(obj)

    override fun parse(clazz: ClassContainer, config: Config, name: String) = config.getInt(name).toUByte()

    override fun toConfig(obj: Any, name: String) = (obj as UByte).toString().toConfig(name)
}