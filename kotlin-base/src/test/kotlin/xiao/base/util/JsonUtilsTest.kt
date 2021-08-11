package xiao.base.util

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import xiao.base.testing.KtTestBase

class JsonUtilsTest : KtTestBase() {
    @Test
    fun `test jackson generic convert`() {
        val obj = SerializeObj()
            .apply {
                list = listOf(1, 2, 3)
                map = mapOf("var1" to 1, "var2" to 2)
                value = 3
            }
        val content = JsonUtils.serialize(obj)
        val deserializeObj = JsonUtils.deserialize(content, SerializeObj::class.java)
        Assertions.assertEquals(deserializeObj.list, obj.list)
        Assertions.assertEquals(deserializeObj.map, obj.map)
        Assertions.assertEquals(deserializeObj.value, obj.value)
    }

    @Test
    fun `test jackson deserialize list`() {
        val list = listOf(1, 2, 3)
        val content = JsonUtils.serialize(list)
        val deserializeList = JsonUtils.deserializeList(content, Int::class.java)
        Assertions.assertEquals(deserializeList, list)
    }

    @Test
    fun `test jackson deserialize map`() {
        val map = mapOf("var1" to 1, "var2" to 2)
        val content = JsonUtils.serialize(map)
        val deserializeMap = JsonUtils.deserializeMap(content, String::class.java, Int::class.java)
        Assertions.assertEquals(deserializeMap, map)
    }

    class SerializeObj {
        var map: Map<Any, Any>? = null
        var list: List<Any>? = null
        var value: Int = 0
    }
}