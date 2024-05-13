package com.chungchungdev

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.experimental.and
import kotlin.experimental.or

fun main() {
    val thirdInt = 0x12345cae
    val second = byteArrayOf(-117, 11, 0, 0, 0, 0, 0, 0)
    val secondText = "-117,11,0,0,0,0,0,0".split(",")
        .map { it.toByte() }
        .toByteArray()
        .contentToString().also { println(it) }
    val third = byteArrayOf(112, 92, 0, 0, 0, 0, 0, 0)
    val thirdText = "112,92,0,0,0,0,0,0".split(",")
        .map { it.toByte() }
        .toByteArray()
        .contentToString().also { println(it) }
    //val bytes
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

fun String.toOctetBytes() =
    this.split(",")
        .map { it.toByte() }
        .toByteArray()

fun ByteArray.intFromBigEndianOctetBytes(): Int {
    require(this.size == 8) { "Parsing wrong bytes! Your bytes have ${this.size} bytes, but 8 is required." }
    return this[0].toNonNegativeInt() or
            (this[1].toNonNegativeInt() shl 8) or
            (this[2].toNonNegativeInt() shl 16) or
            (this[3].toNonNegativeInt() shl 24)
}

fun Byte.toNonNegativeInt() = this.toUByte().toInt()