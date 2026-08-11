package io.github.kei_1111.admin.server

import io.github.kei_1111.admin.server.service.ContentService
import io.github.kei_1111.admin.server.service.PublishService
import io.github.kei_1111.admin.server.storage.ContentStorage
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentDocument
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.ContentStatus
import io.github.kei_1111.admin.shared.model.PublishedSnapshot
import io.github.kei_1111.admin.shared.model.ReadmeBlock
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.ReadmeInline
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.TerminalTextCommand
import io.github.kei_1111.admin.shared.model.Work
import io.github.kei_1111.admin.shared.model.WorksContent
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val PUBLISHED_AT = "2026-08-08T12:00:00Z"

private class PublishContentStorage : ContentStorage {
    val objects = mutableMapOf<String, String>()
    private val binaries = mutableMapOf<String, ByteArray>()

    override suspend fun read(path: String): String? = objects[path]

    override suspend fun write(path: String, content: String) {
        objects[path] = content
    }

    override suspend fun readBytes(path: String): ByteArray? = binaries[path]

    override suspend fun writeBytes(path: String, content: ByteArray, contentType: String) {
        binaries[path] = content
    }
}

private class PublishTestContext {
    val storage = PublishContentStorage()
    val contentService = ContentService(storage = storage)
    val publishService = PublishService(
        storage = storage,
        contentService = contentService,
        now = { PUBLISHED_AT },
    )
}

private fun terminalContent(keyword: String) = TerminalCommandsContent(
    commands = listOf(
        TerminalTextCommand(
            keyword = keyword,
            description = "$keyword description",
            lines = listOf("$keyword output"),
        ),
    ),
)

private fun readmeContent(text: String) = ReadmeContent(
    ja = listOf(ReadmeBlock.Paragraph(inlines = listOf(ReadmeInline.PlainText(text)))),
    en = listOf(ReadmeBlock.Paragraph(inlines = listOf(ReadmeInline.PlainText("$text en")))),
)

class PublishServiceTest {

    @Test
    fun publishCopiesPublishedWorksAndOtherDraftsVerbatim() = runTest {
        val context = PublishTestContext()
        val publishedWork = Work(id = "published", name = "Published", status = ContentStatus.Published)
        val draftWork = Work(id = "draft", name = "Draft", status = ContentStatus.Draft)
        val profile = AdminProfile(displayName = "Admin", role = "Engineer")
        val terminal = terminalContent("hello")
        val readme = readmeContent("Published readme")
        context.contentService.saveWorksDraft(WorksContent(works = listOf(publishedWork, draftWork)))
        context.contentService.saveProfileDraft(profile)
        context.contentService.saveTerminalDraft(terminal)
        context.contentService.saveReadmeDraft(readme)

        context.publishService.publish()

        assertEquals(
            PublishedSnapshot(
                works = WorksContent(works = listOf(publishedWork)),
                profile = profile,
                terminal = terminal,
                readme = readme,
            ),
            context.publishService.publishedSnapshot(),
        )
        assertTrue(context.storage.objects.containsKey("content/published/works.json"))
        assertTrue(context.storage.objects.containsKey("content/published/profile.json"))
        assertTrue(context.storage.objects.containsKey("content/published/terminal-commands.json"))
        assertTrue(context.storage.objects.containsKey("content/published/readme.json"))
    }

    @Test
    fun publishStampsAndReturnsMetaWithTheInjectedTime() = runTest {
        val context = PublishTestContext()

        val result = context.publishService.publish()

        val expected = ContentMeta(lastPublishedAt = PUBLISHED_AT)
        assertEquals(expected, result)
        assertEquals(expected, context.publishService.meta())
        assertTrue(context.storage.objects.getValue("content/meta.json").contains(PUBLISHED_AT))
    }

    @Test
    fun publishedSnapshotIsEmptyBeforePublishAndContainsDocumentsAfterwards() = runTest {
        val context = PublishTestContext()
        val works = WorksContent(
            works = listOf(Work(id = "published", name = "Published", status = ContentStatus.Published)),
        )
        val profile = AdminProfile(displayName = "Admin")
        val terminal = terminalContent("hello")
        val readme = readmeContent("Readme")

        assertEquals(PublishedSnapshot(), context.publishService.publishedSnapshot())

        context.contentService.saveWorksDraft(works)
        context.contentService.saveProfileDraft(profile)
        context.contentService.saveTerminalDraft(terminal)
        context.contentService.saveReadmeDraft(readme)
        context.publishService.publish()

        assertEquals(
            PublishedSnapshot(
                works = works,
                profile = profile,
                terminal = terminal,
                readme = readme,
            ),
            context.publishService.publishedSnapshot(),
        )
    }

    @Test
    fun discardDraftRestoresEveryDocumentFromItsPublishedCopy() = runTest {
        val context = PublishTestContext()
        val works = WorksContent(
            works = listOf(Work(id = "published", name = "Published", status = ContentStatus.Published)),
        )
        val profile = AdminProfile(displayName = "Published profile")
        val terminal = terminalContent("published")
        val readme = readmeContent("Published readme")
        context.contentService.saveWorksDraft(works)
        context.contentService.saveProfileDraft(profile)
        context.contentService.saveTerminalDraft(terminal)
        context.contentService.saveReadmeDraft(readme)
        context.publishService.publish()
        context.contentService.saveWorksDraft(WorksContent())
        context.contentService.saveProfileDraft(AdminProfile(displayName = "Changed"))
        context.contentService.saveTerminalDraft(terminalContent("changed"))
        context.contentService.saveReadmeDraft(readmeContent("Changed"))

        ContentDocument.entries.forEach { document ->
            context.publishService.discardDraft(document)
        }

        assertEquals(works, context.contentService.worksDraft())
        assertEquals(profile, context.contentService.profileDraft())
        assertEquals(terminal, context.contentService.terminalDraft())
        assertEquals(readme, context.contentService.readmeDraft())
    }

    @Test
    fun discardDraftRestoresEveryDocumentFromSeedsBeforeFirstPublish() = runTest {
        val context = PublishTestContext()
        val seedWorks = context.contentService.worksDraft()
        val seedProfile = context.contentService.profileDraft()
        val seedTerminal = context.contentService.terminalDraft()
        val seedReadme = context.contentService.readmeDraft()
        context.contentService.saveWorksDraft(
            WorksContent(works = listOf(Work(id = "changed", name = "Changed"))),
        )
        context.contentService.saveProfileDraft(AdminProfile(displayName = "Changed"))
        context.contentService.saveTerminalDraft(terminalContent("changed"))
        context.contentService.saveReadmeDraft(readmeContent("Changed"))

        ContentDocument.entries.forEach { document ->
            context.publishService.discardDraft(document)
        }

        assertEquals(seedWorks, context.contentService.worksDraft())
        assertEquals(seedProfile, context.contentService.profileDraft())
        assertEquals(seedTerminal, context.contentService.terminalDraft())
        assertEquals(seedReadme, context.contentService.readmeDraft())
    }
}
