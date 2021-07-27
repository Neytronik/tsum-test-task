import io.grpc.ManagedChannel
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import io.grpc.testing.GrpcCleanupRule
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.TestInfo
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import ru.orlovich.proto.SumHolderServiceGrpc
import ru.orlovich.proto.SumHolderServiceOuterClass
import ru.orlovich.service.impl.SumHolderServiceImpl
import ru.orlovich.service.impl.SumHolderServiceImpl.MIN_SLOW_RESPONSE_MILLISECONDS


/**
 * * Created by aorlovich on 27.07.2021.
 */
class AllInOneSumHolderTests {

    private var channel: ManagedChannel? = null

    @BeforeEach
    fun setupChannel(testInfo: TestInfo) {
        val grpcCleanup = GrpcCleanupRule()
        grpcCleanup.register(
            InProcessServerBuilder.forName(testInfo.displayName)
                .directExecutor().addService(SumHolderServiceImpl()).build().start()
        )
        channel =
            grpcCleanup.register(InProcessChannelBuilder.forName(testInfo.displayName).directExecutor().build())
    }

    @ParameterizedTest
    @MethodSource("TestData#testData")
    fun flowTest(
        request: SumHolderServiceOuterClass.SumSaveRequest,
        response: SumHolderServiceOuterClass.SumSaveResponse
    ) {
        //create mock stub manual
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

    @AfterEach
    fun shutdownChannel() {
        channel?.shutdownNow()
    }

    // так как апи спроектировано отдавать всегда два значения в ответе, не зависимо от входных данных
    // для уменьшения кода можно использовать data providers
    // да, хотелось бы разбить хотя бы на позитивки и негативки, но это тестовое задание,
    // в такой уровень методик опускаться не будем

}