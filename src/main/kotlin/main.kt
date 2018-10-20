
import kotlinx.coroutines.experimental.*
import com.aleksclark.kform.*


fun main(args: Array<String>)= runBlocking<Unit>  {
    var fileNames = arrayOf("moo.csv", "bar.csv")
    var conf = Config(fileNames, 5, 10)
    launch {

        var kform = Kform(conf, this.coroutineContext)
        kform {
            var result = loadFile(fileNames.first())
            printFile(result)
            fileReader {
                readFrom("myFile.csv")
            }

            rawTransform {
                prefix = "b1r"
            }

            rawTransform {
                prefix = "foo"
            }

            fileWriter {
                writeTo("destFile.csv")
            }
            execute()

        }
    }


}

