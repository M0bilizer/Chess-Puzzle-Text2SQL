import com.chess.puzzle.text2sql.web.domain.model.ModelName
import com.chess.puzzle.text2sql.web.service.llm.Deepseek
import com.chess.puzzle.text2sql.web.service.llm.LargeLanguageModelFactory
import com.chess.puzzle.text2sql.web.service.llm.Mistral
import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class LargeLanguageModelFactoryTest {

    private val deepSeek: Deepseek = mockk()
    private val mistral: Mistral = mockk()
    private val largeLanguageModelFactory = LargeLanguageModelFactory(deepSeek, mistral)

    @Test
    fun `should return Deepseek model when ModelName is Deepseek`() {
        val model = largeLanguageModelFactory.getModel(ModelName.Deepseek)

        expectThat(model).isEqualTo(deepSeek)
    }

    @Test
    fun `should return Mistral model when ModelName is Mistral`() {
        val model = largeLanguageModelFactory.getModel(ModelName.Mistral)

        expectThat(model).isEqualTo(mistral)
    }
}
