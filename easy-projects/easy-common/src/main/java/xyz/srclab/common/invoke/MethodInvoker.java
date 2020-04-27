package xyz.srclab.common.invoke;

import xyz.srclab.annotation.Immutable;
import xyz.srclab.annotation.Nullable;

import java.lang.reflect.Method;

@Immutable
public interface MethodInvoker {

    static MethodInvoker of(Method method) {
        return InvokerSupport.getMethodInvoker(method);
    }

    static MethodInvoker of(Class<?> type, String methodName, Class<?>... parameterTypes) {
        return InvokerSupport.getMethodInvoker(type, methodName, parameterTypes);
    }

    @Nullable
    Object invoke(@Nullable Object object, Object... args);
}