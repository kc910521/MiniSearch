# mini-search  

一个针对小型应用服务集群搜索的工具包（同时可部署为独立服务）

首个独立运行的spring-boot服务已经发布：  
[https://github.com/kc910521/minisearch-boot-server/releases/tag/0.1](https://github.com/kc910521/minisearch-boot-server/releases/tag/0.1)  

独立服务文档和源码：
[https://github.com/kc910521/minisearch-boot-server](https://github.com/kc910521/minisearch-boot-server)  

  

> 正文开始

> 
>
> 刘总（某创业公司 CTO）：
>
> ​	我们伟大APP下一版本的用户要可以模糊搜索到商品名，我需要开通个ES实例。
>
> ​	所以问题是哪家最便宜？
>
> 小陈（该公司唯一开发人员）：
>
> ​	不贵，直接买个ES实例一个月就三百多吧。或者直接买个服务器咱自己搭一套，一年才一千多。
>
> ​	（自以为解决了老板预算少的问题，看来年终奖二百的某东卡到手了）
>
> 刘总：
>
> ​	贵！咱商品价格都加一起还不到一千呢，你项目里引入个 lucene 好了，免费，我看行！
>
> 小陈：
>
> ​	咱这虽然量不大也是集群啊， lucene 没法直接支持，我先研究两天？
>
> 刘总：
>
> ​	能不能来个简单可行支持集群便宜轻量接入快速不新开服务就地取材的选择？
>
> 小陈：
>
> ​	那试试 **mini-search** ?



## mini-search 特点
- mini-search 自身**可以不需要任何额外的服务**，仅自己一个 jar 包就能实现搜索
- **双向匹配字符**。比如输入‘爱’，可以匹配到‘我爱你’。就像 MYSQL LIKE '%chars%'。
- 支持**中文拼音**。当你输入 ”huihe“，可能搜索出 ”回合“ 和 “部队汇合在湖北”以及 “我huihe”。
- **可以仅开启左侧基准搜索**。当你输入'ABC',就可能搜索出订单号 ‘ABCD’ ,而不会搜索出‘BC’.就像 MYSQL LIKE 'chars%'。
- **搜索并返回承载对象**！mini-search 可以返回你在插入数据时挂载在字符串匹配位置的对象。比如你可以插入一条字符串 ‘abc’ ，同时将 ‘abc' 的叶子节点放置为User对象,当你下次搜索到 ’abc‘ 时，mini-search 可以直接返回给你匹配的User对象列表。你可以将叶子节点的对象设置为一条 SQL 语句，这个技巧也可以让你把节点的数据存入数据库。
- 可以处理**分页**请求。
- 支持**携带业务ID的精准插入。**
## 一、快速开始

1. 下载源码，通过用maven install后引入gav到你项目的pom文件中：
```XML
<dependency>
    <groupId>com.ck.common</groupId>
    <artifactId>minisearch-core</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

2. 创建一颗空索引
```JAVA 
// create
Instancer instance = MiniSearch.findInstance("hello_world");
```
其中， hello_world 即为这个索引的名字，它用来区分不同的业务。

在业务中，你可替换 hello_world 为不同业务模块的名称，一个名字对应一个索引。

3. 将你需要的数据灌入 mini-search 
```JAVA
instance.add("为什么晚上不能照镜子");
instance.add("光电鼠标没有球");
instance.add("白色鼠标有球");
instance.add("白色鼠标");
instance.add("术镖，起立！");
instance.add("鼠标(shubiao)的英文：mouse");
instance.add("为什么shubiao没球了");
```

4.进行搜索！

### 1. 先尝试一个普通的搜索：
```java
//try searching
Collection<Object> result = instance.find("为什么");

```
我们得到结果：

> [为什么shubiao没球了, 为什么晚上不能照镜子]

### 2. 关键字搜索
进行搜索调用：
```JAVA
Collection<Object> result2 = instance.find("鼠标");
```
我们得到结果：

> [白色鼠标, 鼠标(shubiao)的英文：mouse, 白色鼠标有球, 光电鼠标没有球]

### 3. 拼音搜索

```JAVA
Collection<Object> result2 = instance.find("shubiao");
```
我们得到结果：

> [白色鼠标, 术镖，起立！, 鼠标(shubiao)的英文：mouse, 白色鼠标有球, 为什么shubiao没球了, 光电鼠标没有球]

可以看到 ‘术镖’ 也被搜索进来了。

### 4. 订单号搜索
针对订单号等场景，可能你只想从字符的最左端进行匹配，就像 MYSQL 的 ‘LIKE "eg%"’一样。
当你想搜索 ‘bc’, 而不搜索到  ‘abc12345’。你需要作出如下调整：

1. 改变默认配置
```java
MiniSearchConfigure miniSearchConfigure = new MiniSearchConfigure();
miniSearchConfigure.setFreeMatch(false);
miniSearchConfigure.setCoreType(MiniSearchConfigure.CoreType.CODE.getCode());
```
2. 将配置赋给新的索引树，之后插入测试数据：
```java
Instancer instance = MiniSearch.findInstance("code_finder", miniSearchConfigure);
instance.add("abc12345");
instance.add("mbc12345");
instance.add("bck12345");
Collection<Object> bc = instance.find("bc");
```
最后结果仅匹配到：
> [bck12345]

**大功告成！**

到现在你几乎已经完全掌握如何在**单个服务器环境建立和搜索内容**了！



## 二、mini-search 的模块

简单介绍下 mini-search 的模块：

- **minisearch-core**

  提供对搜索的核心支持，同时它可以作为依赖包被独立进行引入，完成本地单点的搜索功能。推荐直接使用 PinYinInstancer，它也是默认的搜索方式。

- **minisearch-cluster-redis**

  mini-search 集群化部署的redis实现，使用 redis 完成集群之间通信，只要项目中配置好了 spring 的

  redis template，就可以将你对当前节点的修改广播到其他所有配置了相同 redis 的应用服务器上，并自动完成索引重建。

- **minisearch-boot-support**

  一键完成对于minisearch-cluster-*、minisearch-core 的整合，它会增强你的 springboot 项目，通过简单配置就可以将你的spring-boot服务直接升级为一台集群搜索的节点！并自动提供对外 http 的接口完成CRUD！

可能你对一个可以独立运行的小型搜索服务更感兴趣？

那可以直接下载这个集成了上述所有功能的 spring-boot 服务，启动之后，就用 HTTP 请求折磨它吧！

[https://github.com/kc910521/minisearch-boot-server](https://github.com/kc910521/minisearch-boot-server)




## 三、进阶用法

当然如果就这样结束，那也未免过于缭草了，mini-search 并不意味着 simple search ，

它的结构为业务提供了很多可能。



### 1. 搜索并返回承载对象

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
    @Override
    public String toString() {
        return "Info[" + i + "]";
    }
}

```

之后我们将这个类的对象放入索引：
```java
// add all with object into index
instance.add("为什么放弃治疗", new Info("因为我没钱了"));
instance.add("为什么迟迟不来", new Info("因为我爱你"));
instance.add("为什么晚上不能照镜子", new Info("因为没交电费"));
```
使用相同步骤搜索并打印我们的结果：
```java
instance.find("为什么晚上不能照镜子");
```
这时我们返回的是：
> [Info[因为没交电费]]

### 2. 分页搜索

咱们先搞一点数据进去：

```java

        Instancer instance = MiniSearch.findInstance("hello_world_page");
        instance.add("争渡争渡惊起一滩鸥鹭");
        instance.add("争渡争渡惊起二滩鸥鹭");
        instance.add("争渡争渡惊起三滩鸥鹭");
        instance.add("争渡争渡惊起四滩鸥鹭");
        instance.add("争渡争渡惊起五滩鸥鹭");
        instance.add("争渡争渡惊起六滩鸥鹭");
        instance.add("争渡争渡惊起七滩鸥鹭");
        instance.add("争渡争渡惊起八滩鸥鹭");
        instance.add("争渡争渡惊起久滩鸥鹭");
        instance.add("争渡争渡惊起十滩鸥鹭");
        instance.add("争渡争渡惊起十一滩鸥鹭");
        instance.add("争渡争渡惊起十二滩鸥鹭");
        instance.add("争渡争渡惊起十三滩鸥鹭");
```

然后做一个搜索：

```java
	instance.find("争渡", 0, 10)
    instance.find("争渡", 1, 10)
    instance.find("争渡", 2, 10)
```

0,1,2分别是页码，10就是要返回的数据个数，得到结果：

> [争渡争渡惊起六滩鸥鹭, 争渡争渡惊起久滩鸥鹭, 争渡争渡惊起一滩鸥鹭, 争渡争渡惊起五滩鸥鹭, 争渡争渡惊起二滩鸥鹭, 争渡争渡惊起四滩鸥鹭, 争渡争渡惊起十一滩鸥鹭, 争渡争渡惊起十二滩鸥鹭, 争渡争渡惊起十滩鸥鹭, 争渡争渡惊起十三滩鸥鹭]
>
> [争渡争渡惊起三滩鸥鹭, 争渡争渡惊起八滩鸥鹭, 争渡争渡惊起七滩鸥鹭, 争渡争渡惊起六滩鸥鹭, 争渡争渡惊起久滩鸥鹭, 争渡争渡惊起一滩鸥鹭, 争渡争渡惊起五滩鸥鹭, 争渡争渡惊起二滩鸥鹭, 争渡争渡惊起四滩鸥鹭, 争渡争渡惊起十一滩鸥鹭]
>
> [争渡争渡惊起十二滩鸥鹭, 争渡争渡惊起十滩鸥鹭, 争渡争渡惊起十三滩鸥鹭, 争渡争渡惊起三滩鸥鹭, 争渡争渡惊起八滩鸥鹭, 争渡争渡惊起七滩鸥鹭]

### 3. 携带ID插入同名数据

很多商品可能都是重名，那我们就传入一个商品ID，给索引值一个名字：

```java
        Instancer instance = MiniSearch.findInstance("id_test");
        instance.addWithId("0001", "极品狗粮", "极品狗粮1");
        instance.addWithId("0002", "极品狗粮", "极品狗粮2");
        instance.addWithId("0003", "杂粮煎饼", "极品狗粮3");

		instance.find("极品狗粮");
```

这样当我们再搜索时，就可以识别出他们是不同的产品，会搜索到：

> [极品狗粮1, 极品狗粮2]



### 4. 其他开放的配置

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

想必你已能发现， mini-search 的 **minisearch-core 模块实质是工具类**，并没有公共的服务需要部署，

所以实现集群的实质就是冗余，并通过广播（发布订阅模型）进行同步操作。

你可以参照 minisearch-cluster-redis 自己想办法实现 cluster，

不过同时 mini-search 也给出一种默认使用 redis 进行集群间同步数据的方式。

redis 几乎任何一个分布式的系统都会引入，使用 redis 体现了 mini-search 就地取材，节约成本的思想。

不想手工搭建而且是 springboot 项目的话，你可以直接跳到  [升级我的springboot为mini-search节点](#jump3)



若要快速集成这个默认方式，你需要：

### 1. 引入依赖

```xml
        <dependency>
            <groupId>com.ck.common</groupId>
            <artifactId>minisearch-cluster-redis</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
```



### 2. 配置redisTemplate

你要确保你可以使用redisTemplate，同时需要配置其序列化形式：

你可以试着简单的使用：

```java
@Import(DefaultMiniSearchSpringConfig.class)
public class 某springboot的主类Application {
        public 某springboot的主类Application(RedisTemplate<String, String> redisTemplate, DefaultMiniSearchSpringConfig defaultMiniSearchSpringConfig
                                       ) {
        assert redisTemplate != null;
    }
}
```

或者精细化配置：

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
### 3. 数据初始化

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
项目启动后，使用 ClusterMiniSearch 就可以随意调用这个索引实例并实现集群了：
```java
Instancer instance = ClusterMiniSearch.findInstance("hello_world");
Collection<Object> why = instance.find("为什么");
```
输出：

- [为什么放弃治疗, 为什么晚上不能照镜子, 为什么月经迟迟不来]




## 四、基本原理

### 1.存储结构

先上一张CODE核心的索引结构图(前例中匹配订单号、英文串的核心类型)，

结构相对简单便于理解：

![avatar](https://github.com/kc910521/MiniSearch/blob/master/doc/image/etree5.png)  



本质为一颗字典树，每个字符被切分，成为了Node的key，而为了内存占用考虑，Node仅持有子节点不持有父节点。

domain可以理解为所有冲突单元，本质为一个map。

每个被标记为是词尾的节点，都会附带一个carrier承载者，而carrier就是真正的数据。

默认的（简体/繁体）拼音核心 SpellingDictTree 虽然其逻辑虽然类似，但又对字典树进行了变体，我会将中文先进行拼音化，并对原始字符进行切割后多次存入字典数; carrier 被设置为map以便第二次去保存冲突项。匹配时需要找到对应 carrier 这个 map 的key，再挨个进行匹配。

### 2.分词原理
分词暂时处理的方式就是逐个切词，方法如图：
![avatar](https://github.com/kc910521/MiniSearch/blob/master/doc/image/spword.png)   



如字符串 abcd，不同的策略会有不同的处理结果：
- SubsequentWorker：
[abcd, bcd, cd, d]

- CombinationWorker（废弃）:
[abcd, ab, abc ...]


## 五、答疑环节



### 1. 是否支持数据/索引持久化？

暂时不支持;但是通过一些手段，数据持久化是可以的。比如在你存储节点中，设置对象某个属性为SQL语句;
```java
public static class Info implements Serializable {
    private String sql;
```
 也就是每个被匹配到的对象可以去数据库再拿一次，同理，可做推广为 Redis 的某个 key 等...



### 2. 支持搜索结果排序吗？

同样的手法，你可以直接让这个对象去实现 Comparable 接口，当然你也可以预先去设置权重去设置到对象中，不再赘述。



### 3. 有什么不适合的搜索场景吗？

字符过长的场景

适合的场景主要是短语、名字、企业名、游戏名等较短的条目，不推荐单语句超过500字的内容插入索引树。

若开启freeMatch不推荐超过200字。

更不要录入整本的《三国演义》、《红楼梦》等！



### 4. 如何立即升级我的spring-boot项目为一个搜索节点

- 引入依赖<span id="jump3">-</span>

  ```xml
          <dependency>
              <groupId>com.ck.common</groupId>
              <artifactId>minisearch-boot-support</artifactId>
              <version>1.0-SNAPSHOT</version>
          </dependency>
  ```

  

- 配置即可参见 minisearch-server，如下：

  ```java
  @SpringBootApplication
  @MiniSearchServer
  @Configuration
  @Import(DefaultMiniSearchSpringConfig.class)
  public class MinisearchServerApplication {
  
  
      public MinisearchServerApplication(RedisTemplate<String, String> redisTemplate, DefaultMiniSearchSpringConfig defaultMiniSearchSpringConfig
                                         ) {
          assert redisTemplate != null;
      }
  
      public static void main(String[] args) {
          SpringApplication.run(MinisearchServerApplication.class, args);
      }
  
  }
  
  ```

或者直接下载一个已配置好的独立springboot项目：

[https://github.com/kc910521/minisearch-boot-server](https://github.com/kc910521/minisearch-boot-server)



# 总结

1. 想测试、单机部署或者想搞自己的实现，可以使用

   ```xml
   <dependency>
       <groupId>com.ck.common</groupId>
       <artifactId>minisearch-core</artifactId>
       <version>1.0-SNAPSHOT</version>
   </dependency>
   ```

2. 想自己处理集群，可以使用 

   ```xml
   <dependency>
       <groupId>com.ck.common</groupId>
       <artifactId>minisearch-cluster-redis</artifactId>
       <version>1.0-SNAPSHOT</version>
   </dependency>
   ```

3. 想自己实现 springboot 的搜索节点部署形式，或想整合以上到你的springboot项目、以及寻找代码调用范例：

   ```xml
   <dependency>
       <groupId>com.ck.common</groupId>
       <artifactId>minisearch-boot-support</artifactId>
       <version>1.0-SNAPSHOT</version>
   </dependency>
   ```

4. 得到一个整合以上所有完成品--即一个springboot 为基础的 mini-search 的 HTTP 服务：

   直接到：

   [https://github.com/kc910521/minisearch-boot-server](https://github.com/kc910521/minisearch-boot-server)

   接口调用文档也在其中！











## 你的star，我的动力



## 感谢阅读至此！