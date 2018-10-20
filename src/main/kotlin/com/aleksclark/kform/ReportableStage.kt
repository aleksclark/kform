package com.aleksclark.kform

import kotlinx.coroutines.experimental.channels.Channel

interface ReportableStage {
    val statsChan: Channel<StatsData>
    var stageId: Int
    var rowsProcessed: Int
    var totalRowsProcessed: Int
    var lastReportTime: Long
    val reportEvery: Int
    val stageName: String

    suspend fun reportProgress(): Unit {
        rowsProcessed += 1
        if (rowsProcessed >= reportEvery) {
            val currentTime = System.currentTimeMillis()
            val rowsPerSecond = rowsProcessed / ((currentTime  - lastReportTime) / 1000.00)
            totalRowsProcessed += rowsProcessed
            rowsProcessed = 0
            val stats = StatsData("Stage ${fullName()} Processed $totalRowsProcessed at $rowsPerSecond rows/second")
            lastReportTime = currentTime
            statsChan.send(stats)
        }


    }

    suspend fun reportDone(): Unit {
        statsChan.send(StatsData("Stage ${fullName()} is done"))
    }

    suspend fun reportStart(): Unit {
        statsChan.send(StatsData("Stage ${fullName()} has started."))
    }

    fun fullName(): String {
       return "$stageId-$stageName"
    }

}