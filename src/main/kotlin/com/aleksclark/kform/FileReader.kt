package com.aleksclark.kform

import kotlinx.coroutines.experimental.channels.Channel

class FileReader(
    override val statsChan: Channel<StatsData>,
    val controlChan: Channel<ControlData>,
    override val reportEvery: Int
): ReportableStage {
    override var rowsProcessed: Int = 0
    override var totalRowsProcessed: Int = 0
    override var lastReportTime: Long = System.currentTimeMillis()
    override var stageId: Int = 0
    override val stageName = "FileReader"
    lateinit var destChan: Channel<RowData>
    lateinit var srcFileName: String

    suspend fun read(): Unit {
        reportStart()
        val headers: String = "i,j"
        for (i in 1..10) {
            for (j in 1..10) {
                val row = RowData("$i,$j", headers)
                destChan.send(row)
                reportProgress()
            }

        }
        reportDone()
        destChan.close()
    }

    fun readFrom(fileName: String): Unit {
        this.srcFileName = fileName
    }
}