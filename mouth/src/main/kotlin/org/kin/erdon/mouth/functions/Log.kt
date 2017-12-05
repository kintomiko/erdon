package org.kin.erdon.mouth.functions

import java.time.ZonedDateTime

fun log(msg:String) {
    println("${ZonedDateTime.now()}: ${msg}")
}

fun logError(msg:String?) {
    msg?. let {
        println("ERROR!!! ${msg}")
    }
}
fun logError(e:Exception) {
    println("ERROR!!! ${e.message}")
    e.printStackTrace()
}