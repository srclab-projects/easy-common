package sample.java.xyz.srclab.core.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.testng.Assert;
import org.testng.annotations.Test;
import xyz.srclab.common.lang.*;
import xyz.srclab.common.test.TestLogger;
import xyz.srclab.common.utils.Counter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

public class LangSample {

    private static final TestLogger logger = TestLogger.DEFAULT;

    @Test
    public void testLet() {
        int sum = Let.of("1,2,3,4,5,6,7,8,9,10")
            .then(s -> s.split(","))
            .then(Arrays::asList)
            .then(l -> l.stream().mapToInt(Integer::parseInt))
            .then(IntStream::sum)
            .get();
        //55
        logger.log("sum: {}", sum);
    }

    @Test
    public void testRef() {
        Ref<String> ref = Ref.with("1");
        List<String> list = Arrays.asList("-1", "-2", "-3");

        //here <String> should be final without Ref
        list.forEach(i -> ref.set(ref.get() + i));
        //1-1-2-3
        logger.log("result: {}", ref.get());
    }

    @Test
    public void testCurrent() {
        //null
        logger.log(Current.getOrNull("1"));
        Current.set("1", "2");
        //2
        logger.log(Current.get("1"));
        //System.currentTimeMillis();
        logger.log(Current.millis());
    }

    @Test
    public void testDefault() {
        //UTF-8
        logger.log(Defaults.charset());
        //Locale.getDefault();
        logger.log(Defaults.locale());
    }

    @Test
    public void testEnvironment() {
        logger.log(Environment.getProperty(Environment.OS_ARCH_KEY));
        logger.log(Environment.availableProcessors());
        logger.log(Environment.osVersion());
        logger.log(Environment.isOsWindows());
    }

    @Test
    public void testCharsFormat() {
        String byFast = CharsFormat.fastFormat("1, 2, {}", 3);
        String byMessage = CharsFormat.messageFormat("1, 2, {0}", 3);
        String byPrintf = CharsFormat.printfFormat("1, 2, %d", 3);
        //1, 2, 3
        logger.log("byFast: {}", byFast);
        logger.log("byMessage: {}", byMessage);
        logger.log("byPrintf: {}", byPrintf);
    }

    @Test
    public void testCharsTemplate() {
        Map<Object, Object> args = new HashMap<>();
        args.put("name", "Dog");
        args.put("name}", "DogX");
        args.put(1, "Cat");
        args.put(2, "Bird");
        CharsTemplate template1 = CharsTemplate.resolve(
            "This is a {name}, that is a {}", "{", "}");
        //This is a Dog, that is a Cat
        logger.log(template1.process(args));
        CharsTemplate template2 = CharsTemplate.resolve(
            "This is a } {name}, that is a {}}", "{", "}");
        //This is a } Dog, that is a Cat}
        logger.log(template2.process(args));
        CharsTemplate template3 = CharsTemplate.resolve(
            "This is a } \\{{name\\}} ({name}), that is a {}\\\\\\{\\", "{", "}", "\\");
        //This is a } {DogX (Dog), that is a Bird\{\
        logger.log(template3.process(args));
    }

    @Test
    public void testNamingCase() {
        String upperCamel = "UpperCamel";
        String lowerCamel = NamingCase.UPPER_CAMEL.convertTo(upperCamel, NamingCase.LOWER_CAMEL);
        //upperCamel
        logger.log("lowerCamel: {}", lowerCamel);
    }

    @Test
    public void testLazy() {
        Lazy<String> lazy = Lazy.of(() -> UUID.randomUUID().toString());
        String value1 = lazy.get();
        String value2 = lazy.get();
        lazy.refresh();
        String value3 = lazy.get();
        //value1 == value2
        //value2 != value3
        logger.log("value1: {}", value1);
        logger.log("value2: {}", value2);
        logger.log("value3: {}", value3);
    }

    @Test
    public void testLazyString() {
        Counter counter = Counter.startsAt(0);
        LazyString<Integer> lazyString = LazyString.of(Lazy.of(counter::getAndIncrementInt));
        //0
        logger.log("lazyToString: {}", lazyString);
    }

    @Test
    public void testLoaders() {
        Class<String[][][]> cls = Loaders.loadClass("[[[Ljava.lang.String;");
        //class [[[Ljava.lang.String;
        logger.log("cls: {}", cls);
    }

