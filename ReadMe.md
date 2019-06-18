[![](https://jitpack.io/v/Vove7/SmartKey.svg)](https://jitpack.io/#Vove7/SmartKey)

# SmartKey

- [基本使用](#基本使用)
- [更多操作](#更多操作)
- [自定义持久化实现](#自定义持久化实现)
- [引入SmartKey](#引入SmartKey)

> 利用Kotlin委托实现优雅地持久化存储应用配置。

- 支持纯Kotlin项目
- 支持Android项目
- 支持自定义持久化实现

### 基本使用

#### 初始化

- Kotlin项目

无需多余操作

- Android项目

在Application中初始化：

```kotlin
AndroidSettings.init(context)
```

1. 定义配置类
```kotlin
//这里可以使用注解配置存储文件名，多个配置类可分文件存储
@Config("app_config")
object AppConfig {

    /**
     * 基本类型存储
     */
    var text: String by smartKey("a", keyId = R.string.key_text)

    var number: Int  by smartKey(50)


    var intArr: Array<Int> by smartKey(emptyArray())
    var userInfo: UserInfo? by smartKey(null, encrypt = true)

    /**
     * 可空基础类型使用：SmartKey.auto
     */
    var nullableInt: Int? by smartKey(null)
}

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

```

3. 配置类附加功能

继承`BaseConfig`拥有配置类基础操作
```kotlin
object AppConfig : BaseConfig

//清空此配置所有key
AppConfig.clear()
```

### demo

见app目录

<img src="screenshot/Screenshot.jpg" width= "300px" />

### 更多操作

- 你可以指定变量对应存储的key：
```kotlin
    //指定key 
    //import cn.vove7.smartkey.smartKey
    var text: String by smartKey("defaultValue", key = "your_key")
    
    //安卓项目可指定keyId
    //import cn.vove7.smartkey.android.smartKey
    var textAndroid: String by smartKey("defaultValue", keyId = R.string.key)
```

- 选择是否加密数据：

```kotlin
    //使用encrypt来声明加密存储数据
    var userInfo: UserInfo? by smartKey(null, encrypt = true)

```

- 使用`SmartKey`直接操作key和value

```kotlin
SmartKey["app2", "key"] = 1 //configName, key, value
SmartKey["text"] = "234" //key, value

val s = SmartKey["app2", "text", "de"]//config, key, default

```

### 自定义持久化实现

1. 实现`com.russhwolf.settings.Settings`接口

//注意必须存在构造函数(val configName:String)
`class MySettingsImpl(val configName:String) : Settings`

2. 设置实现类为MySettingsImpl
- kotlin

`SmartKey.settingImplCls = MySettingsImpl::class.java`

- Android

```kotlin
AndroidSettings.init(context, MySettingsImpl::class.java)
```

### 引入SmartKey

###### Step 1. Add it in your root build.gradle at the end of repositories:
```groovy
allprojects {
    repositories {
        //...
        maven { url 'https://jitpack.io' }
        maven { url = 'https://dl.bintray.com/russhwolf/multiplatform-settings' }
    }
}
```
###### Step 2. Add the dependency
```groovy
dependencies {
    implementation "com.github.Vove7:SmartKey:$lastest_version"
}
```
> lastest_version : [![](https://jitpack.io/v/Vove7/SmartKey.svg)](https://jitpack.io/#Vove7/SmartKey)


### TODO

- 完成加密


### Thanks

- [Kotlin](https://kotlinlang.org/)
- 底层存储使用[multiplatform-settings](https://github.com/russhwolf/multiplatform-settings)
- [Gson](https://github.com/google/gson)
