@file:JvmName("ReflectKit")
@file:JvmMultifileClass

package xyz.srclab.common.reflect

import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.reflect.TypeUtils
import xyz.srclab.annotations.OutParam
import xyz.srclab.annotations.PossibleTypes
import xyz.srclab.common.base.Current
import xyz.srclab.common.base.asAny
import xyz.srclab.common.base.loadClass
import xyz.srclab.common.collect.toTypedArray
import java.lang.reflect.*

val Member.isPublic: Boolean
    @JvmName("isPublic") get() {
        return Modifier.isPublic(this.modifiers)
    }

val Member.isPrivate: Boolean
    @JvmName("isPrivate") get() {
        return Modifier.isPrivate(this.modifiers)
    }

val Member.isProtected: Boolean
    @JvmName("isProtected") get() {
        return Modifier.isProtected(this.modifiers)
    }

val Member.isStatic: Boolean
    @JvmName("isStatic") get() {
        return Modifier.isStatic(this.modifiers)
    }

val Member.isFinal: Boolean
    @JvmName("isFinal") get() {
        return Modifier.isFinal(this.modifiers)
    }

val Member.isSynchronized: Boolean
    @JvmName("isSynchronized") get() {
        return Modifier.isSynchronized(this.modifiers)
    }

val Member.isVolatile: Boolean
    @JvmName("isVolatile") get() {
        return Modifier.isVolatile(this.modifiers)
    }

val Member.isTransient: Boolean
    @JvmName("isTransient") get() {
        return Modifier.isTransient(this.modifiers)
    }

val Member.isNative: Boolean
    @JvmName("isNative") get() {
        return Modifier.isNative(this.modifiers)
    }

val Member.isInterface: Boolean
    @JvmName("isInterface") get() {
        return Modifier.isInterface(this.modifiers)
    }

val Member.isAbstract: Boolean
    @JvmName("isAbstract") get() {
        return Modifier.isAbstract(this.modifiers)
    }

val Member.isStrict: Boolean
    @JvmName("isStrict") get() {
        return Modifier.isStrict(this.modifiers)
    }

val Class<*>.isPublic: Boolean
    @JvmName("isPublic") get() {
        return Modifier.isPublic(this.modifiers)
    }

val Class<*>.isPrivate: Boolean
    @JvmName("isPrivate") get() {
        return Modifier.isPrivate(this.modifiers)
    }

val Class<*>.isProtected: Boolean
    @JvmName("isProtected") get() {
        return Modifier.isProtected(this.modifiers)
    }

val Class<*>.isStatic: Boolean
    @JvmName("isStatic") get() {
        return Modifier.isStatic(this.modifiers)
    }

val Class<*>.isFinal: Boolean
    @JvmName("isFinal") get() {
        return Modifier.isFinal(this.modifiers)
    }

val Class<*>.isSynchronized: Boolean
    @JvmName("isSynchronized") get() {
        return Modifier.isSynchronized(this.modifiers)
    }

val Class<*>.isVolatile: Boolean
    @JvmName("isVolatile") get() {
        return Modifier.isVolatile(this.modifiers)
    }

val Class<*>.isTransient: Boolean
    @JvmName("isTransient") get() {
        return Modifier.isTransient(this.modifiers)
    }

val Class<*>.isNative: Boolean
    @JvmName("isNative") get() {
        return Modifier.isNative(this.modifiers)
    }

val Class<*>.isInterface: Boolean
    @JvmName("isInterface") get() {
        return this.isInterface
    }

val Class<*>.isAbstract: Boolean
    @JvmName("isAbstract") get() {
        return Modifier.isAbstract(this.modifiers)
    }

val Class<*>.isStrict: Boolean
    @JvmName("isStrict") get() {
        return Modifier.isStrict(this.modifiers)
    }

val Type.rawClassOrNull: Class<*>?
    @JvmName("rawClassOrNull") get() {
        return when (this) {
            is Class<*> -> this
            is ParameterizedType -> this.rawClass
            is GenericArrayType -> this.rawClass
            else -> null
        }
    }

/**
 * @throws IllegalArgumentException
 */
val @PossibleTypes(Class::class, ParameterizedType::class, GenericArrayType::class) Type.rawClass: Class<*>
    @JvmName("rawClass") get() {
        return this.rawClassOrNull ?: throw IllegalArgumentException(
            "Only Class, ParameterizedType or GenericArrayType has raw class."
        )
    }

val ParameterizedType.rawClass: Class<*>
    @JvmName("rawClass") get() {
        return this.rawType.asAny()
    }

