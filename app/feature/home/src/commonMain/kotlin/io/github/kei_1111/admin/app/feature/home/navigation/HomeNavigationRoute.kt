@file:Suppress("MatchingDeclarationName", "Filename")

package io.github.kei_1111.admin.app.feature.home.navigation

import androidx.navigation3.runtime.NavKey
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.BindingContainer
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.IntoSet
import dev.zacsweers.metro.Provides
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
data object Home : NavKey

// wasmJs はリフレクション非対応のため、この file の NavKey をバックスタック直列化へ登録する断片を
// Metro が Set として集約する（AppGraph.navKeySerializers）。NavKey を増やしたらここにも追加する。
@BindingContainer
@ContributesTo(AppScope::class)
interface HomeNavKeyBindings {

    companion object {
        @Provides
        @IntoSet
        fun provideHomeNavKeySerializers(): SerializersModule = SerializersModule {
            polymorphic(NavKey::class) {
                subclass(Home::class, Home.serializer())
            }
        }
    }
}
