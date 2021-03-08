package me.reezy.jetpack.httpcall

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.awaitResponse

@Suppress("UNCHECKED_CAST")
class HttpCall<T>(call: Call<T>, scope: CoroutineScope, private val response: (Response<T>) -> Unit) {

    companion object {
        private val defaultRetrofit by lazy { defaultRetrofitFactory() }

        private lateinit var defaultRetrofitFactory: () -> Retrofit

        private var globalErrorHandler: ((Throwable) -> Unit)? = null

        fun init(retrofitFactory: () -> Retrofit, errorHandler: (Throwable) -> Unit = {}) {
            defaultRetrofitFactory = retrofitFactory
            globalErrorHandler = errorHandler
        }

        private var services = mutableMapOf<Class<*>, Any>()

        fun <T : Any> getService(clazz: Class<T>, retrofit: Retrofit? = null): T {
            if (services.containsKey(clazz)) {
                return services[clazz] as T
            }
            return (retrofit ?: defaultRetrofit).create(clazz).also { services[clazz] = it }
        }
    }

    private var catch: ((Throwable) -> Unit)? = null
    private var finally: MutableList<() -> Unit> = mutableListOf()

    init {

        scope.launch(context = Dispatchers.Main) {
            try {
                response(call.awaitResponse())
            } catch (throwable: Throwable) {
                globalErrorHandler?.invoke(throwable)
                catch?.invoke(throwable)
            } finally {
                finally.forEach {
                    try {
                        it.invoke()
                    } catch (ex:Throwable) {
                        ex.printStackTrace()
                    }
                }
            }
        }
    }

    fun catch(block: (Throwable) -> Unit): HttpCall<T> {
        this.catch = block
        return this
    }

    fun finally(block: () -> Unit): HttpCall<T> {
        this.finally.add(block)
        return this
    }
}