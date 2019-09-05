package com.github.arekolek.phone

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.*
import java.io.File
import retrofit2.http.POST
import retrofit2.http.Multipart


interface RestApiService {

    @Multipart
    @POST("upload")
    fun uploadAudio(
        @Part("recording") description: RequestBody,
        @Part file: MultipartBody.Part
    ): Call<ResponseBody>

}

