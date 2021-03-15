package com.g13.DRAG.API

import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.JsonRequest
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class Word(val id: String, val word: String)

/**
 * Implementation of the request for obtaining the weather information
 */
class GetWordInfoRequest(
    url: String,
    private val mapper: ObjectMapper,
    success: Response.Listener<Array<Word>>,
    error: Response.ErrorListener
) : JsonRequest<Array<Word>>(Method.GET, url, "", success, error) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<Array<Word>> {
        val currenciesDto = mapper.readValue(String(response.data), Array<Word>::class.java)
        return Response.success(currenciesDto, null)
    }
}