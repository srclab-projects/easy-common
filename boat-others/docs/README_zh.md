# Boat Others:  支持特殊需求或第三方的[Boat/](../../)库

## Variables

* author: Sun Qian
* boat-version: 0.0.0

## 支持

* [Protobuf: boat-protobuf](#boat-protobuf)

## <a id="boat-protobuf"/>Protobuf: boat-protobuf

boat-protobuf为protobuf类型提供bean, 类型转换和序列化功能:

* ProtobufBeanResolveHandler: 支持protobuf类型的bean分解器处理器;
* ProtobufBeans.PROTOBUF_BEAN_RESOLVER: 支持protobuf类型的bean分解器, 继承自通用的bean分解器;
* ProtobufBeans.PROTOBUF_BEAN_COPY_OPTIONS: 适配protobuf类型的默认的bean属性赋值选项;
* ProtobufConverts.PROTOBUF_CONVERT_BEAN_COPY_OPTIONS: 适配protobuf类型的默认的bean转换属性赋值选项;
* ProtobufJsons.PROTOBUF_OBJECT_MAPPER: 支持protobuf类型的Jackson的object mapper;
* ProtobufJsons.PROTOBUF_JSON_SERIALIZER: 支持protobuf类型的Json序列化器.

### Java Examples

```java
public class ProtobufSample {

  private static final TestLogger logger = TestLogger.DEFAULT;

  @Test
  public void testProtobufResolver() {
    MessageData messageData = MessageData.newBuilder()
        .setType(MessageData.Type.TYPE_0)
        .setMessage("666")
        .addAllNumber(Arrays.asList("7", "8", "9"))
        .putEntry("m1", "mm1")
        .putEntry("m2", "mm2")
        .build();
    RequestMessage requestMessage = RequestMessage.newBuilder()
        .setId("123")
        .setData(messageData)
        .build();

    BeanResolver beanResolver = ProtobufBeans.PROTOBUF_BEAN_RESOLVER;
    BeanType dataType = beanResolver.resolve(messageData.getClass());
    Assert.assertEquals(
        dataType.properties().keySet().toArray(),
        new Object[]{"type", "message", "numberList", "entryMap", "class"}
    );
    BeanType requestType = beanResolver.resolve(requestMessage.getClass());
    Assert.assertEquals(
        requestType.properties().keySet().toArray(),
        new Object[]{"id", "data", "class"}
    );

    JavaMessageData javaMessageData = new JavaMessageData();
    Beans.copyProperties(messageData, javaMessageData, ProtobufBeans.PROTOBUF_BEAN_COPY_OPTIONS);
    //javaMessageData: {"type":"TYPE_0","message":"666","numberList":["7","8","9"],"entryMap":{"m1":"mm1","m2":"mm2"}}
    logger.log("javaMessageData: {}", JsonSerials.toJsonString(javaMessageData));
    Assert.assertEquals(javaMessageData.getType(), MessageData.Type.TYPE_0);
    Assert.assertEquals(javaMessageData.getMessage(), "666");
    Assert.assertEquals(javaMessageData.getNumberList(), Arrays.asList("7", "8", "9"));
    Assert.assertEquals(
        javaMessageData.getEntryMap(),
        Collects.newMap(new LinkedHashMap<>(), "m1", "mm1", "m2", "mm2")
    );

    JavaRequestMessage javaRequestMessage = new JavaRequestMessage();
    Beans.copyProperties(requestMessage, javaRequestMessage, ProtobufBeans.PROTOBUF_BEAN_COPY_OPTIONS);
    //javaRequestMessage: {"id":"123","data":{"type":"TYPE_0","message":"666","numberList":["7","8","9"],"entryMap":{"m1":"mm1","m2":"mm2"}}}
    logger.log("javaRequestMessage: {}", JsonSerials.toJsonString(javaRequestMessage));
    Assert.assertEquals(javaRequestMessage.getId(), "123");
    Assert.assertEquals(javaRequestMessage.getData().getType(), MessageData.Type.TYPE_0);
    Assert.assertEquals(javaRequestMessage.getData().getMessage(), "666");
    Assert.assertEquals(javaRequestMessage.getData().getNumberList(), Arrays.asList("7", "8", "9"));
    Assert.assertEquals(
        javaRequestMessage.getData().getEntryMap(),
        Collects.newMap(new LinkedHashMap<>(), "m1", "mm1", "m2", "mm2")
    );

    javaRequestMessage.setId("999");
    javaMessageData.setType(MessageData.Type.TYPE_1);
    javaMessageData.setMessage("java");
    javaMessageData.setNumberList(Arrays.asList("5", "6", "7"));
    javaMessageData.setEntryMap(Anys.as(Collects.newMap(new LinkedHashMap<>(), "j1", "jj1")));
    javaRequestMessage.setData(javaMessageData);

    MessageData.Builder messageDataBuilder = MessageData.newBuilder();
    Beans.copyProperties(javaMessageData, messageDataBuilder, ProtobufBeans.PROTOBUF_BEAN_COPY_OPTIONS);
    //messageDataBuilder: {"type":"TYPE_1","message":"java","number":["5","6","7"],"entry":{"j1":"jj1"}}
    logger.log(
        "messageDataBuilder: {}",
        ProtobufJsons.PROTOBUF_JSON_SERIALIZER.toJsonString(messageDataBuilder)
    );
    Assert.assertEquals(messageDataBuilder.getType(), MessageData.Type.TYPE_1);
    Assert.assertEquals(messageDataBuilder.getMessage(), "java");
    Assert.assertEquals(messageDataBuilder.getNumberList(), Arrays.asList("5", "6", "7"));
    Assert.assertEquals(
        messageDataBuilder.getEntryMap(),
        Collects.newMap(new LinkedHashMap<>(), "j1", "jj1")
    );

    RequestMessage.Builder requestMessageBuilder = RequestMessage.newBuilder();
    Beans.copyProperties(javaRequestMessage, requestMessageBuilder, ProtobufBeans.PROTOBUF_BEAN_COPY_OPTIONS);
    //requestMessageBuilder: {"id":"999","data":{"type":"TYPE_1","message":"java","number":["5","6","7"],"entry":{"j1":"jj1"}}}
    logger.log(
        "requestMessageBuilder: {}",
        ProtobufJsons.PROTOBUF_JSON_SERIALIZER.toJsonString(requestMessageBuilder)
    );
    Assert.assertEquals(requestMessageBuilder.getId(), "999");
    Assert.assertEquals(requestMessageBuilder.getData().getType(), MessageData.Type.TYPE_1);
    Assert.assertEquals(requestMessageBuilder.getData().getMessage(), "java");
    Assert.assertEquals(requestMessageBuilder.getData().getNumberList(), Arrays.asList("5", "6", "7"));
    Assert.assertEquals(
        requestMessageBuilder.getData().getEntryMap(),
        Collects.newMap(new LinkedHashMap<>(), "j1", "jj1")
    );

    RequestMessage convertRequestMessage =
        ProtobufConverts.PROTOBUF_CONVERTER.convert(javaRequestMessage, RequestMessage.class);
    //convertRequestMessage: {"id":"999","data":{"type":"TYPE_1","message":"java","number":["5","6","7"],"entry":{"j1":"jj1"}}}
    logger.log(
        "convertRequestMessage: {}",
        ProtobufJsons.PROTOBUF_JSON_SERIALIZER.toJsonString(convertRequestMessage)
    );
    Assert.assertEquals(convertRequestMessage.getId(), "999");
    Assert.assertEquals(convertRequestMessage.getData().getType(), MessageData.Type.TYPE_1);
    Assert.assertEquals(convertRequestMessage.getData().getMessage(), "java");
    Assert.assertEquals(convertRequestMessage.getData().getNumberList(), Arrays.asList("5", "6", "7"));
    Assert.assertEquals(
        convertRequestMessage.getData().getEntryMap(),
        Collects.newMap(new LinkedHashMap<>(), "j1", "jj1")
    );
  }

  public static class JavaRequestMessage {

    private String id;
    private JavaMessageData data;

    public String getId() {
      return id;
    }

    public void setId(String id) {
      this.id = id;
    }

    public JavaMessageData getData() {
      return data;
    }

    public void setData(JavaMessageData data) {
      this.data = data;
    }
  }

  public static class JavaMessageData {

    private MessageData.Type type;
    private String message;
    private List<String> numberList;
    private Map<String, String> entryMap;

    public MessageData.Type getType() {
      return type;
    }

    public void setType(MessageData.Type type) {
      this.type = type;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public List<String> getNumberList() {
      return numberList;
    }

    public void setNumberList(List<String> numberList) {
      this.numberList = numberList;
    }

    public Map<String, String> getEntryMap() {
      return entryMap;
    }

    public void setEntryMap(Map<String, String> entryMap) {
      this.entryMap = entryMap;
    }
  }
}
```

### Kotlin Examples

```kotlin
class ProtobufSample {
  @Test
  fun testProtobufResolver() {
    val messageData = MessageData.newBuilder()
      .setType(MessageData.Type.TYPE_0)
      .setMessage("666")
      .addAllNumber(Arrays.asList("7", "8", "9"))
      .putEntry("m1", "mm1")
      .putEntry("m2", "mm2")
      .build()
    val requestMessage = RequestMessage.newBuilder()
      .setId("123")
      .setData(messageData)
      .build()
    val beanResolver: BeanResolver = PROTOBUF_BEAN_RESOLVER
    val dataType = beanResolver.resolve(messageData.javaClass)
    Assert.assertEquals(
      dataType.properties.keys.toTypedArray(), arrayOf<Any>("type", "message", "numberList", "entryMap", "class")
    )
    val requestType = beanResolver.resolve(requestMessage.javaClass)
    Assert.assertEquals(
      requestType.properties.keys.toTypedArray(), arrayOf<Any>("id", "data", "class")
    )
    val javaMessageData = JavaMessageData()
    messageData.copyProperties(javaMessageData, PROTOBUF_BEAN_COPY_OPTIONS)
    //javaMessageData: {"type":"TYPE_0","message":"666","numberList":["7","8","9"],"entryMap":{"m1":"mm1","m2":"mm2"}}
    logger.log("javaMessageData: {}", javaMessageData.toJsonString())
    Assert.assertEquals(javaMessageData.type, MessageData.Type.TYPE_0)
    Assert.assertEquals(javaMessageData.message, "666")
    Assert.assertEquals(javaMessageData.numberList, listOf("7", "8", "9"))
    Assert.assertEquals(
      javaMessageData.entryMap,
      LinkedHashMap<Any, Any>().putEntries("m1", "mm1", "m2", "mm2")
    )
    val javaRequestMessage = JavaRequestMessage()
    requestMessage.copyProperties(javaRequestMessage, PROTOBUF_BEAN_COPY_OPTIONS)
    //javaRequestMessage: {"id":"123","data":{"type":"TYPE_0","message":"666","numberList":["7","8","9"],"entryMap":{"m1":"mm1","m2":"mm2"}}}
    logger.log("javaRequestMessage: {}", javaRequestMessage.toJsonString())
    Assert.assertEquals(javaRequestMessage.id, "123")
    Assert.assertEquals(javaRequestMessage.data!!.type, MessageData.Type.TYPE_0)
    Assert.assertEquals(javaRequestMessage.data!!.message, "666")
    Assert.assertEquals(javaRequestMessage.data!!.numberList, listOf("7", "8", "9"))
    Assert.assertEquals(
      javaRequestMessage.data!!.entryMap,
      LinkedHashMap<Any, Any>().putEntries("m1", "mm1", "m2", "mm2")
    )
    javaRequestMessage.id = "999"
    javaMessageData.type = MessageData.Type.TYPE_1
    javaMessageData.message = "java"
    javaMessageData.numberList = listOf("5", "6", "7")
    javaMessageData.entryMap = LinkedHashMap<Any, Any>().putEntries("j1", "jj1").asAny<Map<String?, String?>>()
    javaRequestMessage.data = javaMessageData
    val messageDataBuilder = MessageData.newBuilder()
    javaMessageData.copyProperties(messageDataBuilder, PROTOBUF_BEAN_COPY_OPTIONS)
    //messageDataBuilder: {"type":"TYPE_1","message":"java","number":["5","6","7"],"entry":{"j1":"jj1"}}
    logger.log(
      "messageDataBuilder: {}",
      PROTOBUF_JSON_SERIALIZER.toJsonString(messageDataBuilder)
    )
    Assert.assertEquals(messageDataBuilder.type, MessageData.Type.TYPE_1)
    Assert.assertEquals(messageDataBuilder.message, "java")
    Assert.assertEquals(messageDataBuilder.numberList, listOf("5", "6", "7"))
    Assert.assertEquals(
      messageDataBuilder.entryMap,
      LinkedHashMap<Any, Any>().putEntries("j1", "jj1")
    )
    val requestMessageBuilder = RequestMessage.newBuilder()
    javaRequestMessage.copyProperties(requestMessageBuilder, PROTOBUF_BEAN_COPY_OPTIONS)
    //requestMessageBuilder: {"id":"999","data":{"type":"TYPE_1","message":"java","number":["5","6","7"],"entry":{"j1":"jj1"}}}
    logger.log(
      "requestMessageBuilder: {}",
      PROTOBUF_JSON_SERIALIZER.toJsonString(requestMessageBuilder)
    )
    Assert.assertEquals(requestMessageBuilder.id, "999")
    Assert.assertEquals(requestMessageBuilder.data.type, MessageData.Type.TYPE_1)
    Assert.assertEquals(requestMessageBuilder.data.message, "java")
    Assert.assertEquals(requestMessageBuilder.data.numberList, listOf("5", "6", "7"))
    Assert.assertEquals(
      requestMessageBuilder.data.entryMap,
      LinkedHashMap<Any, Any>().putEntries("j1", "jj1")
    )
    val convertRequestMessage: RequestMessage =
      PROTOBUF_CONVERTER.convert<RequestMessage>(javaRequestMessage, RequestMessage::class.java)
    //convertRequestMessage: {"id":"999","data":{"type":"TYPE_1","message":"java","number":["5","6","7"],"entry":{"j1":"jj1"}}}
    logger.log(
      "convertRequestMessage: {}",
      PROTOBUF_JSON_SERIALIZER.toJsonString(convertRequestMessage)
    )
    Assert.assertEquals(convertRequestMessage.id, "999")
    Assert.assertEquals(convertRequestMessage.data.type, MessageData.Type.TYPE_1)
    Assert.assertEquals(convertRequestMessage.data.message, "java")
    Assert.assertEquals(convertRequestMessage.data.numberList, listOf("5", "6", "7"))
    Assert.assertEquals(
      convertRequestMessage.data.entryMap,
      LinkedHashMap<Any, Any>().putEntries("j1", "jj1")
    )
  }

  class JavaRequestMessage {
    var id: String? = null
    var data: JavaMessageData? = null
  }

  class JavaMessageData {
    var type: MessageData.Type? = null
    var message: String? = null
    var numberList: List<String?>? = null
    var entryMap: Map<String?, String?>? = null
  }

  companion object {
    private val logger = TestLogger.DEFAULT
  }
}
```

## <a id="contact"/>贡献和联系方式

* fredsuvn@163.com
* https://github.com/srclab-projects
* QQ群: 1037555759

## <a id="license"/>License

[Apache 2.0 license][license]

[license]: https://www.apache.org/licenses/LICENSE-2.0.html