val GenericArrayType.rawClass: Class<*>
    @JvmName("rawClass") get() {
        return this.genericComponentType.rawClass.arrayClass
    }

val TypeVariable<*>.upperClass: Class<*>
    @JvmName("upperClass") get() {
        val bounds = this.bounds
        return if (bounds.isEmpty()) {
            Any::class.java
        } else {
            bounds[0].rawOrUpperClass
        }
    }

val WildcardType.upperClass: Class<*>
    @JvmName("upperClass") get() {
        val upperBounds = this.upperBounds
        return if (upperBounds.isEmpty()) {
            Any::class.java
        } else {
            upperBounds[0].rawOrUpperClass
        }
    }

val Type.rawOrUpperClass: Class<*>
    @JvmName("rawOrUpperClass") get() {
        return when (this) {
            is Class<*> -> this
            is ParameterizedType -> this.rawClass
            is GenericArrayType -> this.rawClass
            is TypeVariable<*> -> this.upperClass
            is WildcardType -> this.upperClass
            else -> Any::class.java
        }
    }

val TypeVariable<*>.lowerClass: Class<*>?
    @JvmName("lowerClass") get() {
        val bounds = this.bounds
        return if (bounds is Class<*> && bounds.isFinal) bounds else null
    }

val WildcardType.lowerClass: Class<*>?
    @JvmName("lowerClass") get() {
        val lowerBounds = this.lowerBounds
        return if (lowerBounds.isEmpty()) {
            null
        } else {
            lowerBounds[0].rawOrLowerClass
        }
    }

val Type.rawOrLowerClass: Class<*>?
    @JvmName("rawOrLowerClass") get() {
        return when (this) {
            is Class<*> -> this
            is ParameterizedType -> this.rawClass
            is GenericArrayType -> this.rawClass
            is TypeVariable<*> -> this.lowerClass
            is WildcardType -> this.lowerClass
            else -> null
        }
    }

val TypeVariable<*>.upperBound: Type
    @JvmName("upperBound")
    get() {
        val bounds = this.bounds
        return if (bounds.isEmpty()) {
            Any::class.java
        } else {
            bounds[0].thisOrUpperBound
        }
    }

val WildcardType.upperBound: Type
    @JvmName("upperBound")
    get() {
        val upperBounds = this.upperBounds
        return if (upperBounds.isEmpty()) {
            Any::class.java
        } else {
            upperBounds[0].thisOrUpperBound
        }
    }

val Type.thisOrUpperBound: Type
    @JvmName("thisOrUpperBound")
    get() {
        return when (this) {
            is TypeVariable<*> -> this.upperBound
            is WildcardType -> this.upperBound
            else -> this
        }
    }

val TypeVariable<*>.lowerBound: Type?
    @JvmName("lowerBound")
    get() {
        return this.lowerClass
    }

val WildcardType.lowerBound: Type?
    @JvmName("lowerBound")
    get() {
        val lowerBounds = this.lowerBounds
        return if (lowerBounds.isEmpty()) {
            null
        } else {
            lowerBounds[0].thisOrLowerBound
        }
    }

val Type.thisOrLowerBound: Type?
    @JvmName("thisOrLowerBound")
    get() {
        return when (this) {
            is TypeVariable<*> -> this.lowerBound
            is WildcardType -> this.lowerBound
            else -> this
        }
    }

val WildcardType.isUnbounded: Boolean
    @JvmName("isUnbounded") get() {
        val upperBounds = this.upperBounds
        return (upperBounds.isEmpty() || (upperBounds.size == 1 && upperBounds[0] == Any::class.java))
                && this.lowerBounds.isEmpty()
    }

val Type.isArray: Boolean
    @JvmName("isArray") get() {
        return when (this) {
            is Class<*> -> this.isArray
            is GenericArrayType -> true
            else -> false
        }
    }

val Type.componentType: Type?
    @JvmName("componentType") get() {
        return when (this) {
            is Class<*> -> if (this.isArray) this.componentType else null
            is GenericArrayType -> this.genericComponentType
            else -> null
        }
    }

val <T> Class<T>.arrayClass: Class<Array<T>>
    @JvmName("arrayClass") get() {
        if (this.isArray) {
            return "[${this.name}".loadClass()
        }
        val arrayClassName = when (this) {
            Boolean::class.javaPrimitiveType -> "[Z"
            Byte::class.javaPrimitiveType -> "[B"
            Short::class.javaPrimitiveType -> "[S"
            Char::class.javaPrimitiveType -> "[C"
            Int::class.javaPrimitiveType -> "[I"
            Long::class.javaPrimitiveType -> "[J"
            Float::class.javaPrimitiveType -> "[F"
            Double::class.javaPrimitiveType -> "[D"
            Void::class.javaPrimitiveType -> "[V"
            else -> {
                "[L" + this.canonicalName + ";"
            }
        }
        return arrayClassName.loadClass()
    }

