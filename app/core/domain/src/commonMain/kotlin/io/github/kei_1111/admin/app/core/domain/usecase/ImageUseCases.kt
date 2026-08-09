package io.github.kei_1111.admin.app.core.domain.usecase

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import io.github.kei_1111.admin.app.core.data.repository.AdminImageRepository
import io.github.kei_1111.admin.app.core.utils.PickedImageFile
import io.github.kei_1111.admin.app.core.utils.pickImageFile

/** 画像ファイル選択ダイアログを開く(未選択は null)。 */
interface PickImageUseCase {
    suspend operator fun invoke(): PickedImageFile?
}

@ContributesBinding(AppScope::class)
@Inject
internal class PickImageUseCaseImpl : PickImageUseCase {
    override suspend fun invoke(): PickedImageFile? = pickImageFile()
}

/** スクリーンショットをアップロードして配信パスを返す。 */
interface UploadWorkImageUseCase {
    suspend operator fun invoke(workId: String, file: PickedImageFile): String
}

@ContributesBinding(AppScope::class)
@Inject
internal class UploadWorkImageUseCaseImpl(
    private val repository: AdminImageRepository,
) : UploadWorkImageUseCase {
    override suspend fun invoke(workId: String, file: PickedImageFile): String =
        repository.uploadWorkImage(
            workId = workId,
            fileName = file.name,
            mimeType = file.mimeType,
            bytes = file.bytes,
        )
}

/** プロフィールアバターをアップロードして配信パスを返す。 */
interface UploadProfileImageUseCase {
    suspend operator fun invoke(file: PickedImageFile): String
}

@ContributesBinding(AppScope::class)
@Inject
internal class UploadProfileImageUseCaseImpl(
    private val repository: AdminImageRepository,
) : UploadProfileImageUseCase {
    override suspend fun invoke(file: PickedImageFile): String =
        repository.uploadProfileImage(fileName = file.name, mimeType = file.mimeType, bytes = file.bytes)
}
