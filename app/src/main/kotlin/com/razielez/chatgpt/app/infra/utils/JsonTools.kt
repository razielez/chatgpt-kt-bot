package com.razielez.chatgpt.app.infra.utils

import com.razielez.chatgpt.app.infra.common.JsonSerializable
import com.razielez.chatgpt.app.infra.common.toJson
import kotlinx.serialization.*
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.util.*

object JsonTools {

    val json = Json {
        ignoreUnknownKeys = true

    }

    //    private val mapper = ObjectMapper()
//
//    init {
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//        val kotlinModule = KotlinModule.Builder()
//            .configure(KotlinFeature.NullToEmptyCollection, true)
//            .configure(KotlinFeature.NullToEmptyMap, true)
//            .configure(KotlinFeature.NullIsSameAsDefault, true)
//            .configure(KotlinFeature.SingletonSupport, true)
//            .configure(KotlinFeature.StrictNullChecks, true)
//            .build()
//        mapper.registerModule(kotlinModule)
//    }
//
//
    inline fun <reified T> fromJson(str: String, clz: Class<T>): T {
        return json.decodeFromString<T>(str)
    }

    @OptIn(InternalSerializationApi::class, ExperimentalSerializationApi::class)
    fun toJson(obj: Any): String {
        return json.encodeToString(DynamicLookupSerializer(), obj)
    }

}

@InternalSerializationApi
@ExperimentalSerializationApi
class DynamicLookupSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = ContextualSerializer(Any::class, null, emptyArray()).descriptor

    override fun serialize(encoder: Encoder, value: Any) {
        val actualSerializer = encoder.serializersModule.getContextual(value::class) ?: value::class.serializer()
        encoder.encodeSerializableValue(actualSerializer as KSerializer<Any>, value)
    }

    override fun deserialize(decoder: Decoder): Any {
        error("Unsupported")
    }
}

object LinkedListSerializer : KSerializer<LinkedList<JsonSerializable>> {
    private val serializer = ListSerializer(String.serializer())
    override val descriptor = serializer.descriptor
    override fun deserialize(decoder: Decoder): LinkedList<JsonSerializable> {
        return LinkedList(serializer.deserialize(decoder).map { Json.decodeFromString(it) })
    }

    override fun serialize(encoder: Encoder, value: LinkedList<JsonSerializable>) {
        serializer.serialize(encoder, value.map { it.toJson() })
    }
}