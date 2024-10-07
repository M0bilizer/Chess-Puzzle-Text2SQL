package com.chess.puzzle.text2sql.web.service

import com.google.gson.Gson
import okhttp3.*
import org.springframework.stereotype.Service
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sagemakerruntime.SageMakerRuntimeClient
import software.amazon.awssdk.services.sagemakerruntime.model.InvokeEndpointRequest
import java.nio.charset.Charset

data class InputData(val inputs: String, val parameters: Map<String, Any>)
data class OutputData(val outputs: String) // Adjust according to your expected output structure

@Service
class SageMakerEndpointService {
    fun getInference(query: String) {
        val runtime = SageMakerRuntimeClient.builder()
            .region(Region.AP_SOUTHEAST_2)
            .build()

        val requestString =
            """
            {"foo": "bar"}
            """

        val request = InvokeEndpointRequest.builder()
            .endpointName("my-endpoint")
            .contentType("application/json")
            .body(SdkBytes.fromString(requestString, Charset.defaultCharset()))
            .build()

        val response = runtime.invokeEndpoint(request)

        println(response.body().asString(Charset.defaultCharset()))

        runtime.close()
    }
}