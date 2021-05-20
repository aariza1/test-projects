package extensions

import mu.KLogger
import mu.KotlinLogging
import net.logstash.logback.argument.StructuredArguments.kv
import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.Options
import org.openjdk.jmh.runner.options.OptionsBuilder
import java.util.concurrent.TimeUnit
import org.openjdk.jmh.annotations.State
import java.util.*

private val logger = KotlinLogging.logger {}

@BenchmarkMode(Mode.AverageTime) // Benchmark mode
@Warmup(iterations = 3) // Preheating times
@Measurement(iterations = 50, time = 5, timeUnit = TimeUnit.NANOSECONDS)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Threads(8) // number of test threads per process
@Fork(2)
open class ExtensionBenchmark {

    @State(Scope.Benchmark)
    open class ThreadState {
        val uuid1 = "179155ce-c7d5-41b2-bbf9-367ee9458c30"
        val uuid2 = "84159ceb-f5b5-4e47-ae6a-a86fefa6fe14"
        val uuid3 = "08964c8f-3e23-4097-af09-496a33c9a08c"
        val uuid4 = "1e1f22b9-e0b9-4b8e-b033-d46223027e5e"
        val uuid5 = "5dee00d6-c879-484b-a16c-2671f2a2882f"
    }

    @State(Scope.Benchmark)
    open class ThreadStateRandom {
        val uuid1 = UUID.randomUUID()
        val uuid2 = UUID.randomUUID()
        val uuid3 = UUID.randomUUID()
        val uuid4 = UUID.randomUUID()
        val uuid5 = UUID.randomUUID()
    }

    private fun KLogger.infoWithSingleSupplier(
        error: Throwable,
        arguments: () -> Array<Any>,
        message: () -> String,
    ) {
        if (isInfoEnabled) {
            info(
                message.invoke(),
                *arguments.invoke() + error,
            )
        }
    }

    private inline fun KLogger.infoInlinedWithSingleSupplier(
        error: Throwable,
        arguments: () -> Array<Any>,
        message: () -> String,
    ) {
        if (isInfoEnabled) {
            info(
                message.invoke(),
                *arguments.invoke() + error,
            )
        }
    }

    private fun KLogger.infoWithVarargSupplier(
        error: Throwable,
        vararg arguments: () -> Any,
        message: () -> String,
    ) {
        if (isInfoEnabled) {
            info(
                message.invoke(),
                *arguments
                    .map { it.invoke() }
                    .toTypedArray() + error,
            )
        }
    }

    private inline fun KLogger.infoWithInlinedVarargSupplier(
        error: Throwable,
        vararg arguments: () -> Any,
        message: () -> String,
    ) {
        if (isInfoEnabled) {
            info(
                message.invoke(),
                *arguments
                    .map { it.invoke() }
                    .toTypedArray() + error,
            )
        }
    }

    @Benchmark
    fun testWithoutExtension(state: ThreadState) {

        if (logger.isInfoEnabled) {
            logger.info(
                "This is a test message",
                kv("uuid", state.uuid1),
                kv("uuid", state.uuid2),
                kv("uuid", state.uuid3),
                kv("uuid", state.uuid4),
                kv("uuid", state.uuid5),
                IllegalStateException(),
            )
        }

    }

    @Benchmark
    fun testWithVarargSupplier(state: ThreadState) {

        logger.infoWithVarargSupplier(
            IllegalStateException(),
            { kv("uuid", state.uuid1) },
            { kv("uuid", state.uuid2) },
            { kv("uuid", state.uuid3) },
            { kv("uuid", state.uuid4) },
            { kv("uuid", state.uuid5) },
        )
        { "This is the test message from function with extension" }
    }

    @Benchmark
    fun testWithSingleSupplier(state: ThreadState) {
        logger.infoWithSingleSupplier(
            IllegalStateException(),
            {
                arrayOf(
                    kv("uuid", state.uuid1),
                    kv("uuid", state.uuid2),
                    kv("uuid", state.uuid3),
                    kv("uuid", state.uuid4),
                    kv("uuid", state.uuid5),
                )
            },
        )
        { "This is a message" }
    }

    @Benchmark
    fun testWithInlinedVarargSupplier(state: ThreadState) {

        logger.infoWithInlinedVarargSupplier(
            IllegalStateException(),
            { kv("uuid", state.uuid1) },
            { kv("uuid", state.uuid2) },
            { kv("uuid", state.uuid3) },
            { kv("uuid", state.uuid4) },
            { kv("uuid", state.uuid5) },
        )
        { "This is the test message from function with extension" }
    }

    @Benchmark
    fun testInlinedWithSingleSupplier(state: ThreadState) {
        logger.infoInlinedWithSingleSupplier(
            IllegalStateException(),
            {
                arrayOf(
                    kv("uuid", state.uuid1),
                    kv("uuid", state.uuid2),
                    kv("uuid", state.uuid3),
                    kv("uuid", state.uuid4),
                    kv("uuid", state.uuid5),
                )
            },
        )
        { "This is a message" }
    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {

            val options: Options = OptionsBuilder()
                .include(".*" + ExtensionBenchmark::class.java.simpleName + ".*")
                .output("benchmark_test.log")
                .build()

            Runner(options).run()
        }
    }

}