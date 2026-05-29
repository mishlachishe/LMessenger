package ru.mishlachok.LMessageClient.presentation.screens.chat

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.mishlachok.LMessageClient.data.remote.ApiService
import ru.mishlachok.LMessageClient.domain.model.Attachment
import ru.mishlachok.LMessageClient.domain.model.Message
import java.io.File
import java.net.URL

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
	chatId: Long,
	navController: NavHostController,
	viewModel: ChatDetailViewModel = hiltViewModel()
) {
	val messages by viewModel.messages.collectAsStateWithLifecycle()
	val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
	val error by viewModel.error.collectAsStateWithLifecycle()
	val currentUserId by viewModel.currentUserId.collectAsStateWithLifecycle()
	var text by remember { mutableStateOf("") }
	val listState = rememberLazyListState()
	val snackbarHostState = remember { SnackbarHostState() }
	val context = LocalContext.current
	val attachmentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
		uri?.let { selectedUri ->
			val file = copyUriToCache(context, selectedUri)
			if (file != null) {
				viewModel.sendAttachment(chatId, file.absolutePath, context.contentResolver.getType(selectedUri))
			}
		}
	}

	LaunchedEffect(chatId) { viewModel.loadMessages(chatId) }

	LaunchedEffect(messages.size) {
		if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
	}

	LaunchedEffect(error) {
		error?.let {
			snackbarHostState.showSnackbar(it)
			viewModel.clearError()
		}
	}

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) },
		contentWindowInsets = WindowInsets.systemBars,
		topBar = {
			TopAppBar(
				title = { Text("Чат") },
				navigationIcon = {
					IconButton(onClick = { navController.popBackStack() }) {
						Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
					}
				}
			)
		},
		bottomBar = {
			Row(
				modifier = Modifier
					.fillMaxWidth()
					.padding(8.dp)
					.navigationBarsPadding(),
				verticalAlignment = Alignment.CenterVertically
			) {
				IconButton(onClick = { attachmentLauncher.launch("*/*") }) {
					Icon(Icons.Default.AttachFile, contentDescription = "Прикрепить файл")
				}
				TextField(
					value = text,
					onValueChange = { text = it },
					modifier = Modifier.weight(1f),
					placeholder = { Text("Сообщение") }
				)
				IconButton(onClick = {
					if (text.isNotBlank()) {
						viewModel.sendMessage(chatId, text)
						text = ""
					}
				}) {
					Icon(Icons.Default.Send, contentDescription = "Отправить")
				}
			}
		}
	) { padding ->
		when {
			isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
				CircularProgressIndicator()
			}
			messages.isEmpty() -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
				Text("Нет сообщений")
			}
			else -> LazyColumn(
				state = listState,
				modifier = Modifier
					.fillMaxSize()
					.padding(padding)
					.padding(horizontal = 8.dp)
			) {
				items(messages) { msg -> MessageBubble(msg, currentUserId) }
			}
		}
	}
}

@Composable
fun MessageBubble(message: Message, currentUserId: Long?) {
	val isOwnMessage = currentUserId != null && message.senderId == currentUserId
	val bubbleColor = if (isOwnMessage) {
		MaterialTheme.colorScheme.primaryContainer
	} else {
		MaterialTheme.colorScheme.surfaceVariant
	}
	val contentColor = if (isOwnMessage) {
		MaterialTheme.colorScheme.onPrimaryContainer
	} else {
		MaterialTheme.colorScheme.onSurfaceVariant
	}

	Column(
		modifier = Modifier
			.fillMaxWidth()
			.padding(vertical = 4.dp),
		horizontalAlignment = if (isOwnMessage) Alignment.End else Alignment.Start
	) {
		Surface(
			color = bubbleColor,
			contentColor = contentColor,
			shape = MaterialTheme.shapes.medium,
			tonalElevation = 1.dp,
			modifier = Modifier.widthIn(max = 280.dp)
		) {
			Column(modifier = Modifier.padding(8.dp)) {
				if (!message.text.isNullOrBlank()) {
					Text(text = message.text)
				}
				message.attachments.forEach { attachment ->
					Spacer(modifier = Modifier.height(if (message.text.isNullOrBlank()) 0.dp else 6.dp))
					AttachmentView(attachment = attachment)
				}
			}
		}
		Text(
			text = message.createdAt,
			style = MaterialTheme.typography.labelSmall,
			modifier = Modifier.padding(top = 2.dp)
		)
	}
}

