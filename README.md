# MiniSearch
search it

## MiniSearch有什么用途
- 广义的搜索，写入什么就搜索什么，可支持两端匹配。eg：输入‘爱’，可能搜索出‘我爱你’.
- 左向基准搜索.eg:你输入'XAT0',就可能搜索出订单号‘XAT0012412120’,‘XAT0121231212’,而不会搜索出‘XXXXAT0’.
- 拼音的搜索,eg:你输入‘huihe’，可能搜索出‘回合’和‘大部队的汇合’.
- 搜索并返回承载对象。MiniSearch并不一定要返回字符串，它可以返回你在录入数据时插入的对象。eg：你可以插入了一条字符串‘abc’，同时,
你可以设定‘abc'的根部为new User,当你下次搜索到’abc‘时，MiniSearch可以直接返回给你匹配的对象列表。
    
## 一、快速开始

1. 下载源码 or 用maven安装后引入gav：
```XML
<dependency>
    <groupId>com.ck.common</groupId>
    <artifactId>mini-search</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

2. 创建一颗空索引
```JAVA
// create
Instancer instance = MiniSearch.findInstance("hello_world");
```
其中， hello_world 即为这个索引的名字，

在业务中，你可替换为类似 user_name_idx 形式的名称，一个名字对应一个索引。

3. 将你需要的数据灌入 Mini-Search 
```JAVA
instance.add("为什么放弃治疗");
instance.add("为什么月经迟迟不来");
instance.add("为什么晚上不能照镜子");
instance.add("为蛇要放弃治疗");
```

4.进行搜索！

先尝试一个普通的场景：
```java
//try searching
Collection<Object> result = instance.find("为什么");

```

我们得到结果：

- [为什么放弃治疗, 为什么晚上不能照镜子, 为什么月经迟迟不来]
好吧，那拼音搜索混合中文呢？相同的办法：
```JAVA
Collection<Object> result2 = instance.find("为sheyao");
```
我们得到结果：

- [为蛇要放弃治疗]
大功告成！

到现在你几乎已经完全掌握如何在单个服务器环境建立和搜索内容了！



## 二、进阶用法

当然如果就这样结束，那也未免过于缭草了，MiniSearch 并不意味着 simple search ，

它的结构为业务提供了很多可能。



1. 搜索并返回承载对象

当我们匹配到字符串时，常常会希望返回匹配到的字符串代表的含义、权重或一个特殊的对象，

这时可以将这个对象加入我们的索引。

首先定义一个类（示例，但序列化是必须的）：
```java
public static class Info implements Serializable {
    private String i;

    private Info(String i) {
        this.i = i;
    }
    public String getI() {
        return i;
    }
}

```

之后我们将这个类的对象放入索引：
```java
// add all with object into index
instance.add("为什么放弃治疗", new Info("为什么放弃治疗:因为我没钱了"));
instance.add("为什么月经迟迟不来", new Info("为什么月经迟迟不来：因为我爱你"));
instance.add("为什么晚上不能照镜子", new Info("为什么晚上不能照镜子：因为没交电费"));
instance.add("为蛇要放弃治疗", new Info("为蛇要放弃治疗：没太听明白"));
```
使用相同步骤搜索并打印我们的结果：
```java
Collection<Object> result = instance.find("weisheyao");
```
这时我们Collection返回的就是这个对象了

- [com.ck.common.minisearchdemo.MiniSearchDemoApplicationTests$Info@1bc53649]
有兴趣请自行字符化。



2. 满足你古怪的癖好和其他配置

你可以自定义整个搜索的配置，包括搜索偏好，一些核心参数等。

实例化一个默认的配置：
```java
// configuration
MiniSearchConfigure miniSearchConfigure = new MiniSearchConfigure();
// 关闭严格模式，会让你尽可能的匹配到搜索结果：

// setStrict false
miniSearchConfigure.setStrict(false);
// 使用简单核心，更适用于匹配订单号、英文串等场景：

// for alphabet,code
miniSearchConfigure.setCoreType(MiniSearchConfigure.CoreType.CODE.getCode());
// 配置完毕，我们将配置加入索引的生成器中：

// create with configuration
Instancer instance = MiniSearch.findInstance("hello_world", miniSearchConfigure);
// 这时当你添加订单号并搜索：

