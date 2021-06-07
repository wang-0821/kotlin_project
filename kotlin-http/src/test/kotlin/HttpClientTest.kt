import com.xiao.base.testing.KtTestBase
import com.xiao.rpc.Client
import com.xiao.rpc.util.UrlParser
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 *
 * @author lix wang
 */
class HttpClientTest : KtTestBase() {
    @Test
    fun `test get baidu`() {
        val response = Client().newCall(UrlParser.parseUrl("https://www.baidu.com")).execute()
        assertEquals(response.status, 200)
        assertTrue(response.asString()!!.isNotBlank())
    }
}