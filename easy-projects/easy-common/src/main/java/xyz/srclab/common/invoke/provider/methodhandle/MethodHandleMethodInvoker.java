package xyz.srclab.common.invoke.provider.methodhandle;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.invoke.MethodInvoker;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class MethodHandleMethodInvoker implements MethodInvoker {

    private final MethodInvoker invokeByMethodHandle;

    MethodHandleMethodInvoker(Method method) {
        this.invokeByMethodHandle = buildMethodHandleInvoker(method);
    }

    private MethodInvoker buildMethodHandleInvoker(Method method) {
        MethodHandle methodHandle = buildMethodHandle(method);
        if (Modifier.isStatic(method.getModifiers())) {
            InvokeByMethodHandle.StaticMethodInvoker staticMethodInvoker =
                    new InvokeByMethodHandle.StaticMethodInvoker(methodHandle);
            return new MethodInvoker() {
                @Override
                public @Nullable Object invoke(@Nullable Object object, Object... args) {
                    return staticMethodInvoker.invoke(args);
                }
            };
        }
        return new InvokeByMethodHandle.VirtualMethodInvoker(methodHandle);
    }

    private MethodHandle buildMethodHandle(Method method) {
        MethodType methodType;
        switch (method.getParameterCount()) {
            case 0:
                methodType = MethodType.methodType(method.getReturnType());
                break;
            case 1:
                methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes()[0]);
                break;
            default:
                methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
        }
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        try {
            return Modifier.isStatic(method.getModifiers()) ?
                    lookup.findStatic(method.getDeclaringClass(), method.getName(), methodType)
                    :
                    lookup.findVirtual(method.getDeclaringClass(), method.getName(), methodType);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public @Nullable Object invoke(@Nullable Object object, Object... args) {
        return invokeByMethodHandle.invoke(object, args);
    }
}