    @Test
    public void testSpecParser() {
        String s = SpecParser.parseFirstClassNameToInstance("java.lang.String");
        //an empty String
        logger.log("s: {}", s);
    }

    @Test
    public void testBaseTypes() {

        //Anys examples:
        List<String>[] lists = Anys.as(new List[]{});
        int hash = Anys.anyOrArrayHash(Arrays.asList("", 1));
        boolean equals = Anys.anyOrArrayEquals(Arrays.asList("", 1), Arrays.asList("", 1));

        //Chars examples:
        byte[] bytes = "message10086".getBytes();
        String toChars = Chars.toChars(bytes);
        byte[] toBytes = Chars.toBytes(toChars);
        //message10086
        logger.log("toChars: {}", toChars);
        //[109, 101, 115, 115, 97, 103, 101, 49, 48, 48, 56, 54]
        logger.log("toBytes: {}", toBytes);

        //Nums examples:
        BigDecimal n = Nums.toBigDecimal("110");
        int i = Nums.toInt(new BigDecimal("2333"));
        //110
        logger.log("n: {}", n);
        //2333
        logger.log("i: {}", i);

        //Bools examples:
        boolean b = Bools.toBoolean("true");
        //true
        logger.log("b: {}", b);

        //Dates examples:
        String timestamp = Dates.timestamp();
        LocalDateTime localDateTime = Dates.toLocalDateTime("2011-12-03T10:15:30");
        //20210207144816045
        logger.log("timestamp: {}", timestamp);
        //2011-12-03T10:15:30
        logger.log("localDateTime: {}", localDateTime);

        //Compares example:
        //99
        logger.log("inBounds: {}", Compares.inBounds(100, 0, 99));

        //Checks examples:
        try {
            Checks.checkArgument(1 == 2, "1 != 2");
        } catch (IllegalArgumentException e) {
            //java.lang.IllegalArgumentException: 1 != 2
            logger.log("e: {}", e);
        }

        //Requires examples:
        try {
            Object notNull = Requires.notNull(null, "null");
        } catch (NullPointerException e) {
            //java.lang.NullPointerException: null
            logger.log("e: {}", e);
        }

        //Enums examples:
        TestEnum t1 = Enums.value(TestEnum.class, "T1");
        //t1: T1
        logger.log("t1: {}", t1);
        TestEnum t2 = Enums.valueIgnoreCase(TestEnum.class, "t2");
        //t2: T2
        logger.log("t2: {}", t2);
    }

    @Test
    public void testRandom() {
        //[10, 20)
        for (int j = 0; j < 10; j++) {
            logger.log("random[10, 20): {}", Randoms.between(10, 20));
        }

        RandomSupplier<?> randomSupplier = RandomSupplier.newBuilder()
            .mayBe(20, "A")
            .mayBe(20, "B")
            .mayBe(60, "C")
            .build();
        int countA = 0;
        int countB = 0;
        int countC = 0;
        for (int i = 0; i < 1000; i++) {
            Object result = randomSupplier.get();
            if (result.equals("A")) {
                countA++;
            } else if (result.equals("B")) {
                countB++;
            } else if (result.equals("C")) {
                countC++;
            }
        }
        int total = countA + countB + countC;
        Assert.assertEquals(total, 1000);
        //countA: 189, countB: 190, countC: 621, total: 1000
        logger.log("countA: {}, countB: {}, countC: {}, total: {}", countA, countB, countC, total);
    }

    @Test
    public void testCachingProductBuilder() {

        class CachingBuilderSample extends CachingProductBuilder<String> {

            private String value = "null";
            private long counter = 0L;

            public void setValue(String value) {
                this.value = value;
                this.commitModification();
            }

            @NotNull
            @Override
            protected String buildNew() {
                return value + counter++;
            }
        }

        CachingBuilderSample cachingBuilderSample = new CachingBuilderSample();
        cachingBuilderSample.setValue("1");
        String value1 = cachingBuilderSample.build();
        String value2 = cachingBuilderSample.build();
        cachingBuilderSample.setValue("2");
        String value3 = cachingBuilderSample.build();
        //10
        logger.log("value1: {}", value1);
        //10
        logger.log("value2: {}", value2);
        //21
        logger.log("value3: {}", value3);
    }

