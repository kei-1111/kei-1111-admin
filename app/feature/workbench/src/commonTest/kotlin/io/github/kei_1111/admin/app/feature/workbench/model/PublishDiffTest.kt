package io.github.kei_1111.admin.app.feature.workbench.model

import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentStatus
import io.github.kei_1111.admin.shared.model.PublishedSnapshot
import io.github.kei_1111.admin.shared.model.ReadmeBlock
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.ReadmeInline
import io.github.kei_1111.admin.shared.model.SocialLink
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.TerminalTextCommand
import io.github.kei_1111.admin.shared.model.Work
import io.github.kei_1111.admin.shared.model.WorksContent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private fun work(id: String, about: String = "", status: ContentStatus = ContentStatus.Published) =
    Work(id = id, name = id, about = about, status = status)

class PublishDiffTest {

    @Test
    fun classifiesWorkChangesByKind() {
        val diff = computePublishDiff(
            mergedWorks = listOf(
                work("unchanged"),
                work("changed", about = "after"),
                work("added"),
                work("excluded", status = ContentStatus.Draft),
            ),
            mergedProfile = AdminProfile(),
            mergedTerminal = TerminalCommandsContent(),
            mergedReadme = ReadmeContent(),
            published = PublishedSnapshot(
                works = WorksContent(
                    works = listOf(work("unchanged"), work("changed", about = "before"), work("removed")),
                ),
                profile = AdminProfile(),
                terminal = TerminalCommandsContent(),
                readme = ReadmeContent(),
            ),
        )

        assertFalse(diff.isFirstPublish)
        assertEquals(
            mapOf(
                "changed" to WorkChangeKind.Changed,
                "added" to WorkChangeKind.Added,
                "removed" to WorkChangeKind.Removed,
                "excluded" to WorkChangeKind.Excluded,
            ),
            diff.works.associate { it.name to it.kind },
        )
        assertTrue(diff.works.none { it.name == "unchanged" })
    }

    @Test
    fun reportsChangedProfileFieldsByName() {
        val published = AdminProfile(displayName = "けい", role = "Dev", socialLinks = emptyList())
        val diff = computePublishDiff(
            mergedWorks = emptyList(),
            mergedProfile = published.copy(
                displayName = "新しい名前",
                avatarUrl = "images/profile/1-a.png",
                socialLinks = listOf(SocialLink(service = "GitHub", url = "https://github.com/kei-1111")),
            ),
            mergedTerminal = TerminalCommandsContent(),
            mergedReadme = ReadmeContent(),
            published = PublishedSnapshot(
                works = WorksContent(),
                profile = published,
                terminal = TerminalCommandsContent(),
                readme = ReadmeContent(),
            ),
        )

        assertEquals(listOf("表示名", "アバター", "リンク"), diff.profileChangedFields)
    }

    @Test
    fun firstPublishIsFlaggedAndTreatsEverythingAsNew() {
        val diff = computePublishDiff(
            mergedWorks = listOf(work("a")),
            mergedProfile = AdminProfile(displayName = "けい"),
            mergedTerminal = TerminalCommandsContent(
                commands = listOf(TerminalTextCommand(keyword = "coffee")),
            ),
            mergedReadme = ReadmeContent(
                ja = listOf(ReadmeBlock.Paragraph(inlines = listOf(ReadmeInline.PlainText("本文")))),
            ),
            published = PublishedSnapshot(),
        )

        assertTrue(diff.isFirstPublish)
        assertEquals(WorkChangeKind.Added, diff.works.single().kind)
        assertEquals(listOf("coffee"), diff.terminalAdded)
        assertTrue(diff.readmeJaChanged)
        assertFalse(diff.readmeEnChanged)
    }

    @Test
    fun diffsTerminalCommandsByKeyword() {
        val diff = computePublishDiff(
            mergedWorks = emptyList(),
            mergedProfile = AdminProfile(),
            mergedTerminal = TerminalCommandsContent(
                commands = listOf(
                    TerminalTextCommand(keyword = "kept", lines = listOf("same")),
                    TerminalTextCommand(keyword = "edited", lines = listOf("after")),
                    TerminalTextCommand(keyword = "new"),
                ),
            ),
            mergedReadme = ReadmeContent(),
            published = PublishedSnapshot(
                works = WorksContent(),
                profile = AdminProfile(),
                terminal = TerminalCommandsContent(
                    commands = listOf(
                        TerminalTextCommand(keyword = "kept", lines = listOf("same")),
                        TerminalTextCommand(keyword = "edited", lines = listOf("before")),
                        TerminalTextCommand(keyword = "gone"),
                    ),
                ),
                readme = ReadmeContent(),
            ),
        )

        assertEquals(listOf("new"), diff.terminalAdded)
        assertEquals(listOf("edited"), diff.terminalChanged)
        assertEquals(listOf("gone"), diff.terminalRemoved)
    }
}
