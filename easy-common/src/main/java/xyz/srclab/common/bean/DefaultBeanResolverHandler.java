package xyz.srclab.common.bean;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.cache.Cache;
import xyz.srclab.common.cache.threadlocal.ThreadLocalCache;
import xyz.srclab.common.exception.ExceptionWrapper;
import xyz.srclab.common.reflect.SignatureHelper;
import xyz.srclab.common.reflect.invoke.InvokerHelper;
import xyz.srclab.common.reflect.invoke.MethodInvoker;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultBeanResolverHandler implements BeanResolverHandler {

    private static final Cache<Class<?>, BeanClass> CACHE = new ThreadLocalCache<>();

    @Override
    public boolean supportBean(Class<?> beanClass) {
        return true;
    }

    @Override
    public BeanClass resolve(Class<?> beanClass) {
        return CACHE.getNonNull(beanClass, type -> {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(type);
                Method[] methods = type.getMethods();
                return BeanClassSupport.newBuilder()
                        .setType(type)
                        .setProperties(buildProperties(beanInfo.getPropertyDescriptors()))
                        .setMethods(buildMethods(methods))
                        .build();
            } catch (IntrospectionException e) {
                throw new ExceptionWrapper(e);
            }
        });
    }

    private Map<String, BeanProperty> buildProperties(PropertyDescriptor[] descriptors) {
        Map<String, BeanProperty> map = new LinkedHashMap<>();
        for (PropertyDescriptor descriptor : descriptors) {
            map.put(descriptor.getName(), new BeanPropertyImpl(descriptor));
        }
        return map;
    }

    private Map<String, BeanMethod> buildMethods(Method[] methods) {
        Map<String, BeanMethod> map = new LinkedHashMap<>();
        for (Method method : methods) {
            BeanMethod beanMethod = new BeanMethodImpl(method);
            map.put(beanMethod.getSignature(), beanMethod);
        }
        return map;
    }

    private static final class BeanPropertyImpl implements BeanProperty {

        private final PropertyDescriptor descriptor;
        private final Type genericType;

        private final @Nullable MethodInvoker getter;
        private final @Nullable MethodInvoker setter;

        private BeanPropertyImpl(PropertyDescriptor descriptor) {
            this.descriptor = descriptor;
            Method getter = descriptor.getReadMethod();
            Method setter = descriptor.getWriteMethod();
            if (getter == null && setter == null) {
                throw new IllegalStateException("Both getter and setter method are null: " + getName());
            }
            this.genericType = getter == null ?
                    setter.getGenericParameterTypes()[0]
                    :
                    getter.getGenericReturnType();
            this.getter = getter == null ? null : InvokerHelper.getMethodInvoker(getter);
            this.setter = setter == null ? null : InvokerHelper.getMethodInvoker(setter);
        }

        @Override
        public String getName() {
            return descriptor.getName();
        }

        @Override
        public Class<?> getType() {
            return descriptor.getPropertyType();
        }

        @Override
        public Type getGenericType() {
            return genericType;
        }

        @Override
        public boolean isReadable() {
            return getter != null;
        }

        @Nullable
        @Override
        public Object getValue(Object bean) throws UnsupportedOperationException {
            if (!isReadable()) {
                throw new UnsupportedOperationException("Property is not readable: " + descriptor);
            }
            return getter.invoke(bean);
        }

        @Override
        public @Nullable Method getReadMethod() {
            return descriptor.getReadMethod();
        }

        @Override
        public boolean isWriteable() {
            return setter != null;
        }

        @Override
        public void setValue(Object bean, @Nullable Object value) throws UnsupportedOperationException {
            if (!isWriteable()) {
                throw new UnsupportedOperationException("Property is not writeable: " + descriptor);
            }
            setter.invoke(bean, value);
        }

        @Override
        public @Nullable Method getWriteMethod() {
            return descriptor.getWriteMethod();
        }
    }

    private static final class BeanMethodImpl implements BeanMethod {

        private final Method method;
        private final String signature;
        private final MethodInvoker methodInvoker;

        private BeanMethodImpl(Method method) {
            this.method = method;
            this.signature = SignatureHelper.signMethod(this.method);
            this.methodInvoker = InvokerHelper.getMethodInvoker(this.method);
        }

        @Override
        public String getName() {
            return method.getName();
        }

        @Override
        public String getSignature() {
            return signature;
        }

        @Override
        public Class<?>[] getParameterTypes() {
            return method.getParameterTypes();
        }

        @Override
        public Type[] getGenericParameterTypes() {
            return method.getGenericParameterTypes();
        }

        @Override
        public int getParameterCount() {
            return method.getParameterCount();
        }

        @Override
        public Class<?> getReturnType() {
            return method.getReturnType();
        }

        @Override
        public Type getGenericReturnType() {
            return method.getGenericReturnType();
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public @Nullable Object invoke(Object bean, Object... args) {
            return methodInvoker.invoke(bean, args);
        }
    }
}