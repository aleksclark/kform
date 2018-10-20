package com.aleksclark.kform

class Config(var fileNames: Array<String>,val chanCap: Int, val reportEvery: Int) {

    init {
        println("Config init")
        println(this.fileNames.joinToString(", "))
    }
}