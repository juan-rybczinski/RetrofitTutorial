package com.rybczinski.retrofittutorial.helpers

import com.rybczinski.retrofittutorial.api.model.ApiError
import com.rybczinski.retrofittutorial.ui.MainActivity
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

class ErrorUtils {
    companion object {
        fun parseError(response: Response<out Any>): ApiError? {
            val converter: Converter<ResponseBody, ApiError> = MainActivity
                .retrofit.responseBodyConverter(ApiError::class.java, arrayOfNulls<Annotation>(0))

            val error: ApiError?
            try {
                error = converter.convert(response.errorBody())
            } catch (e: IOException) {
                return ApiError()
            }
            return error
        }
    }
}