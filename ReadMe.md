# SmartKey

> 利用Kotlin委托实现优雅地持久化存储App配置。



### 基本使用

0. 在Application中初始化：

```kotlin
SmartKet.init(context)
```

1. 定义配置类：
```kotlin
object AppConfig {

    /**
     * 基本类型存储
     */
    var text: String by SmartKey("defaultValue")
    var number: Int  by SmartKey(50)

    /**
     * 其他类型使用：[SmartKey.auto]
     */
    var intArr: Array<Int> by SmartKey.auto(emptyArray())

    var userInfo: UserInfo? by SmartKey.auto(null)

}

data class UserInfo(
        val name: String
)
```

2. 使用

此时你可以像这样使用：

```kotlin
//获取存储值
val value = AppConfig.text
val n = AppConfig.number 

//实时存储
AppConfig.text = "setValue"
AppConfig.number = 0

//存储登录用户数据
AppConfig.userInfo = UserInfo("new_user")

```

### demo

<img src="screenshot/Screenshot.jpg" width= "300px" />

### 更多

你可以指定变量对应存储的key：
```kotlin
    //指定key 或 keyId
    var text: String by SmartKey("defaultValue", key = "your_key", keyId = R.string.key_text)
```

选择是否加密数据：

```kotlin
    //使用encrypt来声明加密存储数据
    var userInfo: UserInfo? by SmartKey.auto(null, encrypt = true)

```

使用`SmartKey`直接操作key和value

```kotlin
SmartKey.set("text", "aaa")
SmartKey.get("number", -1)

```


### TODO

- 完成加密
