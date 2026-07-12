package com.shayarishaala.shayarishaala.ui.studio

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

// ─────────────────────────────────────────────
//  UI State
// ─────────────────────────────────────────────
data class ShayariStudioUiState(
    val shayariText: String = "",
    val selectedBackground: ShayariBackground = ShayariBackground.ROSE_ROMANCE,
    val selectedFont: ShayariFont = ShayariFont.SERIF,
    val selectedAlignment: TextAlignOption = TextAlignOption.CENTER,
    val selectedTextSize: TextSizeOption = TextSizeOption.MEDIUM,
    val selectedTextColor: TextColorOption = TextColorOption.WHITE,
    val selectedCardStyle: CardStyle = CardStyle.TRANSPARENT,
    val activeTool: StudioTool? = null,
    val isExporting: Boolean = false,
    val exportResult: ExportResult? = null
)

enum class StudioTool { THEME, FONT, COLOR, ALIGNMENT, CARD_STYLE, DOWNLOAD }

// ─────────────────────────────────────────────
//  ViewModel
// ─────────────────────────────────────────────
class ShayariStudioViewModel(initialText: String) : ViewModel() {

    companion object {
        private const val FILE_PROVIDER_AUTHORITY =
            "com.shayarishaala.shayarishaala.provider"

        fun factory(initialText: String) = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return ShayariStudioViewModel(initialText) as T
            }
        }
    }

    private val _state = MutableStateFlow(ShayariStudioUiState(shayariText = initialText))
    val state: StateFlow<ShayariStudioUiState> = _state.asStateFlow()

    // ── Tool selection ──────────────────────────────────────────────────────
    fun onToolSelected(tool: StudioTool) {
        val current = _state.value.activeTool
        // Toggle: tap active tool again to collapse
        _state.value = _state.value.copy(activeTool = if (current == tool) null else tool)
    }

    fun closePicker() {
        _state.value = _state.value.copy(activeTool = null)
    }

    // ── Per-property updates ────────────────────────────────────────────────
    fun onBackgroundSelected(bg: ShayariBackground) {
        _state.value = _state.value.copy(selectedBackground = bg)
    }

    fun onFontSelected(font: ShayariFont) {
        _state.value = _state.value.copy(selectedFont = font)
    }

    fun onAlignmentSelected(align: TextAlignOption) {
        _state.value = _state.value.copy(selectedAlignment = align)
    }

    fun onTextSizeSelected(size: TextSizeOption) {
        _state.value = _state.value.copy(selectedTextSize = size)
    }

    fun onTextColorSelected(color: TextColorOption) {
        _state.value = _state.value.copy(selectedTextColor = color)
    }

    fun onCardStyleSelected(style: CardStyle) {
        _state.value = _state.value.copy(selectedCardStyle = style)
    }

    fun clearExportResult() {
        _state.value = _state.value.copy(exportResult = null)
    }

    // ── Save to gallery ─────────────────────────────────────────────────────
    fun saveImage(context: Context, bitmap: Bitmap) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isExporting = true)
            val result = withContext(Dispatchers.IO) {
                try {
                    saveToGallery(context, bitmap)
                    ExportResult.Success()
                } catch (e: Exception) {
                    ExportResult.Failure(e.message ?: "Unknown error")
                }
            }
            _state.value = _state.value.copy(isExporting = false, exportResult = result)
        }
    }

    // ── Share via FileProvider ──────────────────────────────────────────────
    fun getShareUri(context: Context, bitmap: Bitmap): Uri? {
        return try {
            val imagesDir = File(context.cacheDir, "images").also { it.mkdirs() }
            val imageFile = File(imagesDir, "shayari_shaala_${System.currentTimeMillis()}.png")
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITY, imageFile)
        } catch (e: Exception) {
            null
        }
    }

    // ── Internal save helper ────────────────────────────────────────────────
    private fun saveToGallery(context: Context, bitmap: Bitmap) {
        val fileName = "ShayariShaala_${System.currentTimeMillis()}.png"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // MediaStore — no permission needed (API 29+)
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/ShayariShaala"
                )
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val resolver = context.contentResolver
            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                ?: throw Exception("Failed to create MediaStore entry")

            resolver.openOutputStream(uri)?.use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, values, null, null)
        } else {
            // API 24-28 — use app-specific external dir (no permission needed)
            val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                ?: throw Exception("External storage unavailable")
            val file = File(dir, fileName)
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
        }
    }
}
