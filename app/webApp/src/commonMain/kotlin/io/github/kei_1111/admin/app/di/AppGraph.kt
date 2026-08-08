package io.github.kei_1111.admin.app.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metrox.viewmodel.ViewModelGraph
import io.github.kei_1111.admin.app.core.common.logging.InteractionLog
import kotlinx.serialization.modules.SerializersModule

@DependencyGraph(scope = AppScope::class)
interface AppGraph : ViewModelGraph {
    val interactionLog: InteractionLog

    /** 各 feature が @IntoSet で提供する NavKey 直列化断片（AppNavDisplay が統合する）。 */
    val navKeySerializers: Set<SerializersModule>
}
