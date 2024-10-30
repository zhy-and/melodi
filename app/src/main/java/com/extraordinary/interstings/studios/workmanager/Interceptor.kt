package com.extraordinary.interstings.studios.workmanager
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody


class TransSpesInters : Interceptor {


    interface BasicRequestListener {
        fun loadFailed(message: String)
        fun loadSuccessConfig(configInfo: String)
    }


    override fun intercept(chain: Interceptor.Chain): Response {
        weavesMusIntersRes = ""
        val weavesMusRequest = chain.request().newBuilder().build()
        val res = chain.proceed(weavesMusRequest)
        try {
            res.body?.let {
                weavesMusIntersRes = it.string()
                weavesMusType = it.contentType()
            }
        }catch (e: Exception){
            e.printStackTrace()
        }
        return res.newBuilder().body(weavesMusIntersRes.toResponseBody(weavesMusType)).build()
    }
    private var weavesMusType : MediaType?= null
    private var weavesMusIntersRes = ""
}