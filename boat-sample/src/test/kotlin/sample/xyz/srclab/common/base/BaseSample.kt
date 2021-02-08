package sample.xyz.srclab.common.base

import org.testng.annotations.Test
import xyz.srclab.common.base.*
import xyz.srclab.common.base.Counter.Companion.counterStarts
import xyz.srclab.common.base.Format.Companion.fastFormat
import xyz.srclab.common.base.Format.Companion.messageFormat
import xyz.srclab.common.base.Format.Companion.printfFormat
import xyz.srclab.common.base.SpecParser.Companion.parseFirstClassNameToInstance
import xyz.srclab.common.base.Version.Companion.parseToVersion
import xyz.srclab.common.test.TestLogger
import java.math.BigDecimal
import java.util.*
import kotlin.text.toBigDecimal
import kotlin.text.toBoolean

class BaseSampleKt {

    @Test
    fun testCurrent() {
        Current.set("1", "2")
        //2
        logger.log(Current.get<Any>("1"))
        //System.currentTimeMillis();
        logger.log(Current.millis)
    }

    @Test
    fun testDefault() {
        //UTF-8
        logger.log(Default.charset)
        //Locale.getDefault();
        logger.log(Default.locale)
    }

    @Test
    fun testEnvironment() {
        logger.log(Environment.getProperty(Environment.KEY_OS_ARCH))
        logger.log(Environment.availableProcessors)
        logger.log(Environment.osVersion)
        logger.log(Environment.isOsWindows)
    }

    @Test
    fun testFormat() {
        val byFast = "1, 2, {}".fastFormat(3)
        val byMessage = "1, 2, {0}".messageFormat(3)
        val byPrintf = "1, 2, %d".printfFormat(3)
        //1, 2, 3
        logger.log("byFast: {}", byFast)
        logger.log("byMessage: {}", byMessage)
        logger.log("byPrintf: {}", byPrintf)
    }

    @Test
    fun testNamingCase() {
        val upperCamel = "UpperCamel"
        val lowerCamel = NamingCase.UPPER_CAMEL.convertTo(upperCamel, NamingCase.LOWER_CAMEL)
        //upperCamel
        logger.log("lowerCamel: {}", lowerCamel)
    }

    @Test
    fun testCounter() {
        val counter = 100.counterStarts()
        counter.getAndIncrementInt()
        counter.reset()
        val atomicCounter = 100.counterStarts(true)
        atomicCounter.incrementAndGetInt()
        atomicCounter.reset()
    }

    @Test
    fun testLoaders() {
        val cls = "[[[Ljava.lang.String;".loadClass<Array<Array<Array<String>>>>()
        //class [[[Ljava.lang.String;
        logger.log("cls: {}", cls)
    }

    @Test
    fun testSpecParser() {
        val s = "java.lang.String".parseFirstClassNameToInstance<String>()
        //an empty String
        logger.log("s: {}", s)
    }

    @Test
    fun testUtils() {

        //Anys examples:
        val lists = arrayOf<List<*>>().asAny<Array<List<String>>>()
        val hash = Arrays.asList("", 1).anyOrArrayHash()
        val equals = Arrays.asList("", 1).anyOrArrayEquals(Arrays.asList("", 1))

        //Chars examples:
        val bytes = "message10086".toByteArray()
        val toChars = bytes.toChars()
        val toBytes = toChars.toBytes()
        //message10086
        logger.log("toChars: {}", toChars)
        //[109, 101, 115, 115, 97, 103, 101, 49, 48, 48, 56, 54]
        logger.log("toBytes: {}", toBytes)

        //Nums examples:
        val n = "110".toBigDecimal()
        val i = BigDecimal("2333").toInt()
        //110
        logger.log("n: {}", n)
        //2333
        logger.log("i: {}", i)

        //Bools examples:
        val b = "true".toBoolean()
        //true
        logger.log("b: {}", b)

        //Dates examples:
        val timestamp = timestamp()
        val localDateTime = "2011-12-03T10:15:30".toLocalDateTime()
        //20210207144816045
        logger.log("timestamp: {}", timestamp)
        //2011-12-03T10:15:30
        logger.log("localDateTime: {}", localDateTime)

        //Checks examples:
        try {
            checkArgument(1 == 2, "1 != 2")
        } catch (e: IllegalArgumentException) {
            //java.lang.IllegalArgumentException: 1 != 2
            logger.log("e: {}", e)
        }

        //Requires examples:
        try {
            val notNull = null.notNull<Any>("null")
        } catch (e: NullPointerException) {
            //java.lang.NullPointerException: null
            logger.log("e: {}", e)
        }
    }

    @Test
    fun testCachingBuilder() {

        class CachingBuilderSample : CachingProductBuilder<String>() {
            private var value = "null"
            fun setValue(value: String) {
                this.value = value
                commitChange()
            }

            override fun buildNew(): String {
                return value + UUID.randomUUID().toString()
            }
        }

        val cachingBuilderSample = CachingBuilderSample()
        cachingBuilderSample.setValue("1")
        val value1 = cachingBuilderSample.build()
        val value2 = cachingBuilderSample.build()
        cachingBuilderSample.setValue("2")
        val value3 = cachingBuilderSample.build()
        //10c66dae9-c056-464e-8117-4787914c3af8
        logger.log("value1: {}", value1)
        //10c66dae9-c056-464e-8117-4787914c3af8
        logger.log("value2: {}", value2)
        //2c7c2e230-50b0-4a0f-8530-151723297fb8
        logger.log("value3: {}", value3)
    }

    @Test
    fun testShell() {
        val shell = Shell.DEFAULT
        shell.println("Hello", ",", "World", "!")
        shell.println(Arrays.asList("Hello", ",", "World", "!"))
        shell.println("123", ControlChars.linefeed, "456", EscapeChars.newline, EscapeChars.reset)
        shell.println(
            SgrChars.foregroundRed("red"),
            SgrChars.backgroundCyan(" "),
            SgrChars.foregroundGreen("green")
        )
        shell.println(
            SgrChars.withParam("bright red", SgrParam.FOREGROUND_BRIGHT_RED),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("bright green", SgrParam.FOREGROUND_BRIGHT_GREEN)
        )
        shell.println(
            SgrChars.withParam("color 8", SgrParam.foregroundColor(8)),
            SgrChars.backgroundCyan(" "),
            SgrChars.withParam("rgb(100, 100, 50)", SgrParam.foregroundColor(100, 100, 50))
        )
        shell.println(ControlChars.beep)
        shell.println("123", ControlChars.backspaces, "456", ControlChars.beep)
    }

    @Test
    fun testAbout() {
        val verString = "1.2.3-beta.2.3+123"
        val version: Version = verString.parseToVersion()
        val about = About.of(
            "name",
            "url",
            version,
            Licence.of("lName", "lUrl"),
            PoweredBy.of("pName", "pUrl", "pMail")
        )
        //name 1.2.3-beta.2.3+123, release on 2021-02-07T14:49:36.787+08:00[Asia/Shanghai]
        //url
        //Under the lName licence
        //Powered by pName
        logger.log("About: {}", about)
    }

    companion object {
        private val logger = TestLogger.DEFAULT
    }
}