package com.chungchungdev

import java.nio.ByteBuffer
import java.nio.ByteOrder

fun main() {
    val third = 0x12345c70
    val firstByte = 0x000000ff and third
    val secondByte = (third shr 8) and 0x0000ff
    val thirdByte = (third shr 16) and 0x00ff
    val fourthByte = (third shr 24) and 0xff
    /*println(firstByte.toHexString())
    println(secondByte.toHexString())
    println(thirdByte.toHexString())
    println(fourthByte.toHexString())*/
    val a = third.toBigEndianOctetBytes()
    println(a.contentToString())
}

fun Int.toBigEndianOctetBytes(): ByteArray {
    val firstByte = 0x000000ff and this
    val secondByte = (this shr 8) and 0x0000ff
    val thirdByte = (this shr 16) and 0x00ff
    val fourthByte = (this shr 24) and 0xff
    return byteArrayOf(
        firstByte.toByte(),
        secondByte.toByte(),
        thirdByte.toByte(),
        fourthByte.toByte(),
        0x00,
        0x00,
        0x00,
        0x00
    )
}