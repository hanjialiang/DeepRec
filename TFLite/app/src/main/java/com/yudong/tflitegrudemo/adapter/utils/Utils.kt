package com.yudong.tflitegrudemo.adapter.utils

import kotlin.Throws
import java.io.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

object Utils {
    @Throws(IOException::class)
    fun giveMeByteBuffer(`is`: InputStream): ByteBuffer {
        val tmpOut = ByteArrayOutputStream()
        var count = 0
        val buf = ByteArray(1024 * 1024 * 4)
        while (true) {
            val len = `is`.read(buf)
            if (len == -1) {
                break
            }
            tmpOut.write(buf, 0, len)
            count += len
            System.out.printf("Read count: %d\n", count)
        }
        `is`.close()
        tmpOut.close() // No effect, but good to do anyway to keep the metaphor alive
        val array = tmpOut.toByteArray()
        val ret = ByteBuffer.allocateDirect(array.size)
        ret.order(ByteOrder.nativeOrder())
        ret.put(array)
        return ret
    }

    @Throws(IOException::class)
    fun read2DArray(`is`: InputStream?, N: Int, M: Int): Array<IntArray> {
        val ret = Array(N) { IntArray(M) }
        val br = BufferedReader(InputStreamReader(`is`))
        var line = br.readLine()
        var currentRow = 0
        var currentColumn = 0
        var currentValue = 0
        while (line != null) {
            for (x in line.toCharArray()) {
                if (x == ',') {
                    ret[currentRow][currentColumn] = currentValue
                    currentColumn++
                    currentValue = 0
                } else {
                    assert(x in '0'..'9')
                    currentValue = currentValue * 10 + (x - '0')
                }
            }
            if (currentColumn < M) {
                ret[currentRow][currentColumn] = currentValue
                currentColumn++
                currentValue = 0
            }
            assert(currentColumn == M)
            currentColumn = 0
            currentRow++
            line = br.readLine()
        }
        assert(currentRow == N)
        return ret
    }
}