// add all into index
instance.add("IMX12012912001931");
instance.add("IMX12012912001932");
instance.add("WMX120129120019313");
instance.add("WMX12012912001934");
//try searching in Strict false
Collection<Object> result2 = instance.find("IMX7");
```
它匹配到了：

[IMX12012912001931, IMX12012912001932]
奇怪！输入 IMX7 本不应当匹配到任何内容。

但是非严格模式让你从右向左的尽可能获取到内容，但结果也不会太过奇怪，依然可以预测。

特别的，其他配置暂不在本文讨论范围，仅做列出（某些配置暂不生效）：
```java

    /**
     * 遍历条目时最大返回结果数
     */
    private int maxFetchNum = Integer.MAX_VALUE;

    /**
     * 仅返回全部匹配的入参结果，false则根据入参从尾向头截取进行匹配
     */
    private boolean strict = true;

    /**
     * 全匹配(freeMatch)模式：匹配字符串两端;关闭则转为最左前缀匹配
     */
    private boolean freeMatch = true;

    /**
     * 构建和搜索时忽略所有特殊字符
     */
    private boolean ignoreSymbol = true;

    /**
     * 设置忽略的正则表达式，同 @ignoreSymbol 合用
     */
    private String symbolPattern = "[\\pP\\pS\\pZ]";

    /**
     * 集群化通知标识前缀,后接 实例（index）名
     */
    private String notifyPatternChars = "search:notify:core:instancer:";

    /**
     * 持久化方式
     */
    private int persistence = Persistence.NO.getCode();

    /**
     * 集群容器线程池
     */
    private int clusterContainerPoolSize = 10;

    /**
     * 核心类型偏好，中文0; 英文/数字1
     */
    private int coreType = CoreType.PINYIN.getCode();

    /**
     * 全匹配(freeMatch)模式下，单语句最大处理的字符短语总数（超过则不继续匹配）
     */
    private static int phraseCharNum = 5;
```

注意，每个配置对应一棵索引，仅首次创建索引时放入即可。



## 三、集群同步

想必你已能发现， MiniSearch 的实质是工具类，并没有公共的服务需要部署，

所以实现集群的实质就是冗余，并通过广播（发布订阅模型）进行同步操作。

你可以自己想办法实现，但是 MiniSearch 也给出一种默认使用 redis 的方式。

若要快速集成这个默认方式，你需要：



1. 配置redis和redisTemplate：

你要确保你可以使用redisTemplate，同时需要配置其序列化形式：

// [必要]value值的序列化采用GenericJackson2JsonRedisSerializer，若有问题可尝试切换为jdk序列化
```java
@Bean
@ConditionalOnMissingBean(name = "redisTemplate")
public RedisTemplate<Object, Object> redisTemplate(
        RedisConnectionFactory redisConnectionFactory) {
    RedisTemplate<Object, Object> template = new RedisTemplate<>();
    GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
    template.setValueSerializer(jackson2JsonRedisSerializer);
    template.setHashValueSerializer(jackson2JsonRedisSerializer);
    template.setDefaultSerializer(jackson2JsonRedisSerializer);
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setConnectionFactory(redisConnectionFactory);
    return template;
}
```

之后你可以直接加入包扫描：
```java
@Configuration
@ComponentScan("com.ck.common.mini")
public class MiniConfig {

```
一定使用 ApplicationListener<ContextRefreshedEvent>  进行数据初始化：
```java
@Component
public class InitBean implements ApplicationListener<ContextRefreshedEvent> {

    @Override
 public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        Instancer instance = ClusterMiniSearch.findInstance("hello_world");
        // add all into index，add 已经为线程安全的实现
  instance.add("为什么放弃治疗");
        instance.add("为什么月经迟迟不来");
        instance.add("为什么晚上不能照镜子");
        instance.add("为蛇要放弃治疗");
    }
}
```
项目启动后，就可以随意调用这个索引实例了，例：
```java
Instancer instance = ClusterMiniSearch.findInstance("hello_world");
Collection<Object> why = instance.find("为什么");
```
输出：

- [为什么放弃治疗, 为什么晚上不能照镜子, 为什么月经迟迟不来]


## 四、基本原理

先上一张CODE核心的索引结构图(前例中匹配订单号、英文串的核心类型)，

结构相对简单便于理解：

技术部文档 > [工具]右侧模糊匹配搜索封装工具类(Mini-Search) > etree5.png

本质为一颗字典树，每个字符被切分，成为了Node的key，而为了内存占用考虑，Node仅持有子节点不持有父节点。

domain可以理解为所有冲突单元，本质为一个map。

每个被标记为是词尾的节点，都会附带一个carrier承载者，而carrier就是真正的数据。

针对默认的（简体/繁体）拼音核心，图就不上了，其逻辑虽然类似，但又对字典树进行了变体，我会将中文先进行拼音化，再用carrier对象无法满足匹配的要求，

于是设置其为map（carrierMap），第二次去保存冲突项。匹配时需要找到对应carrierMap的key，再挨个进行匹配。



## 五、答疑环节



1. 是否支持数据/索引持久化？

      暂时不支持;但是通过一些手段，数据持久化是可以的。比如在你存储节点中，设置对象某个属性为SQL语句;
```java
public static class Info implements Serializable {
    private String sql;
```
     也就是每个被匹配到的对象可以去数据库再拿一次，同理，可做推广为 Redis 的某个 key 等...



2. 支持搜索结果排序吗？

    同样的手法，你可以直接让这个对象去实现 Comparable 接口，当然你也可以预先去设置权重去设置到对象中，不再赘述。



3. 有什么不适合的搜索场景吗？

    有，首先左测匹配是搞不了的，需要的话请自行了解ES;

    再有就是字符过长的场景，请不要尝试压测自己的主机，

    适合的场景主要是短语、名字、企业名、游戏名等较短的条目，不推荐单语句超过500字的内容插入索引树。

    更不要录入整本的《三国演义》、《红楼梦》等！



4. springboot 化？

    暂不。