/**
 * Returns whether [clazz] can use [this]
 */
fun Member.isAccessibleFor(clazz: Class<*>): Boolean {
    if (this.isPublic) {
        return true
    }
    if (this.isPrivate) {
        return this.declaringClass == clazz
    }
    val declaringPackage = this.declaringClass.`package`
    if (declaringPackage == clazz.`package`) {
        return true
    }
    if (this.isProtected) {
        return this.declaringClass.isAssignableFrom(clazz)
    }
    return false
}

/**
 * @throws ClassNotFoundException
 */
@JvmOverloads
fun <T> CharSequence.toClass(classLoader: ClassLoader = Current.classLoader): Class<T> {
    return this.loadClass(classLoader)
}

/**
 * @throws ClassNotFoundException
 * @throws NoSuchMethodException
 */
@JvmOverloads
fun <T> CharSequence.toInstance(classLoader: ClassLoader = Current.classLoader): T {
    return toInstance(ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.EMPTY_CLASS_ARRAY, classLoader)
}

/**
 * @throws ClassNotFoundException
 * @throws NoSuchMethodException
 */
@JvmOverloads
fun <T> CharSequence.toInstance(
    parameterTypes: Array<out Class<*>>,
    args: Array<out Any?>,
    classLoader: ClassLoader = Current.classLoader,
): T {
    val clazz: Class<T> = this.loadClass(classLoader)
    return clazz.toInstance(parameterTypes, args)
}

/**
 * @throws NoSuchMethodException
 */
fun <T> Class<*>.toInstance(): T {
    return toInstance(ArrayUtils.EMPTY_CLASS_ARRAY, ArrayUtils.EMPTY_CLASS_ARRAY)
}

/**
 * @throws NoSuchMethodException
 */
fun <T> Class<*>.toInstance(parameterTypes: Array<out Class<*>>, args: Array<out Any?>): T {
    val constructor = try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
    return constructor.newInstance(*args).asAny()
}

fun Class<*>.toWrapperClass(): Class<*> {
    return when (this) {
        Boolean::class.javaPrimitiveType -> Boolean::class.java
        Byte::class.javaPrimitiveType -> Byte::class.java
        Short::class.javaPrimitiveType -> Short::class.java
        Char::class.javaPrimitiveType -> Char::class.java
        Int::class.javaPrimitiveType -> Int::class.java
        Long::class.javaPrimitiveType -> Long::class.java
        Float::class.javaPrimitiveType -> Float::class.java
        Double::class.javaPrimitiveType -> Double::class.java
        Void::class.javaPrimitiveType -> Void::class.java
        else -> this
    }
}

