import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.assertj.core.api.Assertions.assertThat
import org.grpcmock.GrpcMock
import org.grpcmock.GrpcMock.stubFor
import org.grpcmock.definitions.response.Delay
import org.grpcmock.junit5.GrpcMockExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import ru.orlovich.proto.SumHolderServiceGrpc
import ru.orlovich.proto.SumHolderServiceOuterClass
import ru.orlovich.service.impl.SumHolderServiceImpl.MAX_SLOW_RESPONSE_MILLISECONDS
import ru.orlovich.service.impl.SumHolderServiceImpl.MIN_SLOW_RESPONSE_MILLISECONDS


/**
 * * Created by aorlovich on 27.07.2021.
 */
@ExtendWith(GrpcMockExtension::class)
class AllInOneSumHolderOnMockTests {

    private var channel: ManagedChannel? = null

    @BeforeEach
    fun setupChannel() {
        channel = ManagedChannelBuilder.forAddress("localhost", GrpcMock.getGlobalPort())
            .usePlaintext()
            .build()
    }

    @ParameterizedTest
    @MethodSource("TestData#testData")
    fun flowTest(
        request: SumHolderServiceOuterClass.SumSaveRequest,
        response: SumHolderServiceOuterClass.SumSaveResponse
    ) {
        //create mock stub manual
        createStubForTest(request, response)
        val serviceStub: SumHolderServiceGrpc.SumHolderServiceBlockingStub =
            SumHolderServiceGrpc.newBlockingStub(channel)

        //action
        val timeBefore = System.currentTimeMillis()
        val saveResponse = serviceStub.save(request)
        val timeAfter = System.currentTimeMillis()

        //asserts
        // у объектов реализованы equals и hashcode, можно и так
        assertThat(saveResponse).isEqualTo(response)
        if (request.slow) assertThat(timeAfter - timeBefore).isGreaterThan(MIN_SLOW_RESPONSE_MILLISECONDS.toLong())
    }

    //decomposition
    private fun createStubForTest(
        request: SumHolderServiceOuterClass.SumSaveRequest,
        response: SumHolderServiceOuterClass.SumSaveResponse
    ) {
        stubFor(
            GrpcMock.unaryMethod(SumHolderServiceGrpc.getSaveMethod())
                .withRequest(request)
                .willReturn(
                    GrpcMock.response(response)
                        .apply {
                            if (request.slow) withDelay(
                                Delay.randomDelay(
                                    MIN_SLOW_RESPONSE_MILLISECONDS.toLong(),
                                    MAX_SLOW_RESPONSE_MILLISECONDS.toLong()
                                )
                            )
                        }

                )
        )
    }

    @AfterEach
    fun shutdownChannel() {
        channel?.shutdownNow()
    }

    // так как апи спроектировано отдавать всегда два значения в ответе, не зависимо от входных данных
    // для уменьшения кода можно использовать data providers
    // да, хотелось бы разбить хотя бы на позитивки и негативки, но это тестовое задание,
    // в такой уровень методик опускаться не будем

}