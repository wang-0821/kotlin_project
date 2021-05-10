import com.xiao.rpc.Client
import com.xiao.rpc.util.UrlParser
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 *
 * @author lix wang
 */
class HttpClientTest {
    @Test
    fun `test get baidu`() {
        val response = Client().newCall(UrlParser.parseUrl("https://www.baidu.com")).execute()
        assertEquals(response.status, 200)
        val responseString = response.asString()
        println(responseString)
        Assertions.assertTrue(responseString!!.startsWith("<html>"))
        Assertions.assertTrue(responseString.endsWith("</html>"))
    }
}