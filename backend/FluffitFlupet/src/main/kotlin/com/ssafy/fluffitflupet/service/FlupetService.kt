package com.ssafy.fluffitflupet.service

import com.ssafy.fluffitflupet.client.MemberServiceClientAsync
import com.ssafy.fluffitflupet.dto.*
import com.ssafy.fluffitflupet.entity.Flupet
import com.ssafy.fluffitflupet.entity.MemberFlupet
import com.ssafy.fluffitflupet.error.ErrorType
import com.ssafy.fluffitflupet.exception.CustomBadRequestException
import com.ssafy.fluffitflupet.repository.FlupetRepository
import com.ssafy.fluffitflupet.repository.MemberFlupetRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.system.measureTimeMillis

@Service
class FlupetService(
    private val memberFlupetRepository: MemberFlupetRepository,
    private val client: MemberServiceClientAsync,
    private val flupetRepository: FlupetRepository
) {
    //lombok slf4j를 쓰기 위해
    private val log = LoggerFactory.getLogger(FlupetService::class.java)

    //우선 요청을 받으면 포만감과 건강의 업데이트 시간을 확인한 다음 최신 데이터를 업데이트를 하고 이후 그 flupet의 정보를 리턴
    suspend fun getMainInfo(userId: String): MainInfoResponse? = coroutineScope {
        println("여기왔나?")
        val mainInfoDto = async { memberFlupetRepository.findMainInfoByUserId(userId) }
        //ErrorDecoder로 오류 처리를 할지, 아니면 try~catch로 오류처리를 할지
        val coinWait = async { client.getUserCoin(userId) }
        val dto = mainInfoDto.await()
        val coin = coinWait.await()
        if(dto == null){ //현재 플러펫이 없다(mainInfoDto 연산이 완료될때까지 '블로킹되지 않고' 기다리게 된다)
            val response = MainInfoResponse()
            response.coin = coin
            return@coroutineScope response
        } else {
            val response = MainInfoResponse(
                fullness = dto.fullness,
                health = dto.health,
                flupetName = dto.flupetName,
                imageUrl = dto.imageUrl,
                birthDay = dto.birthDay.toLocalDate(),
                age = "${ChronoUnit.DAYS.between(dto.birthDay, LocalDate.now())}일 ${ChronoUnit.HOURS.between(dto.birthDay, LocalDate.now())}",
                isEvolutionAvailable = if(dto.exp == 100) true else false,
                nextFullnessUpdateTime = dto.nextFullnessUpdateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                nextHealthUpdateTime = dto.nextHealthUpdateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
                coin = coin
            )
            return@coroutineScope response
        }
    }

    suspend fun updateNickname(userId: String, nickname: String?){
        val rgx = Regex("^[가-힣a-zA-Z0-9]+$") //한글 범위, 영어 대소문자 범위 및 숫자 범위를 포함하는 정규 표현식
        if(nickname.isNullOrEmpty()){
            throw CustomBadRequestException(ErrorType.TOO_SHORT_NICKNAME)
        }else if(nickname.length > 8){
            throw CustomBadRequestException(ErrorType.TOO_LONG_NICKNAME)
        }else if(!rgx.matches(nickname)){
            throw CustomBadRequestException(ErrorType.WRONG_CONDITION)
        }else {
            val mflupet = withContext(Dispatchers.IO){ memberFlupetRepository.findByMemberIdAndIsDeadIsFalse(userId).awaitSingleOrNull() }
            if(mflupet == null){
                throw CustomBadRequestException(ErrorType.INVALID_USERID)
            }
            mflupet.name = nickname
            memberFlupetRepository.save(mflupet)
        }
    }

    suspend fun getFullness(userId: String): FullResponse {
        val mflupet = withContext(Dispatchers.IO){ memberFlupetRepository.findByMemberIdAndIsDeadIsFalse(userId).awaitSingleOrNull() }
        if(mflupet == null){
            throw CustomBadRequestException(ErrorType.INVALID_USERID)
        }
        return FullResponse(
            fullness = mflupet.fullness,
            nextUpdateTime = (mflupet.fullnessUpdateTime!!.plusHours(2)).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            isEvolutionAvailable = if(mflupet.exp == 100) true else false
        )
    }

    suspend fun getHealth(userId: String): HealthResponse {
        //시간 측정용(임시)
        val measuredTime = measureTimeMillis {
            memberFlupetRepository.findByMemberIdAndIsDeadIsFalse(userId).awaitSingleOrNull()
        }
        log.info(measuredTime.toString())

        val mflupet = withContext(Dispatchers.IO){ memberFlupetRepository.findByMemberIdAndIsDeadIsFalse(userId).awaitSingleOrNull() }
        if(mflupet == null){
            throw CustomBadRequestException(ErrorType.INVALID_USERID)
        }
        return HealthResponse(
            health = mflupet.health,
            nextUpdateTime = (mflupet.healthUpdateTime!!.plusHours(2)).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            isEvolutionAvailable = if(mflupet.exp == 100) true else false
        )
    }

    suspend fun getPetCollection(userId: String): CollectionResponse {
        val flupets = withContext(Dispatchers.IO){ memberFlupetRepository.findFlupetsByUserId(userId).toList() }
        return CollectionResponse(flupets)
    }

    suspend fun generateFlupet(userId: String): GenFlupetResponse = coroutineScope {
        launch(Dispatchers.IO) {
            memberFlupetRepository.save(
                MemberFlupet(
                    flupetId = 0,
                    memberId = userId,
                    name = "새로운 알"
                )
            ).awaitSingle()
        }
        val fInfo = async(Dispatchers.IO) { flupetRepository.findById(0) }
        return@coroutineScope GenFlupetResponse(
            flupetName = "새로운 알",
            imageUrl = fInfo.await()?.imgUrl,
            fullness = 100,
            health = 100
        )
    }

    @Transactional
    suspend fun evolveFlupet(userId: String): EvolveResponse = coroutineScope {
        val mypet = async(Dispatchers.IO) { memberFlupetRepository.findByMemberIdAndFlupet(userId) }
        val mflupet = async(Dispatchers.IO) { memberFlupetRepository.findByMemberIdAndIsDeadIsFalse(userId).awaitSingleOrNull() }
        val mypetRst = mypet.await() ?: throw CustomBadRequestException(ErrorType.INVALID_USERID)
        //val t = flupetRepository.findByStage(mypetRst.stage+1).toList()
        val flupets = async(Dispatchers.IO) {
            if(mypetRst.stage == 1){ //기본 단계인 알을 1단계로 설정
                flupetRepository.findByStage(mypetRst.stage+1).toList()
            }else if(mypetRst.stage == 2){
                flupetRepository.findById(mypetRst.flupetId+1)
            }else{
                throw CustomBadRequestException(ErrorType.NOT_AVAILABLE_EVOLVE)
            }
            //flupetRepository.findByStage(mypetRst.stage).toList()
        }

        //진화하기전의 기존의 펫을 죽었다고 처리
        //mflupet.await()이 null이면 예외 처리
        val mflupetRst = mflupet.await() ?: throw CustomBadRequestException(ErrorType.INVALID_USERID)
        mflupetRst.isDead = true
        mflupetRst.endTime = LocalDateTime.now()
        launch(Dispatchers.IO) {
            memberFlupetRepository.save(mflupetRst)
        }

        var flist: List<Flupet> = listOf() //stage == 1일때 사용
        var evolveFlupet: Flupet? = null //stage == 2일때 사용
        var anyrst = flupets.await()
        if(mypetRst.stage == 1){
            val tmp = anyrst as List<Flupet>
            flist = tmp.shuffled() //리스트(List<Flupet>)를 무작위로 썩은 값을 반환한다.
        }else{
            evolveFlupet = anyrst as Flupet?
        }
        //진화된 새로운 캐릭터(펫)을 지정해서 저장한다.
        launch(Dispatchers.IO) {
            memberFlupetRepository.save(
                MemberFlupet(
                    flupetId = if(mypetRst.stage == 1) flist[0].id else evolveFlupet!!.id,
                    memberId = userId,
                    name = mflupetRst.name,
                    exp = mflupetRst.exp,
                    steps = mflupetRst.steps,
                    createTime = mflupetRst.createTime,
                    fullness = mflupetRst.fullness,
                    health = mflupetRst.health,
                    patCnt = mflupetRst.patCnt,
                    fullnessUpdateTime = mflupetRst.fullnessUpdateTime,
                    healthUpdateTime = mflupetRst.healthUpdateTime
                )
            )
        }
        return@coroutineScope EvolveResponse(
            flupetName = mflupetRst.name,
            imageUrl = if(mypetRst.stage == 1) flist[0].imgUrl else evolveFlupet!!.imgUrl,
            fullness = mflupetRst.fullness,
            health = mflupetRst.health,
            isEvolutionAvailable = false,
            nextFullnessUpdateTime = mflupetRst.fullnessUpdateTime!!.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(),
            nextHealthUpdateTime = mflupetRst.healthUpdateTime!!.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
    }
}