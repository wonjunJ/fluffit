package com.kiwa.data.api

import com.kiwa.fluffit.model.flupet.NicknameRequest
import com.kiwa.fluffit.model.flupet.response.BasicResponse
import com.kiwa.fluffit.model.flupet.response.FlupetHistory
import com.kiwa.fluffit.model.main.FullnessUpdateInfo
import com.kiwa.fluffit.model.main.HealthUpdateInfo
import com.kiwa.fluffit.model.main.response.FlupetResponse
import com.kiwa.fluffit.model.main.response.NewEggResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface FlupetService {
    @GET("flupet-service/flupet/info")
    suspend fun fetchMainUIInfo(): Response<FlupetResponse>

    @GET("flupet-service/flupet/fullness")
    suspend fun fetchFullnessInfo(): Response<FullnessUpdateInfo>

    @GET("flupet-service/flupet/health")
    suspend fun fetchHealthInfo(): Response<HealthUpdateInfo>

    @POST("flupet-service/flupet/new-egg")
    suspend fun createNewEgg(): Response<NewEggResponse>

    @PUT("flupet-service/flupet/nickname")
    suspend fun editFlupetNickname(
        @Body nickname: NicknameRequest
    ): Response<BasicResponse>

    @GET("flupet-service/flupet/history")
    suspend fun loadHistory(): FlupetHistory
}
