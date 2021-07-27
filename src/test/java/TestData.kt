import org.junit.jupiter.params.provider.Arguments
import ru.orlovich.proto.SumHolderServiceOuterClass

/**
 * * Created by aorlovich on 27.07.2021.
 */
object TestData {

    @JvmStatic
    fun testData(): List<Arguments> {
        fun sumSaveRequest(slow: Boolean, sum: Int): SumHolderServiceOuterClass.SumSaveRequest? {
            return SumHolderServiceOuterClass.SumSaveRequest.newBuilder()
                .setSlow(slow)
                .setSum(sum)
                .build()
        }

        fun sumSaveResponse(code: Int, status: String): SumHolderServiceOuterClass.SumSaveResponse? {
            return SumHolderServiceOuterClass.SumSaveResponse.newBuilder()
                .setCode(code)
                .setStatus(status)
                .build()
        }
        //легче расширять, если тесты будут добавляться, меньше кода
        return listOf(
            Arguments.of(sumSaveRequest(true, 1001), sumSaveResponse(11, "Out of range")),
            Arguments.of(sumSaveRequest(false, 1000), sumSaveResponse(0, "Sum successfully saved")),
            Arguments.of(sumSaveRequest(false, 0), sumSaveResponse(10, "Zero sum operation")),
            Arguments.of(sumSaveRequest(true, -1), sumSaveResponse(9, "Negative sum operation")),
        )
    }
}