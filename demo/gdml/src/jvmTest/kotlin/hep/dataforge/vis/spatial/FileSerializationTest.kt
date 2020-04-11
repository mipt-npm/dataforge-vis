package hep.dataforge.vis.spatial

import hep.dataforge.names.asName
import org.junit.jupiter.api.Test
import kotlin.test.Ignore

class FileSerializationTest {
    @Test
    @Ignore
    fun testFileRead(){
        val text = this::class.java.getResourceAsStream("/cubes.json").readAllBytes().decodeToString()
        val visual = VisualGroup3D.parseJson(text)
        visual["composite_001".asName()]
    }
}