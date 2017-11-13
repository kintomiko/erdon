package org.kin.erdon.mouth

fun main(args: Array<String>){

    val rawAudio = loadAudio("/Users/kindai/workspace/erdon/mouth/speech/sample.m4a")
    val cuts = parseCutsFromFile("/Users/kindai/workspace/erdon/mouth/speech/cuts.json")
    val fragments = VoiceSplitter.splitVoice(rawAudio, cuts)

    VoiceDao.save(fragments)
}

fun loadAudio(path: String): ByteArray = M4uReader.loadFile(path)

object M4uReader {
    fun loadFile(path: String): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

fun parseCutsFromFile(path: String): List<Cut> {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
}