fun <T> Class<T>.constructorOrNull(vararg parameterTypes: Class<*>): Constructor<T>? {
    return try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

/**
 * @throws NoSuchMethodException
 */
fun <T> Class<T>.constructor(vararg parameterTypes: Class<*>): Constructor<T> {
    return try {
        this.getConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
}

fun <T> Class<T>.constructors(): List<Constructor<T>> {
    return this.constructors.asList().asAny()
}

fun <T> Class<T>.declaredConstructorOrNull(vararg parameterTypes: Class<*>): Constructor<T>? {
    return try {
        this.getDeclaredConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

/**
 * @throws NoSuchMethodException
 */
fun <T> Class<T>.declaredConstructor(vararg parameterTypes: Class<*>): Constructor<T> {
    return try {
        this.getDeclaredConstructor(*parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
}

fun <T> Class<T>.declaredConstructors(): List<Constructor<T>> {
    return this.declaredConstructors.asList().asAny()
}

fun <T> Constructor<T>.invoke(vararg args: Any?): T {
    return this.newInstance(*args)
}

fun <T> Constructor<T>.invokeForcibly(vararg args: Any?): T {
    this.isAccessible = true
    return this.newInstance(*args)
}

fun Class<*>.fieldOrNull(name: String): Field? {
    return try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
}

/**
 * @throws NoSuchFieldException
 */
fun Class<*>.field(name: String): Field {
    return try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        throw e
    }
}

fun Class<*>.fields(): List<Field> {
    return this.fields.asList()
}

fun Class<*>.declaredFieldOrNull(name: String): Field? {
    return try {
        this.getDeclaredField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
}

/**
 * @throws NoSuchFieldException
 */
fun Class<*>.declaredField(name: String): Field {
    return try {
        this.getDeclaredField(name)
    } catch (e: NoSuchFieldException) {
        throw e
    }
}

fun Class<*>.declaredFields(): List<Field> {
    return this.declaredFields.asList()
}

fun Class<*>.ownedFieldOrNull(name: String): Field? {
    return searchFieldOrNull(name, false)
}

/**
 * @throws NoSuchFieldException
 */
fun Class<*>.ownedField(name: String): Field {
    return ownedFieldOrNull(name) ?: throw NoSuchFieldException(name)
}

fun Class<*>.ownedFields(): List<Field> {
    val set = LinkedHashSet<Field>()
    set.addAll(this.fields())
    for (declaredField in this.declaredFields()) {
        if (!set.contains(declaredField)) {
            set.add(declaredField)
        }
    }
    return set.toList()
}

@JvmOverloads
fun Class<*>.searchFieldOrNull(name: String, deep: Boolean = true): Field? {
    var field = try {
        this.getField(name)
    } catch (e: NoSuchFieldException) {
        null
    }
    if (field !== null) {
        return field
    }
    field = this.declaredFieldOrNull(name)
    if (field !== null) {
        return field
    }
    if (!deep) {
        return null
    }
    var superClass = this.superclass
    while (superClass !== null) {
        field = superClass.declaredFieldOrNull(name)
        if (field !== null) {
            return field
        }
        superClass = superClass.superclass
    }
    return null
}

@JvmOverloads
fun Class<*>.searchFields(deep: Boolean = true, predicate: (Field) -> Boolean): List<Field> {
    val result = mutableListOf<Field>()
    for (field in this.fields) {
        if (predicate(field)) {
            result.add(field)
        }
    }
    for (field in this.declaredFields) {
        if (predicate(field)) {
            result.add(field)
        }
    }
    if (!deep) {
        return result
    }
    var superClass = this.superclass
    while (superClass !== null) {
        for (field in superClass.declaredFields) {
            if (predicate(field)) {
                result.add(field)
            }
        }
        superClass = superClass.superclass
    }
    return result
}

/**
 * @throws IllegalAccessException
 */
@JvmOverloads
fun <T> Field.getValue(owner: Any?, force: Boolean = false): T {
    return try {
        if (force) {
            this.isAccessible = true
        }
        this.get(owner).asAny()
    } catch (e: IllegalAccessException) {
        throw e
    }
}

/**
 * @throws IllegalAccessException
 */
@JvmOverloads
fun Field.setValue(owner: Any?, value: Any?, force: Boolean = false) {
    try {
        if (force) {
            this.isAccessible = true
        }
        this.set(owner, value)
    } catch (e: IllegalAccessException) {
        throw e
    }
}

/**
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
fun <T> Class<*>.getFieldValue(name: String, owner: Any?): T {
    val field = this.fieldOrNull(name)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.getValue(owner)
}

/**
 * @throws NoSuchFieldException
 */
fun <T> Class<*>.getFieldValue(
    name: String,
    owner: Any?,
    deep: Boolean,
    force: Boolean
): T {
    val field = this.searchFieldOrNull(name, deep)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.getValue(owner, force)
}

/**
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
fun Class<*>.setFieldValue(name: String, owner: Any?, value: Any?) {
    val field = this.fieldOrNull(name)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.setValue(value, owner)
}

/**
 * @throws NoSuchFieldException
 * @throws IllegalAccessException
 */
fun Class<*>.setFieldValue(
    name: String,
    owner: Any?,
    value: Any?,
    deep: Boolean,
    force: Boolean,
) {
    val field = this.searchFieldOrNull(name, deep)
    if (field === null) {
        throw NoSuchFieldException(name)
    }
    return field.setValue(owner, value, force)
}

fun Class<*>.methodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return try {
        this.getMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

/**
 * @throws NoSuchMethodException
 */
fun Class<*>.method(name: String, vararg parameterTypes: Class<*>): Method {
    return try {
        this.getMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
}

fun Class<*>.methods(): List<Method> {
    return this.methods.asList()
}

fun Class<*>.declaredMethodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return try {
        this.getDeclaredMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
}

/**
 * @throws NoSuchMethodException
 */
fun Class<*>.declaredMethod(name: String, vararg parameterTypes: Class<*>): Method {
    return try {
        this.getDeclaredMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        throw e
    }
}

fun Class<*>.declaredMethods(): List<Method> {
    return this.declaredMethods.asList()
}

fun Class<*>.ownedMethodOrNull(name: String, vararg parameterTypes: Class<*>): Method? {
    return searchMethodOrNull(name, parameterTypes, false)
}

/**
 * @throws NoSuchMethodException
 */
fun Class<*>.ownedMethod(name: String, vararg parameterTypes: Class<*>): Method {
    return ownedMethodOrNull(name, *parameterTypes) ?: throw NoSuchMethodException(
        "name: $name, parameters: ${parameterTypes.contentToString()}"
    )
}

fun Class<*>.ownedMethods(): List<Method> {
    val set = LinkedHashSet<Method>()
    set.addAll(this.methods())
    for (declaredMethod in this.declaredMethods()) {
        if (!set.contains(declaredMethod)) {
            set.add(declaredMethod)
        }
    }
    return set.toList()
}

@JvmOverloads
fun Class<*>.searchMethodOrNull(
    name: String,
    parameterTypes: Array<out Class<*>>,
    deep: Boolean = true,
): Method? {
    var method = try {
        this.getMethod(name, *parameterTypes)
    } catch (e: NoSuchMethodException) {
        null
    }
    if (method !== null) {
        return method
    }
    method = declaredMethodOrNull(name, *parameterTypes)
    if (method !== null) {
        return method
    }
    if (!deep) {
        return null
    }
    var superClass = this.superclass
    while (superClass !== null) {
        method = superClass.declaredMethodOrNull(name, *parameterTypes)
        if (method !== null) {
            return method
        }
        superClass = superClass.superclass
    }
    return null
}

@JvmOverloads
fun Class<*>.searchMethods(deep: Boolean = true, predicate: (Method) -> Boolean): List<Method> {
    val result = mutableListOf<Method>()
    for (method in this.methods) {
        if (predicate(method)) {
            result.add(method)
        }
    }
    for (method in this.declaredMethods) {
        if (predicate(method)) {
            result.add(method)
        }
    }
    if (!deep) {
        return result
    }
    var superClass = this.superclass
    while (superClass !== null) {
        for (method in superClass.declaredMethods) {
            if (predicate(method)) {
                result.add(method)
            }
        }
        superClass = superClass.superclass
    }
    return result
}

/**
 * @throws  IllegalAccessException
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.invoke(owner: Any?, vararg args: Any?): T {
    return try {
        this.invoke(owner, *args).asAny()
    } catch (e: Exception) {
        throw e
    }
}

/**
 * @throws  IllegalArgumentException
 * @throws  ReflectiveOperationException
 */
fun <T> Method.invokeForcible(owner: Any?, vararg args: Any?): T {
    this.isAccessible = true
    return try {
        this.invoke(owner, *args).asAny()
    } catch (e: Exception) {
        throw IllegalStateException(e)
    }
}

fun Method.canOverrideBy(clazz: Class<*>): Boolean {
    if (this.isPrivate) {
        return false
    }
    if (!this.declaringClass.isAssignableFrom(clazz)) {
        return false
    }
    return true
}

fun parameterizedType(
    rawType: Type,
    vararg actualTypeArguments: Type,
): ParameterizedType {
    return TypeUtils.parameterize(rawType.rawClass, *actualTypeArguments)
}

fun parameterizedType(
    rawType: Type,
    actualTypeArguments: Iterable<Type>,
): ParameterizedType {
    return parameterizedType(rawType, *actualTypeArguments.toTypedArray())
}

fun parameterizedTypeWithOwner(
    rawType: Type,
    ownerType: Type?,
    vararg actualTypeArguments: Type,
): ParameterizedType {
    return TypeUtils.parameterizeWithOwner(ownerType, rawType.rawClass, *actualTypeArguments)
}

fun parameterizedTypeWithOwner(
    rawType: Type,
    ownerType: Type?,
    actualTypeArguments: Iterable<Type>,
): ParameterizedType {
    return parameterizedTypeWithOwner(rawType, ownerType, *actualTypeArguments.toTypedArray())
}

fun wildcardType(upperBounds: Array<out Type>?, lowerBounds: Array<out Type>?): WildcardType {
    val uppers = upperBounds ?: arrayOf(Any::class.java)
    val lowers = lowerBounds ?: ArrayUtils.EMPTY_TYPE_ARRAY
    return TypeUtils.wildcardType().withUpperBounds(*uppers).withLowerBounds(*lowers).build()
}

fun wildcardType(upperBounds: Iterable<Type>?, lowerBounds: Iterable<Type>?): WildcardType {
    val uppers = upperBounds ?: emptyList()
    val lowers = lowerBounds ?: emptyList()
    return wildcardType(uppers.toTypedArray(), lowers.toTypedArray())
}

fun wildcardTypeWithUpperBounds(vararg upperBounds: Type): WildcardType {
    return TypeUtils.wildcardType().withUpperBounds(*upperBounds).build()
}

fun wildcardTypeWithUpperBounds(upperBounds: Iterable<Type>): WildcardType {
    return wildcardTypeWithUpperBounds(*upperBounds.toTypedArray())
}

fun wildcardTypeWithLowerBounds(vararg lowerBounds: Type): WildcardType {
    return TypeUtils.wildcardType().withLowerBounds(*lowerBounds).build()
}

fun wildcardTypeWithLowerBounds(lowerBounds: Iterable<Type>): WildcardType {
    return wildcardTypeWithLowerBounds(*lowerBounds.toTypedArray())
}

fun Type.genericArrayType(): GenericArrayType {
    return TypeUtils.genericArrayType(this)
}

/**
 * Returns type arguments of [this].
 *
 * @throws IllegalArgumentException
 */
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class,
    GenericArrayType::class,
) Type.findTypeArguments(): Map<TypeVariable<*>, Type> {

    if (this.isArray) {
        return this.componentType!!.findTypeArguments()
    }

    fun findTypeArgumentsTo0(@OutParam typeArguments: MutableMap<TypeVariable<*>, Type>, type: Type) {
        if (type is ParameterizedType) {
            val ownerType = type.ownerType;
            if (ownerType !== null) {
                findTypeArgumentsTo0(typeArguments, ownerType)
            }
            findTypeArgumentsTo(typeArguments, type)
        }
        val rawClass = type.rawClass
        val genericSuperclass = rawClass.genericSuperclass
        if (genericSuperclass !== null) {
            findTypeArgumentsTo0(typeArguments, genericSuperclass)
        }
        val genericInterfaces = rawClass.genericInterfaces
        for (genericInterface in genericInterfaces) {
            findTypeArgumentsTo0(typeArguments, genericInterface)
        }
    }

    val typeArguments = mutableMapOf<TypeVariable<*>, Type>()
    findTypeArgumentsTo0(typeArguments, this)
    eraseTypeVariablesSelves(typeArguments)
    return typeArguments
}

/**
 * Returns type arguments of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [to].
 *
 * @throws IllegalArgumentException
 */
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class,
    GenericArrayType::class,
) Type.findTypeArguments(vararg to: Class<*>): Map<TypeVariable<*>, Type> {
    return this.findTypeArguments(to.toList())
}

/**
 * Returns type arguments of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [to].
 *
 * @throws IllegalArgumentException
 */
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class,
    GenericArrayType::class,
) Type.findTypeArguments(to: Iterable<Class<*>>): Map<TypeVariable<*>, Type> {

    if (this.isArray) {
        return this.componentType!!.findTypeArguments()
    }

    fun findTypeArgumentsTo0(
        @OutParam typeArguments: MutableMap<TypeVariable<*>, Type>,
        type: Type,
        to: MutableCollection<Class<*>>,
    ) {
        if (to.isEmpty()) {
            return
        }
        if (type is ParameterizedType) {
            val ownerType = type.ownerType;
            if (ownerType !== null) {
                findTypeArgumentsTo0(typeArguments, ownerType, to)
            }
            findTypeArgumentsTo(typeArguments, type)
        }
        val rawClass = type.rawClass
        to.remove(rawClass)
        if (to.isEmpty()) {
            return
        }
        val genericSuperclass = rawClass.genericSuperclass
        if (genericSuperclass !== null) {
            findTypeArgumentsTo0(typeArguments, genericSuperclass, to)
        }
        val genericInterfaces = rawClass.genericInterfaces
        for (genericInterface in genericInterfaces) {
            findTypeArgumentsTo0(typeArguments, genericInterface, to)
        }
    }

    val typeArguments = mutableMapOf<TypeVariable<*>, Type>()
    findTypeArgumentsTo0(typeArguments, this, to.toMutableList())
    eraseTypeVariablesSelves(typeArguments)
    return typeArguments
}

private fun findTypeArgumentsTo(@OutParam typeArguments: MutableMap<TypeVariable<*>, Type>, type: ParameterizedType) {
    val arguments = type.actualTypeArguments
    val parameters = type.rawClass.typeParameters
    for (i in parameters.indices) {
        val parameter = parameters[i]
        val argument = arguments[i]
        typeArguments[parameter] = typeArguments.getOrDefault(argument, argument)
    }
}

private fun eraseTypeVariablesSelves(@OutParam typeArguments: MutableMap<TypeVariable<*>, Type>) {
    for (entry in typeArguments.entries) {
        val param = entry.key
        val type = entry.value
        typeArguments[param] = type.eraseTypeVariables(typeArguments)
    }
}

@JvmOverloads
fun Type.eraseTypeVariables(typeArguments: Map<TypeVariable<*>, Type> = this.findTypeArguments()): Type {
    return when (this) {
        is Class<*> -> this
        is ParameterizedType -> {
            val actualTypeArguments = this.actualTypeArguments
            if (replaceTypeVariable(actualTypeArguments, typeArguments)) {
                return parameterizedTypeWithOwner(this.rawType, this.ownerType, *actualTypeArguments)
            }
            return this
        }
        is TypeVariable<*> -> {
            return deepSearchTypeVariable(this, typeArguments)
        }
        is WildcardType -> {
            val upperBounds = this.upperBounds
            val replaceUppers = replaceTypeVariable(upperBounds, typeArguments)
            val lowerBounds = this.lowerBounds
            val replaceLowers = replaceTypeVariable(lowerBounds, typeArguments)
            if (replaceUppers || replaceLowers) {
                return wildcardType(upperBounds, lowerBounds)
            }
            return this
        }
        is GenericArrayType -> {
            val componentType = this.genericComponentType.eraseTypeVariables(typeArguments)
            if (componentType === this.genericComponentType) {
                return this
            }
            return componentType.genericArrayType()
        }
        else -> this
    }
}

fun Type.eraseTypeVariables(typeArgumentsGenerator: (Type) -> Map<TypeVariable<*>, Type>): Type {

    var typeArgumentsTemp: Map<TypeVariable<*>, Type>? = null

    fun getTypeArguments(): Map<TypeVariable<*>, Type> {
        val result = typeArgumentsTemp
        return if (result === null) {
            val args = typeArgumentsGenerator(this)
            typeArgumentsTemp = args
            args
        } else {
            result
        }
    }

    return when (this) {
        is Class<*> -> this
        is ParameterizedType -> {
            val actualTypeArguments = this.actualTypeArguments
            if (replaceTypeVariable(actualTypeArguments, getTypeArguments())) {
                return parameterizedTypeWithOwner(this.rawType, this.ownerType, *actualTypeArguments)
            }
            return this
        }
        is TypeVariable<*> -> {
            return deepSearchTypeVariable(this, getTypeArguments())
        }
        is WildcardType -> {
            val upperBounds = this.upperBounds
            val replaceUppers = replaceTypeVariable(upperBounds, getTypeArguments())
            val lowerBounds = this.lowerBounds
            val replaceLowers = replaceTypeVariable(lowerBounds, getTypeArguments())
            if (replaceUppers || replaceLowers) {
                return wildcardType(upperBounds, lowerBounds)
            }
            return this
        }
        is GenericArrayType -> {
            val componentType = this.genericComponentType.eraseTypeVariables(getTypeArguments())
            if (componentType === this.genericComponentType) {
                return this
            }
            return componentType.genericArrayType()
        }
        else -> this
    }
}

private fun replaceTypeVariable(@OutParam array: Array<Type>, typeArguments: Map<TypeVariable<*>, Type>): Boolean {
    var result = false
    for (i in array.indices) {
        val oldType = array[i]
        val newType = if (oldType is TypeVariable<*>) {
            deepSearchTypeVariable(oldType, typeArguments)
        } else {
            oldType.eraseTypeVariables(typeArguments)
        }
        if (oldType !== newType) {
            array[i] = newType
            result = true
        }
    }
    return result
}

private fun deepSearchTypeVariable(
    type: TypeVariable<*>,
    typeArguments: Map<TypeVariable<*>, Type>
): Type {
    val tryType = typeArguments[type]
    if (tryType === null) {
        return type
    }
    if (tryType !is TypeVariable<*>) {
        return tryType.eraseTypeVariables(typeArguments)
    }
    val history = mutableSetOf<Type>()
    history.add(tryType)
    var curType: TypeVariable<*> = tryType
    while (true) {
        val newType = typeArguments[curType]
        if (newType === null) {
            return curType
        }
        if (newType !is TypeVariable<*>) {
            return newType.eraseTypeVariables(typeArguments)
        }
        if (history.contains(newType)) {
            return tryType
        }
        history.add(newType)
        curType = newType
    }
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 */
@JvmOverloads
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.generalize(typeArguments: Map<TypeVariable<*>, Type>? = null, vararg targets: Class<*>): Type? {
    return this.generalize(typeArguments, targets.asList())
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 */
@JvmOverloads
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.generalize(typeArguments: Map<TypeVariable<*>, Type>? = null, targets: Iterable<Class<*>>): Type? {
    for (target in targets) {
        val result = if (target.isInterface)
            this.genericInterface(typeArguments, target)
        else
            this.genericSuperclass(typeArguments, target)
        if (result !== null) {
            return result
        }
    }
    return null
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 */
@JvmOverloads
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.genericSuperclass(typeArguments: Map<TypeVariable<*>, Type>? = null, vararg targets: Class<*>): Type? {
    return this.genericSuperclass(typeArguments, targets.asList())
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 */
@JvmOverloads
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.genericSuperclass(typeArguments: Map<TypeVariable<*>, Type>? = null, targets: Iterable<Class<*>>): Type? {
    var rawClass = this.rawClass
    if (targets.contains(rawClass)) {
        return if (typeArguments === null) {
            this.eraseTypeVariables { it.findTypeArguments(rawClass) }
        } else {
            this.eraseTypeVariables(typeArguments)
        }
    }

    var genericType: Type? = rawClass.genericSuperclass
    while (genericType !== null) {
        rawClass = genericType.rawClass
        if (targets.contains(rawClass)) {
            return if (typeArguments === null) {
                genericType.eraseTypeVariables { it.findTypeArguments(rawClass) }
            } else {
                genericType.eraseTypeVariables(typeArguments)
            }
        }
        genericType = rawClass.genericSuperclass
    }
    return null
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 */
@JvmOverloads
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.genericInterface(typeArguments: Map<TypeVariable<*>, Type>? = null, vararg targets: Class<*>): Type? {
    return this.genericInterface(typeArguments, targets.asList())
}

/**
 * Returns generic signature of [this].
 *
 * This method will iterate on the inheritance tree util anyone (included) contained by [targets].
 *
 * For example:
 * ```
 * class StringFoo : Foo<String>
 * ```
 * Let [this] be `StringFoo`, [targets] contains only `Foo.class`, this method will return `Foo<String>`.
 *
 * @throws IllegalArgumentException
 */
@JvmOverloads
@PossibleTypes(Class::class, ParameterizedType::class)
fun @PossibleTypes(
    Class::class,
    ParameterizedType::class
) Type.genericInterface(typeArguments: Map<TypeVariable<*>, Type>? = null, targets: Iterable<Class<*>>): Type? {

    //Level searching
    fun findInterface(genericTypes: List<Type>, targets: Iterable<Class<*>>): Type? {
        for (genericType in genericTypes) {
            val genericClass = genericType.rawClass
            if (targets.contains(genericClass)) {
                return if (typeArguments === null) {
                    genericType.eraseTypeVariables { it.findTypeArguments(genericClass) }
                } else {
                    genericType.eraseTypeVariables(typeArguments)
                }
            }
        }
        val nextLevel = mutableListOf<Type>()
        for (genericType in genericTypes) {
            val genericClass = genericType.rawClass
            nextLevel.addAll(genericClass.genericInterfaces)
        }
        if (nextLevel.isEmpty()) {
            return null
        }
        return findInterface(nextLevel, targets)
    }

    val rawClass = this.rawClass
    if (targets.contains(rawClass)) {
        return if (typeArguments === null) {
            this.eraseTypeVariables { it.findTypeArguments(rawClass) }
        } else {
            this.eraseTypeVariables(typeArguments)
        }
    }
    val tryInterfaces = findInterface(rawClass.genericInterfaces.asList(), targets)
    if (tryInterfaces !== null) {
        return if (typeArguments === null) {
            tryInterfaces.eraseTypeVariables { it.findTypeArguments(tryInterfaces.rawClass) }
        } else {
            tryInterfaces.eraseTypeVariables(typeArguments)
        }
    }

    // Try interfaces of superclasses
    var superclass = rawClass.superclass
    var trySuperInterface: Type?
    while (superclass !== null) {
        trySuperInterface = findInterface(superclass.genericInterfaces.asList(), targets)
        if (trySuperInterface !== null) {
            return if (typeArguments === null) {
                trySuperInterface.eraseTypeVariables { it.findTypeArguments(trySuperInterface.rawClass) }
            } else {
                trySuperInterface.eraseTypeVariables(typeArguments)
            }
        }
        superclass = superclass.superclass
    }
    return null
}