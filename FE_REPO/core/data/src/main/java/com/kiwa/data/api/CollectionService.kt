package com.kiwa.data.api

import com.kiwa.fluffit.model.flupet.response.Flupets
import retrofit2.http.GET

interface CollectionService {
    @GET("flupet-service/flupet/collection")
    suspend fun loadCollection(): Flupets
}
