package io.github.kei_1111.admin.app.core.common.dispatcher

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@BindingContainer
@ContributesTo(AppScope::class)
interface DispatcherBindings {

    companion object {
        @DefaultDispatcher
        @Provides
        @SingleIn(AppScope::class)
        fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
    }
}
