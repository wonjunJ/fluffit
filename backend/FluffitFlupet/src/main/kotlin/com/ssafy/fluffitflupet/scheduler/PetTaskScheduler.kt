package com.ssafy.fluffitflupet.scheduler

import com.ssafy.fluffitflupet.entity.MemberFlupet
import com.ssafy.fluffitflupet.repository.FoodRepository
import com.ssafy.fluffitflupet.repository.MemberFlupetRepository
import com.ssafy.fluffitflupet.service.FlupetService
import jakarta.annotation.PreDestroy
import jakarta.annotation.Priority
import kotlinx.coroutines.*
import kotlinx.coroutines.reactor.awaitSingle
import org.slf4j.LoggerFactory
import org.springframework.core.env.Environment
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.BufferOverflowStrategy
import reactor.core.scheduler.Schedulers
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.coroutines.CoroutineContext

@Component
class PetTaskScheduler(
    private val memberFlupetRepository: MemberFlupetRepository,
    private val env: Environment,
    private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>,
    private val achaCalculator: AchaCalculator,
    private val foodRepository: FoodRepository
): CoroutineScope { //CoroutineScope를 컴포넌트 레벨에서 구현하여 각 스케쥴된 작업이 자신의 CoroutineScope를 가지게 된다.
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default + job
    private val log = LoggerFactory.getLogger(FlupetService::class.java)
    var dropList = ArrayList<MemberFlupet>()
    var dropPat = ArrayList<MemberFlupet>()
    //var cron: String = env.getProperty("schedule.cron", "0 1 0 * * ?")

    //2시간마다 스케쥴링을 돈다(초기 딜레이를 얼마로 할지)
    @Scheduled(fixedDelay = 1000 * 60 * 10L, initialDelay = 1000 * 60 * 2L)
    fun run() {
        log.info("스케쥴링")
        memberFlupetRepository.findAllByIsDeadIsFalse()
            .onBackpressureBuffer(256,
                {dropped -> dropList.add(dropped)},
                BufferOverflowStrategy.DROP_LATEST)
            .publishOn(Schedulers.parallel(), false, 32)
            .subscribe(this::handleData)
            { error -> log.error(error.toString()) }

        for( memberFlupet in dropList ) {
            val job1 = launch { updateFullness(memberFlupet) }
            val job2 = launch { updateHealth(memberFlupet) }
            launch {
                joinAll(job1, job2)
                if(memberFlupet.fullness <= 0 || memberFlupet.health <= 0) {
                    memberFlupet.isDead = true
                    memberFlupet.endTime = LocalDateTime.now()
                }

                memberFlupet.achaTime = achaCalculator.calAchaTime(memberFlupet.fullness, memberFlupet.health) ?: memberFlupet.achaTime
                withContext(Dispatchers.IO){
                    memberFlupetRepository.save(memberFlupet)
                }
            }
        }
    }

    fun handleData(data: MemberFlupet){
        log.info("handleData")
        //포만감 업데이트
        val job1 = launch { updateFullness(data) }
        //건강 업데이트
        val job2 = launch { updateHealth(data) }
        launch {
            joinAll(job1, job2)
            if(data.fullness <= 0 || data.health <= 0){
                data.isDead = true
                data.endTime = LocalDateTime.now()
            }

            withContext(Dispatchers.Default) {
                data.achaTime = achaCalculator.calAchaTime(data.fullness, data.health) ?: data.achaTime
            }
            log.info("스케쥴링에서의 현재 포만감은 ${data.fullness}")
            withContext(Dispatchers.IO){
                //코루틴을 사용할때 Mono로 리턴이 되는거에는 뒤에 .awaitSingle()을 붙혀줘야 작동이 된다.
                memberFlupetRepository.save(data).awaitSingle()
            }
        }
    }

    suspend fun updateFullness(data: MemberFlupet) {
        var hoursDiff = ChronoUnit.HOURS.between(data.fullnessUpdateTime, LocalDateTime.now())
        var tmp = 0
        var fullness = data.fullness
//        for (hour in 1..hoursDiff step 1) {
//            val checkTime = data.fullnessUpdateTime?.plusHours(hour)
//            if (checkTime?.hour in 0..7) {
//                if ((checkTime?.hour?.minus(tmp) ?: 0) >= 2) {
//                    fullness -= 5
//                    tmp += 2
//                }
//            } else {
//                fullness -= 5
//            }
//        }
        fullness -= 3
        data.fullness = fullness
        data.fullnessUpdateTime = LocalDateTime.now()
    }

    suspend fun updateHealth(data: MemberFlupet) {
        var hoursDiff = ChronoUnit.HOURS.between(data.healthUpdateTime, LocalDateTime.now())
        var tmp = 0
        var health = data.health
        for (hour in 1..hoursDiff step 1) {
            val checkTime = data.healthUpdateTime?.plusHours(hour)
            if (checkTime?.hour in 0..7) {
                if ((checkTime?.hour?.minus(tmp) ?: 0) >= 2) {
                    health -= 3
                    tmp += 2
                }
            } else {
                health -= 3
            }
        }
        data.health = health
        data.healthUpdateTime = LocalDateTime.now()
    }

    @Scheduled(cron = "0 1 0 * * ?")
    fun initializePatRun() { //매일 쓰다듬기 횟수를 초기화 한다.
        log.info("스케쥴링")
        memberFlupetRepository.findAllByIsDeadIsFalse()
            .onBackpressureBuffer(256,
                {dropped -> dropPat.add(dropped)},
                BufferOverflowStrategy.DROP_LATEST)
            .publishOn(Schedulers.parallel(), false, 32)
            .subscribe(this::initPat)
            { error -> log.error(error.toString()) }

        for(mf in dropPat){
            mf.patCnt = 5
            launch(Dispatchers.IO) { memberFlupetRepository.save(mf).awaitSingle() }
        }

        launch(Dispatchers.IO) { initStock() }
    }

    suspend fun initStock(){
        val foods = foodRepository.findAll()
        foods.collect{value ->
            if(value.id == 1L){ //기본
                value.stock = 70
            }else if(value.id == 2L){ //인스턴스
                value.stock = 100
            }else{ //고급
                value.stock = 30
            }
            withContext(Dispatchers.IO) { foodRepository.save(value) }
        }
    }

    fun initPat(data: MemberFlupet){
        data.patCnt = 5
        launch(Dispatchers.IO) { reactiveRedisTemplate.opsForValue().delete("patTime: ${data.memberId}").awaitSingle() }
        launch(Dispatchers.IO) { memberFlupetRepository.save(data).awaitSingle() }
    }

    @PreDestroy
    fun destroy() {
        job.cancel()  // 모든 코루틴을 취소
        println("CoroutineScheduledTasks 빈 파괴 시 모든 코루틴 취소")
    }
}