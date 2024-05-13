import com.chungchungdev.toNonNegativeInt
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BytesExtTestTest : FunSpec({

    context("Byte.toNonNegativeInt() is correct") {

        test("test non negative") {
            for (i in 0..Byte.MAX_VALUE) {
                i.toByte().toNonNegativeInt() shouldBe i
            }
        }

        test("test negative") {
            for (i in Byte.MIN_VALUE..<0) {
                i.toByte().toNonNegativeInt() shouldBe (256 + i)
            }
        }
    }

    context("ByteArray.intFromBigEndianOctetBytes() is correct") {


    }
})