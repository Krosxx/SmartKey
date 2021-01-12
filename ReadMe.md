[![](https://jitpack.io/v/Vove7/SmartKey.svg)](https://jitpack.io/#Vove7/SmartKey)

# SmartKey

> 利用Kotlin委托实现优雅地持久化存储应用配置。

1. 支持纯Kotlin项目
2. 支持Android项目
3. 支持自定义持久化实现
4. 空安全
5. 配置动态监听


- [基本使用](#基本使用)
- [更多操作](#更多操作)
- [基本存储实现](#基本存储实现)
- [自定义持久化实现](#自定义持久化实现)
- [引入SmartKey](#引入SmartKey)
- [注意事项](#注意事项)

### 基本使用

#### 初始化

这里可以使用注解 `@Config("app_config", implCls = ...)` 配置存储文件名，多个配置类可分文件存储。  
**安卓项目可以无需指明implCls，自动使用 `SharedPreference` 做存储；使用数据库需要适配，参考： [自定义持久化实现](#自定义持久化实现)**

1. 定义配置类
```kotlin
//注解非必须，不加注解，会使用默认配置路径，和默认存储实现
@Config("app_config", implCls = JsonSettings::class)
object AppConfig {

    //基本类型存储
    var text: String by smartKey("a")

    //可空基础类型
    var nullableInt: Int? by smartKey(null)
    var number: Int  by smartKey(50)

    //数组
    var intArr: Array<Int> by smartKey(emptyArray())

    //实体类
    var userInfo: UserInfo? by smartKey(null, encrypt = true)
    
    //实体数组  [操作即更新]  默认空列表
    var modelList by smartKeyList<ListModel>()
    //实体集    [操作即更新]  默认空集
    var modelSet by smartKeySet<ListModel>(emptySet())
    //         [操作即更新]
    var map by smartKeyMap<String, Int>()

}

//数组实体
data class ListModel(val s: String, val a: Int)

//实体类
data class UserInfo(
        var name: String,
        var email: String,
        var age: Int
)

```


2. 此时你可以像这样使用

```kotlin
//获取存储值
val value = AppConfig.text
val n = AppConfig.number 

//实时存储
AppConfig.text = "setValue"
AppConfig.number = 0

//存储登录用户数据
val user = UserInfo("new_user", "xx@xx.xx", 0)
AppConfig.userInfo = user

//注意修改实体中的属性无法触发持久化操作
user.name = "hello"
//需要赋值操作触发
AppConfig.userInfo = user

//操作 实时存储 无需显式赋值；需要注意避免循环 add， 可以使用addAll
AppConfig.modelList.add(ListModel("string", 1))
//set map 操作同 List 实时存储

```

3. 配置类附加功能

继承`BaseConfig`拥有配置类基础操作
```kotlin
object AppConfig : BaseConfig {
    //...
}
```
```kotlin

// 清空此配置所有key
AppConfig.clear()

// 直接存储key
AppConfig["key"] = 1 //key, value
AppConfig["text"] = "abc" //key, value

// 加密储存(目前只支持String及实体类型加密)
AppConfig["key", true] = "value"
"key" in AppConfig // is contains key
AppConfig -= "key" // remove key

// 获取可空类型的值
val strNullable: String? = AppConfig["dont_exists_key"]

//提供默认值 以获取不可空值
val s :String = AppConfig["text", "default"] //key, default


// 获取解密后的数据
val value :String? = AppConfig["key", true]


//获取可空数据
val user :UserInfo? = AppConfig["userInfo"]
// or
val user = AppConfig.get<UserInfo?>("userInfo")

//获取加密内容
val user: UserInfo? = AppConfig["userInfo", true]

```

### demo

见app目录

<img src="screenshot/Screenshot.jpg" width= "300px" />

### 更多操作

- 你可以指定变量对应存储的key：
```kotlin
object AppConfig2 : BaseConfig {    //指定key 
    //import cn.vove7.smartkey.smartKey
    var text: String by smartKey("defaultValue", key = "your_key")

    var textWithKey: String by smartKey("aaa", key="text_key")

    //安卓项目可通过resId指定keyId
    //import cn.vove7.smartkey.android.smartKey
    var textAndroid: String by smartKey("defaultValue", keyId = R.string.key)
}
```

- key 动态绑定

```kotlin
print(AppConfig2.textWithKey) //aaa
AppConfig2["text_key"] = "bbb"
print(AppConfig2.textWithKey) //bbb
```

- 选择是否加密数据：

```kotlin
    //使用encrypt来声明加密存储数据
    var userInfo: UserInfo? by smartKey(null, encrypt = true)

```

- 为每个配置类设置存储实现

> 不指定`implCls`时, 默认实现为`JsonSettings`

```kotlin
@Config(implCls = FileSettings::class)
class AppConfig1 {

}

@Config(implCls = PropertiesSettings::class)
class AppConfig2 {

}
```


- 无缓存的NoCacheKey

由于`SmartKey`会对value进行缓存，在多进程会存在问题。因此而生的`NoCacheKey`，保证读取的数据是实时的。
使用和SmartKey基本一致。

另外，在使用基于文件存储的Settings时，修改文件配置后，`NoCacheKey`可以监听文件变化，来载入最新配置。

```kotlin
    var text: String by noCacheKey("defaultValue", key = "your_key")
```

### 基本存储实现

- JsonSettings

使用json格式存储配置。

- PropertiesSettings

基于java `PropertiesSettings`持久化  
可设置`baseDir` `PropertiesSettings.baseDir = "..."`

- FileSettings

使用文件存储。  
可设置`baseDir` `FileSettings.baseDir = "..."`

**其中 `JsonSettings` 和 `PropertiesSettings` 继承与 `BaseSyncFileSetting`，配置可随文件修改重新加载到内存。为达到此目的，你需要使用 `NoCacheKey` 来确保实时读取到的是修改后的配置**

### 自定义持久化实现

1. 实现`com.russhwolf.settings.Settings`接口

```kotlin
//必须存在构造函数(val configName:String)

class MySettingsImpl(val configName:String) : Settings
```

2. 使用自定义实现类

在配置类设置注解参数`implCls`：

```kotlin
@Config(implCls = MySettingsImpl::class)
class AppConfig {

}
```

### 引入SmartKey

###### Step 1. Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
    repositories {
        //...
        maven { url 'https://jitpack.io' }
    }
}
```
###### Step 2. Add the dependency

- Kotlin

```groovy
dependencies {
    implementation "com.github.Vove7.SmartKey:smartkey:$lastest_version"
}
```

- Android

```groovy
dependencies {
    implementation "com.github.Vove7.SmartKey:smartkey-android:$lastest_version"
}
```
> lastest_version : [![](https://jitpack.io/v/Vove7/SmartKey.svg)](https://jitpack.io/#Vove7/SmartKey)

### 注意事项

在开启混淆时，请注意添加规则保证Config正常。

1. 由于类的持久化使用了Gson，请保证类字段名不被混淆，或使用注解@SerializedName
2. 在没有指定key的config实例，例如 val a :Int by smaryKey(0)，由于没有指定key，会默认使用变量名作为key，请保证变量名不被混淆（本人未验证kotlin编译混淆后，property name是否被改变）


### TODO

- DynamicKey

this key can reset to 0 everyday:

```kotlin
val dk by synamicKey(0) {  
    SimpleDateFormat("yyyy-MM-dd").format(Date()) + "_today_xxx_count"
}
```

- ExpirableKey


### Thanks

- [Kotlin](https://kotlinlang.org/)
- 底层存储使用[multiplatform-settings](https://github.com/russhwolf/multiplatform-settings)
- [Gson](https://github.com/googlo/gson)
