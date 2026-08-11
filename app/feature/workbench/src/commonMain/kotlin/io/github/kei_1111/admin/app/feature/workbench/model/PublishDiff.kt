package io.github.kei_1111.admin.app.feature.workbench.model

import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentStatus
import io.github.kei_1111.admin.shared.model.PublishedSnapshot
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.Work

/** 公開確認ダイアログに出す「公開すると何が変わるか」。項目単位(行テキストの diff はしない)。 */
internal data class PublishDiff(
    /** published/ に何も無い初回公開。 */
    val isFirstPublish: Boolean,
    val works: List<WorkChange>,
    /** profile で変わるフィールドの表示名。空なら profile は変更なし。 */
    val profileChangedFields: List<String>,
    val readmeJaChanged: Boolean,
    val readmeEnChanged: Boolean,
    val terminalAdded: List<String>,
    val terminalChanged: List<String>,
    val terminalRemoved: List<String>,
) {
    val hasChanges: Boolean
        get() = works.any { it.kind != WorkChangeKind.Excluded } ||
            profileChangedFields.isNotEmpty() ||
            readmeJaChanged || readmeEnChanged ||
            terminalAdded.isNotEmpty() || terminalChanged.isNotEmpty() || terminalRemoved.isNotEmpty()
}

internal data class WorkChange(val name: String, val kind: WorkChangeKind)

internal enum class WorkChangeKind { Added, Changed, Removed, Excluded }

/**
 * 「これから公開される内容」(下書き+編集バッファ、works は status=公開のみ)と
 * published/ の現在内容を項目単位で比較する。
 */
internal fun computePublishDiff(
    mergedWorks: List<Work>,
    mergedProfile: AdminProfile,
    mergedTerminal: TerminalCommandsContent,
    mergedReadme: ReadmeContent,
    published: PublishedSnapshot,
): PublishDiff {
    val isFirstPublish =
        published.works == null && published.profile == null &&
            published.terminal == null && published.readme == null
    return PublishDiff(
        isFirstPublish = isFirstPublish,
        works = workChanges(mergedWorks, published),
        profileChangedFields = profileChangedFields(mergedProfile, published.profile),
        readmeJaChanged = mergedReadme.ja != published.readme?.ja.orEmpty(),
        readmeEnChanged = mergedReadme.en != published.readme?.en.orEmpty(),
        terminalAdded = terminalKeywords(mergedTerminal) - terminalKeywords(published.terminal),
        terminalChanged = changedTerminalKeywords(mergedTerminal, published.terminal),
        terminalRemoved = terminalKeywords(published.terminal) - terminalKeywords(mergedTerminal),
    )
}

private fun workChanges(mergedWorks: List<Work>, published: PublishedSnapshot): List<WorkChange> {
    val willPublish = mergedWorks.filter { it.status == ContentStatus.Published }
    val publishedWorks = published.works?.works.orEmpty().associateBy { it.id }
    return buildList {
        willPublish.forEach { work ->
            val before = publishedWorks[work.id]
            when {
                before == null -> add(WorkChange(work.name, WorkChangeKind.Added))
                before != work -> add(WorkChange(work.name, WorkChangeKind.Changed))
            }
        }
        val willPublishIds = willPublish.map { it.id }.toSet()
        publishedWorks.values
            .filter { it.id !in willPublishIds }
            .forEach { add(WorkChange(it.name, WorkChangeKind.Removed)) }
        mergedWorks
            .filter { it.status == ContentStatus.Draft }
            .forEach { add(WorkChange(it.name, WorkChangeKind.Excluded)) }
    }
}

private fun profileChangedFields(merged: AdminProfile, published: AdminProfile?): List<String> {
    val base = published ?: AdminProfile()
    return buildList {
        if (merged.displayName != base.displayName || merged.displayNameEn != base.displayNameEn) add("表示名")
        if (merged.role != base.role) add("ロール")
        if (merged.location != base.location) add("ロケーション")
        if (merged.avatarUrl != base.avatarUrl) add("アバター")
        if (merged.socialLinks != base.socialLinks || merged.xUrl != base.xUrl) add("リンク")
        if (merged.pinnedRepos != base.pinnedRepos) add("PINNED")
    }
}

private fun terminalKeywords(content: TerminalCommandsContent?): List<String> =
    content?.commands?.map { it.keyword }.orEmpty()

private fun changedTerminalKeywords(
    merged: TerminalCommandsContent,
    published: TerminalCommandsContent?,
): List<String> {
    val before = published?.commands.orEmpty().associateBy { it.keyword }
    return merged.commands
        .filter { command -> before[command.keyword]?.let { it != command } == true }
        .map { it.keyword }
}
