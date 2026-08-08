package io.github.kei_1111

import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Project

internal fun Project.setupDetekt(
    extension: DetektExtension,
) {
    extension.apply {
        config.setFrom("$rootDir/config/detekt/detekt.yml")
        buildUponDefaultConfig = true
        source.setFrom(files("src"))
        // CI では整形結果が捨てられる（かつ並列コンパイルタスクとのレース源になる）ため無効化
        autoCorrect = providers.environmentVariable("CI").orNull == null
        parallel = true
    }
}
