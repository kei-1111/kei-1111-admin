package io.github.kei_1111.admin.app.core.common.result

import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class ResultTest {

    @Test
    fun asResultPrependsLoadingAndMapsEmissionsToSuccess() = runTest {
        val actual = flowOf("first", "second").asResult().toList()

        assertEquals(
            listOf(Result.Loading, Result.Success("first"), Result.Success("second")),
            actual,
        )
    }

    @Test
    fun asResultEmitsLoadingForAnEmptyFlow() = runTest {
        val actual = emptyFlow<String>().asResult().toList()

        assertEquals(listOf(Result.Loading), actual)
    }

    @Test
    fun asResultEndsInErrorCarryingTheThrownException() = runTest {
        val thrown = IllegalStateException("boom")
        val actual = flow {
            emit("before")
            throw thrown
        }.asResult().toList()

        assertEquals(Result.Loading, actual[0])
        assertEquals(Result.Success("before"), actual[1])
        assertSame(thrown, assertIs<Result.Error>(actual[2]).exception)
    }
}