    @Test
    public void testProcess() {
        if (Environment.isOsUnix()) {
            testProcessing("echo", "ECHO_CONTENT");
        }
        if (Environment.isOsWindows()) {
            testProcessing("cmd.exe", "/c", "echo " + "ECHO_CONTENT");
        }
    }

    private void testProcessing(String... command) {
        Processing processing = Processing.newProcessing(command);
        processing.waitForTermination();
        String output = processing.outputString();
        //ECHO_CONTENT
        logger.log(output);
    }

    @Test
    public void testShell() {
        logger.log("Hello, world!");
        logger.log("123{}456{}{}", EscChars.linefeed(), EscChars.newline(), EscChars.reset());
        logger.log("{}{}{}",
            SgrChars.foregroundRed("red"),
            SgrChars.backgroundCyan(" "),
            SgrChars.foregroundGreen("green")
        );
        logger.log("{}{}{}",
            SgrChars.withParam("bright red", SgrParam.FOREGROUND_BRIGHT_RED),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("bright green", SgrParam.FOREGROUND_BRIGHT_GREEN)
        );
        logger.log("{}{}{}",
            SgrChars.withParam("color 8", SgrParam.foregroundColor(8)),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("rgb(100, 100, 50)", SgrParam.foregroundColor(100, 100, 50))
        );
        logger.log(CtlChars.beep());
        logger.log("123\010456\007");
        logger.log("123{}456{}", CtlChars.backspaces(), CtlChars.beep());
    }

    @Test
    public void testSingleAccessor() {
        TestSingleAccessor singleAccessor = new TestSingleAccessor();
        Assert.assertNull(singleAccessor.getOrNull());
        Assert.assertEquals("666", singleAccessor.getOrElse("666"));
        Assert.assertEquals("666", singleAccessor.getOrElse(() -> "666"));
        singleAccessor.set("777");
        Assert.assertEquals("777", singleAccessor.get());

        TestGenericSingleAccessor genericSingleAccessor = new TestGenericSingleAccessor();
        Assert.assertNull(genericSingleAccessor.getOrNull());
        Assert.assertEquals("666", genericSingleAccessor.getOrElse("666"));
        Assert.assertEquals("666", genericSingleAccessor.getOrElse(() -> "666"));
        genericSingleAccessor.set("777");
        Assert.assertEquals("777", genericSingleAccessor.get());

        TestMapAccessor mapAccessor = new TestMapAccessor();
        Assert.assertNull(mapAccessor.getOrNull("1"));
        Assert.assertEquals("666", mapAccessor.getOrElse("1", "666"));
        Assert.assertEquals("666", mapAccessor.getOrElse("1", (k) -> "666"));
        mapAccessor.set("1", "777");
        Assert.assertEquals("777", mapAccessor.get("1"));

        TestGenericMapAccessor genericMapAccessor = new TestGenericMapAccessor();
        Assert.assertNull(genericMapAccessor.getOrNull("1"));
        Assert.assertEquals("666", genericMapAccessor.getOrElse("1", "666"));
        Assert.assertEquals("666", genericMapAccessor.getOrElse("1", (k) -> "666"));
        genericMapAccessor.set("1", "777");
        Assert.assertEquals("777", genericMapAccessor.get("1"));
    }

    public enum TestEnum {
        T1,
        T2
    }

    public static class TestSingleAccessor implements SingleAccessor {

        private String value;

        @Override
        public <T> T getOrNull() {
            return (T) value;
        }

        @Override
        public void set(@Nullable Object value) {
            this.value = (String) value;
        }
    }

    public static class TestGenericSingleAccessor implements GenericSingleAccessor<String> {

        private String value;

        @Override
        public String getOrNull() {
            return value;
        }

        @Override
        public void set(@Nullable String value) {
            this.value = value;
        }
    }

    public static class TestMapAccessor implements MapAccessor {

        private final Map<Object, Object> values = new HashMap<>();

        @Override
        public @NotNull Map<Object, Object> contents() {
            return values;
        }
    }

    public static class TestGenericMapAccessor implements GenericMapAccessor<String, String> {

        private final Map<String, String> values = new HashMap<>();

        @Override
        public @NotNull Map<String, String> contents() {
            return values;
        }
    }
}
