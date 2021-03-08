# httpcall
 
使用 Coroutine + Retrofit 打造的最简单HTTP请求库 

## Gradle

``` groovy
repositories { 
    maven { url "https://gitee.com/ezy/repo/raw/android_public/"}
} 
dependencies {
    implementation "me.reezy.jetpack:httpcall:0.4.0" 
}
```

## 使用

```kotlin  
 
data class Reault(val data:String)

interface TestService { 
    @GET("test")
    fun test(): Call<Reault> 
} 

// 在 activity/fragment 中使用，获取请求结果
http<TestService>().test().result(this) {
    Log.e("api", it.data)
}

// 在 activity/fragment 中使用，获取请求响应对象
http<TestService>().test().response(this) {
    // it 是 Response<Result>
}
```

处理请求异常 

```
http<TestService>().test().result(this) {
    Log.e("api", it.data)
}.catch { throwable ->
    // 
}.finally {
    // 
}
```

显示请求状态

```
fun <T : Any>  HttpCall<T>.withSpinning(activity: FragmentActivity, spinning: Boolean = false, text: String = ""): HttpCall<T> {
    activity.apply {
        if (isFinishing || isDestroyed) return@apply
        val dialog = showLoading(spinning, text)

        finally { dialog.dismiss() }
    }
    return this
}


http<TestService>().test().result(this) {
    Log.e("api", it.data)
}.withSpinning(this) 
```
 
设置全局异常处理

```
HttpCall.globalErrorHandler = { thowable ->
    // ...
}

```


## LICENSE

The Component is open-sourced software licensed under the [Apache license](LICENSE).
