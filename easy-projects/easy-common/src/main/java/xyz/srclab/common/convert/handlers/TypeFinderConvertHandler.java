package xyz.srclab.common.convert.handlers;

import xyz.srclab.annotation.Nullable;
import xyz.srclab.common.convert.ConvertHandler;
import xyz.srclab.common.convert.Converter;
import xyz.srclab.common.lang.finder.Finder;

import java.lang.reflect.Type;
import java.util.function.Function;

/**
 * @author sunqian
 */
public abstract class TypeFinderConvertHandler implements ConvertHandler {

    @Override
    public @Nullable Object convert(Object from, Class<?> to, Converter converter) {
        @Nullable Function<Object, Object> function = getFinder().find(to);
        if (function == null) {
            return null;
        }
        return function.apply(from);
    }

    @Override
    public @Nullable Object convert(Object from, Type to, Converter converter) {
        @Nullable Function<Object, Object> function = getFinder().find(to);
        if (function == null) {
            return null;
        }
        return function.apply(from);
    }

    protected abstract Finder<Type, Function<Object, Object>> getFinder();
}