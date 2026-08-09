package io.github.kei_1111.admin.app.core.utils

import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader
import kotlin.coroutines.resume
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
actual suspend fun pickImageFile(): PickedImageFile? = suspendCancellableCoroutine { continuation ->
    val input = document.createElement("input") as HTMLInputElement
    input.type = "file"
    input.accept = "image/png,image/jpeg,image/webp"
    var resumed = false
    fun resumeOnce(value: PickedImageFile?) {
        if (!resumed) {
            resumed = true
            continuation.resume(value)
        }
    }
    input.onchange = {
        val file = input.files?.item(0)
        if (file == null) {
            resumeOnce(null)
        } else {
            val reader = FileReader()
            reader.onload = {
                // readAsDataURL の結果(data:<mime>;base64,....)からバイナリへ戻す
                val dataUrl = reader.result?.toString().orEmpty()
                val base64 = dataUrl.substringAfter("base64,", "")
                resumeOnce(
                    if (base64.isEmpty()) {
                        null
                    } else {
                        PickedImageFile(name = file.name, mimeType = file.type, bytes = Base64.decode(base64))
                    },
                )
            }
            reader.onerror = { resumeOnce(null) }
            reader.readAsDataURL(file)
        }
    }
    input.oncancel = { resumeOnce(null) }
    input.click()
}
