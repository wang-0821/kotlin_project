package xiao.base.io

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import xiao.base.testing.KtTestBase

/**
 *
 * @author lix wang
 */
class UnsafeChunkedIntArrayTest : KtTestBase() {
    @Test
    fun `test unsafeChunkedIntArray to array`() {
        UnsafeChunkedDirectIntArray(10, 5)
            .use { unsafeChunkedIntArray ->
                (0..20).forEach {
                    if (unsafeChunkedIntArray.isWriteable()) {
                        unsafeChunkedIntArray.add(it)
                    }
                }
                val array = unsafeChunkedIntArray.toArray()
                Assertions.assertEquals(array.size, 10)
                Assertions.assertEquals(array[0], 0)
                Assertions.assertEquals(array[9], 9)
            }
    }

    @Test
    fun `test unsafeChunkedIntArray to list`() {
        UnsafeChunkedDirectIntArray(10, 5)
            .use { unsafeChunkedIntArray ->
                (0..20).forEach {
                    if (unsafeChunkedIntArray.isWriteable()) {
                        unsafeChunkedIntArray.add(it)
                    }
                }

                val list = unsafeChunkedIntArray.toList()
                Assertions.assertEquals(list.size, 10)
                Assertions.assertEquals(list[0], 0)
                Assertions.assertEquals(list[9], 9)
            }
    }
}