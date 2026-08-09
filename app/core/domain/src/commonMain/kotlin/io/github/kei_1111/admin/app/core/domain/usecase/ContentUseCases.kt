package io.github.kei_1111.admin.app.core.domain.usecase

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.kei_1111.admin.app.core.data.repository.AdminContentRepository
import io.github.kei_1111.admin.app.core.data.repository.AdminPreviewRepository
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.ReadmeContent
import io.github.kei_1111.admin.shared.model.TerminalCommandsContent
import io.github.kei_1111.admin.shared.model.WorksContent
import io.github.kei_1111.admin.shared.model.portfolio.ContributionCalendar
import io.github.kei_1111.admin.shared.model.portfolio.GitHubProfile

interface GetWorksDraftUseCase {
    suspend operator fun invoke(): WorksContent
}

interface SaveWorksDraftUseCase {
    suspend operator fun invoke(content: WorksContent): WorksContent
}

interface GetProfileDraftUseCase {
    suspend operator fun invoke(): AdminProfile
}

interface SaveProfileDraftUseCase {
    suspend operator fun invoke(profile: AdminProfile): AdminProfile
}

interface GetContentMetaUseCase {
    suspend operator fun invoke(): ContentMeta
}

interface PublishContentUseCase {
    suspend operator fun invoke(): ContentMeta
}

interface GetPortfolioProfileUseCase {
    suspend operator fun invoke(): GitHubProfile
}

interface GetPortfolioContributionsUseCase {
    suspend operator fun invoke(): ContributionCalendar
}

@Inject
@ContributesBinding(AppScope::class)
internal class GetWorksDraftUseCaseImpl(
    private val repository: AdminContentRepository,
) : GetWorksDraftUseCase {
    override suspend fun invoke(): WorksContent = repository.fetchWorksDraft()
}

@Inject
@ContributesBinding(AppScope::class)
internal class SaveWorksDraftUseCaseImpl(
    private val repository: AdminContentRepository,
) : SaveWorksDraftUseCase {
    override suspend fun invoke(content: WorksContent): WorksContent = repository.saveWorksDraft(content)
}

@Inject
@ContributesBinding(AppScope::class)
internal class GetProfileDraftUseCaseImpl(
    private val repository: AdminContentRepository,
) : GetProfileDraftUseCase {
    override suspend fun invoke(): AdminProfile = repository.fetchProfileDraft()
}

@Inject
@ContributesBinding(AppScope::class)
internal class SaveProfileDraftUseCaseImpl(
    private val repository: AdminContentRepository,
) : SaveProfileDraftUseCase {
    override suspend fun invoke(profile: AdminProfile): AdminProfile = repository.saveProfileDraft(profile)
}

@Inject
@ContributesBinding(AppScope::class)
internal class GetContentMetaUseCaseImpl(
    private val repository: AdminContentRepository,
) : GetContentMetaUseCase {
    override suspend fun invoke(): ContentMeta = repository.fetchMeta()
}

@Inject
@ContributesBinding(AppScope::class)
internal class PublishContentUseCaseImpl(
    private val repository: AdminContentRepository,
) : PublishContentUseCase {
    override suspend fun invoke(): ContentMeta = repository.publish()
}

@Inject
@ContributesBinding(AppScope::class)
internal class GetPortfolioProfileUseCaseImpl(
    private val repository: AdminPreviewRepository,
) : GetPortfolioProfileUseCase {
    override suspend fun invoke(): GitHubProfile = repository.fetchPortfolioProfile()
}

@Inject
@ContributesBinding(AppScope::class)
internal class GetPortfolioContributionsUseCaseImpl(
    private val repository: AdminPreviewRepository,
) : GetPortfolioContributionsUseCase {
    override suspend fun invoke(): ContributionCalendar = repository.fetchPortfolioContributions()
}

interface GetTerminalDraftUseCase {
    suspend operator fun invoke(): TerminalCommandsContent
}

@ContributesBinding(AppScope::class)
@Inject
internal class GetTerminalDraftUseCaseImpl(
    private val repository: AdminContentRepository,
) : GetTerminalDraftUseCase {
    override suspend fun invoke(): TerminalCommandsContent = repository.fetchTerminalDraft()
}

interface SaveTerminalDraftUseCase {
    suspend operator fun invoke(content: TerminalCommandsContent): TerminalCommandsContent
}

@ContributesBinding(AppScope::class)
@Inject
internal class SaveTerminalDraftUseCaseImpl(
    private val repository: AdminContentRepository,
) : SaveTerminalDraftUseCase {
    override suspend fun invoke(content: TerminalCommandsContent): TerminalCommandsContent =
        repository.saveTerminalDraft(content)
}

interface GetReadmeDraftUseCase {
    suspend operator fun invoke(): ReadmeContent
}

@ContributesBinding(AppScope::class)
@Inject
internal class GetReadmeDraftUseCaseImpl(
    private val repository: AdminContentRepository,
) : GetReadmeDraftUseCase {
    override suspend fun invoke(): ReadmeContent = repository.fetchReadmeDraft()
}

interface SaveReadmeDraftUseCase {
    suspend operator fun invoke(content: ReadmeContent): ReadmeContent
}

@ContributesBinding(AppScope::class)
@Inject
internal class SaveReadmeDraftUseCaseImpl(
    private val repository: AdminContentRepository,
) : SaveReadmeDraftUseCase {
    override suspend fun invoke(content: ReadmeContent): ReadmeContent = repository.saveReadmeDraft(content)
}
