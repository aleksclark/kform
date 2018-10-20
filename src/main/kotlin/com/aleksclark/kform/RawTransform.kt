package com.aleksclark.kform

import kotlinx.coroutines.experimental.channels.*
import kotlin.coroutines.experimental.CoroutineContext

class RawTransform(
    override val srcChan: Channel<RowData>,
    override val destChan: Channel<RowData>,
    override val statsChan: Channel<StatsData>,
    override val reportEvery: Int,
    override val coroutineContext: CoroutineContext
): ProcessingStage {
    override var stageId: Int = 0
    override var rowsProcessed: Int = 0
    override var totalRowsProcessed: Int = 0
    override var lastReportTime: Long = System.currentTimeMillis()
    override val stageName = "RawTransform"
    lateinit var prefix: String


    override suspend fun process(data: RowData): RowData {
        data.raw = prefix + data.raw
        return data
    }
}