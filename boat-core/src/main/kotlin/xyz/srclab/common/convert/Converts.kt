@file:JvmName("Converts")

package xyz.srclab.common.convert

import xyz.srclab.common.reflect.TypeRef
import java.lang.reflect.Type

private val defaultConverter = Converter.DEFAULT

fun <T> Any?.convert(toType: Class<T>): T {
    return defaultConverter.convert(this, toType)
}

fun <T> Any?.convert(toType: Type): T {
    return defaultConverter.convert(this, toType)
}

fun <T> Any?.convert(fromType: Type, toType: Type): T {
    return defaultConverter.convert(this, fromType, toType)
}

fun <T> Any?.convert(toTypeRef: TypeRef<T>): T {
    return defaultConverter.convert(this, toTypeRef)
}

fun <F, R> Any?.convert(fromTypeRef: TypeRef<F>, toTypeRef: TypeRef<R>): R {
    return defaultConverter.convert(this, fromTypeRef, toTypeRef)
}