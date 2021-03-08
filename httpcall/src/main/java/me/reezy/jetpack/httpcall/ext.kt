package me.reezy.jetpack.httpcall

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import retrofit2.Call
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit

inline fun <reified T : Any> http(retrofit: Retrofit? = null) = HttpCall.getService(T::class.java, retrofit)

fun <T : Any> Call<T>.result(fragment: Fragment, onResult: (T) -> Unit): HttpCall<T> {
    return result(fragment.lifecycleScope, onResult)
}

fun <T : Any> Call<T>.result(activity: FragmentActivity, onResult: (T) -> Unit): HttpCall<T> {
    return result(activity.lifecycleScope, onResult)
}

fun <T : Any> Call<T>.result(scope: CoroutineScope = GlobalScope, onResult: (T) -> Unit): HttpCall<T> {
    return HttpCall(this, scope) { response: Response<T> ->
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
        val body = response.body() ?: throw KotlinNullPointerException("response body was null")
        onResult(body)
    }
}

fun <T : Any> Call<T>.response(fragment: Fragment, onResponse: (Response<T>) -> Unit): HttpCall<T> {
    return HttpCall(this, fragment.lifecycleScope, onResponse)
}

fun <T : Any> Call<T>.response(activity: FragmentActivity, onResponse: (Response<T>) -> Unit): HttpCall<T> {
    return HttpCall(this, activity.lifecycleScope, onResponse)
}

fun <T : Any> Call<T>.response(scope: CoroutineScope = GlobalScope, onResponse: (Response<T>) -> Unit): HttpCall<T> {
    return HttpCall(this, scope, onResponse)
}