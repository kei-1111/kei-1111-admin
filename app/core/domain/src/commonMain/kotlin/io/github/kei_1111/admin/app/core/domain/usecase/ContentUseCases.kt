package io.github.kei_1111.admin.app.core.domain.usecase

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.kei_1111.admin.app.core.data.repository.AdminContentRepository
import io.github.kei_1111.admin.shared.model.AdminProfile
import io.github.kei_1111.admin.shared.model.ContentMeta
import io.github.kei_1111.admin.shared.model.WorksContent

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
