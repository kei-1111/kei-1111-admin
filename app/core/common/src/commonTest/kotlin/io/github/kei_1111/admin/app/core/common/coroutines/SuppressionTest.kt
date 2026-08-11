@file:OptIn(ExperimentalCoroutinesApi::class)

package io.github.kei_1111.admin.app.core.common.coroutines

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class SuppressionTest {

    @Test
    fun returnsBlockValueWhenBlockSucceeds() = runTest {
        val result = recoverOrElse({ "value" }) { "fallback" }

        assertEquals("value", result)
    }

    @Test
    fun returnsFallbackValueWhenBlockThrows() = runTest {
        val result = recoverOrElse<String>({ error("boom") }) { "fallback" }

        assertEquals("fallback", result)
    }

    @Test
    fun passesThrownExceptionToOnFailure() = runTest {
        val thrown = IllegalStateException("boom")

        val received = recoverOrElse<Exception?>({ throw thrown }) { it }

        assertSame(thrown, received)
    }

    @Test
    fun propagatesCancellationWithoutInvokingOnFailure() = runTest {
        var recovered = false
        val job = launch {
            recoverOrElse({ awaitCancellation() }) { recovered = true }
        }
        runCurrent()

        job.cancelAndJoin()

        assertTrue(job.isCancelled)
        assertFalse(recovered)
    }

    @Test
    fun propagatesNonExceptionThrowableWithoutInvokingOnFailure() = runTest {
        var recovered = false

        assertFailsWith<AssertionError> {
            recoverOrElse({ throw AssertionError("boom") }) { recovered = true }
        }

        assertFalse(recovered)
    }

    @Test
    fun runBestEffortSwallowsFailure() = runTest {
        runBestEffort { error("boom") }
    }

    @Test
    fun runBestEffortPropagatesCancellation() = runTest {
        var completedAfterCancellation = false
        val job = launch {
            runBestEffort { awaitCancellation() }
            completedAfterCancellation = true
        }
        runCurrent()

        job.cancelAndJoin()

        assertTrue(job.isCancelled)
        assertFalse(completedAfterCancellation)
    }
}
