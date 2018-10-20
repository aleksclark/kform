package com.aleksclark.kform

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.*
import java.util.*
import kotlin.coroutines.experimental.CoroutineContext

data class RowData(
    var raw: String,
    var header: String,
    var fields: HashMap<String, String> = hashMapOf()
)

data class StatsData(
    val msg: String
)

data class ControlData(
    val msg: String
)

class Kform(val conf: Config, override val coroutineContext: CoroutineContext): CoroutineScope {
    val statsChan = Channel<StatsData>(conf.chanCap)
    val controlChan = Channel<ControlData>(conf.chanCap)
    val dataChan = Channel<RowData>(conf.chanCap)
    val statsWorker = StatsReporter(statsChan)
    val fileWriter = FileWriter(statsChan, controlChan, conf.reportEvery)
    val fileReader = FileReader(statsChan, controlChan, conf.reportEvery)
    var stages = mutableListOf<ProcessingStage>()
    var channels = mutableListOf<Channel<RowData>>()


    init {
        println("Hello from Kform")
        println(conf.fileNames.joinToString("-"))
    }

    operator fun invoke(body: Kform.() -> Unit) {
        body()
    }

    fun fileReader(init: FileReader.() -> Unit): FileReader {
        fileReader.stageId = 0
        fileReader.init()
        var chan = Channel<RowData>(conf.chanCap)
        channels.add(chan)
        fileReader.destChan = chan
        return fileReader
    }

    fun fileWriter(init: FileWriter.() -> Unit): FileWriter {
        fileWriter.stageId = 1 + stages.size
        fileWriter.srcChan = channels.last()
        fileWriter.init()
        return fileWriter
    }

    fun rawTransform(init: RawTransform.() -> Unit): RawTransform {
        var chan = Channel<RowData>(conf.chanCap)
        var transform = RawTransform(channels.last(), chan, statsChan, conf.reportEvery, coroutineContext)
        stages.add(transform)
        channels.add(chan)
        transform.init()
        return transform
    }

    fun execute(): Job = launch {

        println("Execute!")
        launch  { statsWorker.monitor()}
        launch {fileWriter.write()}
        stages.forEach {
            launch { it.start() }
        }
        launch {fileReader.read()}

        while (!dataChan.isClosedForReceive) {
            delay(100)
        }

        statsChan.close()

    }

    fun loadFile(fileName: String): String {
        println("pretending to load" + fileName)
        return "loaded_" + fileName;
    }

    fun printFile(fileName: String): Unit {
        println("printing: " + fileName)
    }
}













// Reader -> RowData (headers + string)
// RowData -> RawTransform -> Row Data
// RowData -> ToHash -> RowData (fields populated)
// RowData -> Map -> RowData
// RowData -> CSVFormat -> RowData(string is now updated)
// RowData -> Writer