package io.github.kei_1111.admin.shared.model.portfolio.serialization

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * kotlinx-collections-immutable はシリアライザを同梱しないため、List への委譲で ImmutableList を扱う自作シリアライザ。
 */
class ImmutableListSerializer<T>(elementSerializer: KSerializer<T>) : KSerializer<ImmutableList<T>> {
    private val delegate = ListSerializer(elementSerializer)

    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(encoder: Encoder, value: ImmutableList<T>) = delegate.serialize(encoder, value)

    override fun deserialize(decoder: Decoder): ImmutableList<T> = delegate.deserialize(decoder).toImmutableList()
}