@Composable
private fun AttachmentView(attachment: Attachment) {
	val context = LocalContext.current
	val url = attachment.absoluteUrl()
	if (attachment.isImage()) {
		var imageBitmap by remember(url) { mutableStateOf<androidx.compose.ui.graphics.ImageBitmap?>(null) }
		var failed by remember(url) { mutableStateOf(false) }

		LaunchedEffect(url) {
			runCatching {
				withContext(Dispatchers.IO) {
					URL(url).openStream().use { input ->
						BitmapFactory.decodeStream(input)?.asImageBitmap()
					}
				}
			}.onSuccess { bitmap ->
				imageBitmap = bitmap
			}.onFailure {
				failed = true
			}
		}

		when {
			imageBitmap != null -> Image(
				bitmap = imageBitmap!!,
				contentDescription = attachment.fileName,
				contentScale = ContentScale.Fit,
				modifier = Modifier
					.widthIn(max = 240.dp)
					.heightIn(max = 260.dp)
					.clip(RoundedCornerShape(10.dp))
					.clickable { openAttachment(context, url) }
			)
			failed -> AttachmentLink(attachment = attachment, url = url)
			else -> Box(
				modifier = Modifier
					.width(180.dp)
					.height(120.dp),
				contentAlignment = Alignment.Center
			) { CircularProgressIndicator() }
		}
	} else {
		AttachmentLink(attachment = attachment, url = url)
	}
}

@Composable
private fun AttachmentLink(attachment: Attachment, url: String) {
	val context = LocalContext.current
	Text(
		text = "📎 ${attachment.fileName}",
		style = MaterialTheme.typography.bodyMedium,
		color = MaterialTheme.colorScheme.primary,
		modifier = Modifier.clickable { openAttachment(context, url) }
	)
}

private fun Attachment.isImage(): Boolean {
	val normalizedMime = mimeType?.lowercase().orEmpty()
	val normalizedName = fileName.lowercase()
	return normalizedMime.startsWith("image/") ||
		normalizedName.endsWith(".png") ||
		normalizedName.endsWith(".jpg") ||
		normalizedName.endsWith(".jpeg") ||
		normalizedName.endsWith(".gif") ||
		normalizedName.endsWith(".webp")
}

private fun Attachment.absoluteUrl(): String {
	return if (url.startsWith("http://") || url.startsWith("https://")) {
		url
	} else {
		ApiService.BASE_URL.trimEnd('/') + "/" + url.trimStart('/')
	}
}

private fun openAttachment(context: Context, url: String) {
	runCatching {
		context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
	}
}

private fun copyUriToCache(context: Context, uri: Uri): File? {
	val fileName = getFileName(context, uri) ?: "attachment_${System.currentTimeMillis()}"
	val safeName = fileName.replace(Regex("[^A-Za-z0-9._-]"), "_")
	val file = File(context.cacheDir, safeName)
	val input = context.contentResolver.openInputStream(uri) ?: return null
	input.use { inputStream ->
		file.outputStream().use { outputStream ->
			inputStream.copyTo(outputStream)
		}
	}
	return file
}

private fun getFileName(context: Context, uri: Uri): String? {
	if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
		context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
			val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
			if (nameIndex >= 0 && cursor.moveToFirst()) {
				return cursor.getString(nameIndex)
			}
		}
	}
	return uri.path?.let { File(it).name }
}
