package test.java.xyz.srclab.common.reflect;

import xyz.srclab.common.test.TestLogger;

import java.util.Objects;

public class NewClass {

    private static final TestLogger testLogger = TestLogger.DEFAULT;

    static {
        testLogger.log("Load class: " + NewClass.class);
    }

    private final String param;

    public NewClass() {
        this("");
    }

    protected NewClass(String param) {
        this.param = param;
        testLogger.log("New instance: " + param);
    }

    private NewClass(String param0, String param1) {
        this(param0 + " : " + param1);
    }

    @Override
    public int hashCode() {
        return param.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof NewClass && Objects.equals(param, ((NewClass) obj).param);
    }
}
