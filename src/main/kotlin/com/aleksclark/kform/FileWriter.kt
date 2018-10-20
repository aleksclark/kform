package com.aleksclark.kform

import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.delay

class FileWriter(
    override val statsChan: Channel<StatsData>,
    val controlChan: Channel<ControlData>,
    override val reportEvery: Int
): ReportableStage {
    override var rowsProcessed: Int = 0
    override var totalRowsProcessed: Int = 0
    override var lastReportTime: Long = System.currentTimeMillis()
    override var stageId: Int = 0
    override val stageName = "FileWriter"
    lateinit var srcChan: Channel<RowData>
    lateinit var destFile: String

    suspend fun write(): Unit {
        while (!srcChan.isClosedForReceive) {
            val row = srcChan.poll()
            if (row == null) {
                delay(100)
            } else {
                println("$stageId - Wrote line ${row?.raw}")
                reportProgress()
            }

        }
        reportDone()

    }

    fun writeTo(fileName: String): Unit {
        destFile = fileName
    }
}