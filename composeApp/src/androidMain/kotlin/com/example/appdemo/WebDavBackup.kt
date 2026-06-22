package com.example.appdemo

import android.content.Context
import android.util.Base64
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.security.SecureRandom
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import javax.crypto.KeyGenerator

private const val RECORDS_PREFS_NAME = "milk_tea_tracker"
private const val WEBDAV_PREFS_NAME = "webdav_backup_settings"
private const val KEY_URL = "url"
private const val KEY_USERNAME = "username"
private const val KEY_PASSWORD = "password"
private const val KEY_BACKUP_PASSWORD = "backup_password"
private const val KEY_LAST_BACKUP = "last_backup"
private const val KEYSTORE_ALIAS = "appdemo_webdav_settings"
private const val BACKUP_VERSION = 1
private const val PBKDF2_ITERATIONS = 150_000

private data class WebDavConfig(
    val url: String,
    val username: String,
    val password: String,
    val backupPassword: String,
)

@Composable
internal fun WebDavBackupScreen(
    backupFileName: String,
    recordsKey: String,
    recordType: String,
    onRestored: () -> Unit,
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val initial = remember { loadWebDavConfig(context) }
    var url by rememberSaveable { mutableStateOf(initial.url) }
    var username by rememberSaveable { mutableStateOf(initial.username) }
    var password by rememberSaveable { mutableStateOf(initial.password) }
    var backupPassword by rememberSaveable { mutableStateOf(initial.backupPassword) }
    var status by rememberSaveable { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var confirmRestore by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun currentConfig(): WebDavConfig = WebDavConfig(
        url = url.trim(),
        username = username.trim(),
        password = password,
        backupPassword = backupPassword,
    )

    fun validate(config: WebDavConfig): String? = when {
        config.url.isBlank() -> "请填写 WebDAV 目录地址"
        !config.url.startsWith("https://", ignoreCase = true) -> "为保护记录，只允许使用 HTTPS 地址"
        config.username.isBlank() -> "请填写用户名"
        config.password.isBlank() -> "请填写 WebDAV 密码"
        config.backupPassword.length < 8 -> "备份密码至少需要 8 位"
        else -> null
    }

    fun runAction(block: suspend (WebDavConfig) -> String) {
        val config = currentConfig()
        val error = validate(config)
        if (error != null) {
            status = error
            return
        }
        saveWebDavConfig(context, config)
        busy = true
        status = "正在处理…"
        scope.launch {
            status = runCatching { block(config) }
                .getOrElse { it.message ?: "操作失败" }
            busy = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text("WebDAV 云备份", style = MaterialTheme.typography.headlineSmall)
        Text(
            "当前备份：$recordType。记录会先在手机端加密，再上传到 WebDAV。",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("WebDAV 目录地址") },
            placeholder = { Text("https://dav.example.com/backups/") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
        )
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("用户名") },
            singleLine = true,
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("WebDAV 密码") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
        )
        OutlinedTextField(
            value = backupPassword,
            onValueChange = { backupPassword = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("备份加密密码") },
            supportingText = { Text("换手机恢复时需要，请务必记住（至少 8 位）") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            OutlinedButton(
                enabled = !busy,
                modifier = Modifier.weight(1f),
                onClick = {
                    runAction { config ->
                        withContext(Dispatchers.IO) { testWebDav(config) }
                        "连接成功"
                    }
                },
            ) { Text("测试连接") }
            Button(
                enabled = !busy,
                modifier = Modifier.weight(1f),
                onClick = {
                    val config = currentConfig()
                    val error = validate(config)
                    if (error != null) {
                        status = error
                    } else {
                        saveWebDavConfig(context, config)
                        status = "配置已保存"
                    }
                },
            ) { Text("保存配置") }
        }

        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
            ),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    enabled = !busy,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        runAction { config ->
                            val rawRecords = context
                                .getSharedPreferences(RECORDS_PREFS_NAME, Context.MODE_PRIVATE)
                                .getString(recordsKey, "[]") ?: "[]"
                            withContext(Dispatchers.IO) {
                                uploadBackup(config, backupFileName, recordType, rawRecords)
                            }
                            context.getSharedPreferences(WEBDAV_PREFS_NAME, Context.MODE_PRIVATE)
                                .edit().putLong(KEY_LAST_BACKUP, System.currentTimeMillis()).apply()
                            "备份成功，共 ${JSONArray(rawRecords).length()} 条记录"
                        }
                    },
                ) { Text("备份到云端") }
                OutlinedButton(
                    enabled = !busy,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { confirmRestore = true },
                ) { Text("从云端恢复") }
            }
        }

        if (busy) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator()
            }
        }
        if (status.isNotBlank()) {
            Text(status, color = MaterialTheme.colorScheme.primary)
        }
        val lastBackup = remember(status) {
            context.getSharedPreferences(WEBDAV_PREFS_NAME, Context.MODE_PRIVATE)
                .getLong(KEY_LAST_BACKUP, 0L)
        }
        if (lastBackup > 0L) {
            Text(
                "最近备份：${formatTime(lastBackup)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    if (confirmRestore) {
        AlertDialog(
            onDismissRequest = { confirmRestore = false },
            title = { Text("恢复云端备份？") },
            text = { Text("恢复会用云端备份覆盖当前设备上的全部$recordType。此操作无法撤销。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmRestore = false
                        runAction { config ->
                            val restored = withContext(Dispatchers.IO) {
                                downloadBackup(config, backupFileName, recordType)
                            }
                            context.getSharedPreferences(RECORDS_PREFS_NAME, Context.MODE_PRIVATE)
                                .edit().putString(recordsKey, restored).commit()
                            onRestored()
                            "恢复成功，共 ${JSONArray(restored).length()} 条记录"
                        }
                    },
                ) { Text("确认恢复") }
            },
            dismissButton = {
                TextButton(onClick = { confirmRestore = false }) { Text("取消") }
            },
        )
    }
}

private fun loadWebDavConfig(context: Context): WebDavConfig {
    val prefs = context.getSharedPreferences(WEBDAV_PREFS_NAME, Context.MODE_PRIVATE)
    return WebDavConfig(
        url = prefs.getString(KEY_URL, "") ?: "",
        username = prefs.getString(KEY_USERNAME, "") ?: "",
        password = decryptLocalSecret(prefs.getString(KEY_PASSWORD, "").orEmpty()),
        backupPassword = decryptLocalSecret(prefs.getString(KEY_BACKUP_PASSWORD, "").orEmpty()),
    )
}

private fun saveWebDavConfig(context: Context, config: WebDavConfig) {
    context.getSharedPreferences(WEBDAV_PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_URL, config.url)
        .putString(KEY_USERNAME, config.username)
        .putString(KEY_PASSWORD, encryptLocalSecret(config.password))
        .putString(KEY_BACKUP_PASSWORD, encryptLocalSecret(config.backupPassword))
        .apply()
}

private fun testWebDav(config: WebDavConfig) {
    val testFileName = ".appdemo-write-test-${UUID.randomUUID()}.tmp"
    val testUrl = buildBackupUrl(config.url, testFileName)
    val connection = openConnection(config, testUrl, "PUT")
    try {
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/octet-stream")
        val bytes = "WebDAV write test".toByteArray(StandardCharsets.UTF_8)
        connection.setFixedLengthStreamingMode(bytes.size)
        connection.outputStream.use { it.write(bytes) }
        val code = connection.responseCode
        if (code !in 200..299) throwWebDavError("写入测试", code, connection, config)
    } finally {
        connection.disconnect()
    }

    val deleteConnection = openConnection(config, testUrl, "DELETE")
    try {
        val code = deleteConnection.responseCode
        if (code !in 200..299 && code != HttpURLConnection.HTTP_NOT_FOUND) {
            throwWebDavError("清理测试文件", code, deleteConnection, config)
        }
    } finally {
        deleteConnection.disconnect()
    }
}

private fun uploadBackup(
    config: WebDavConfig,
    fileName: String,
    recordType: String,
    rawRecords: String,
) {
    JSONArray(rawRecords)
    val plain = JSONObject()
        .put("version", BACKUP_VERSION)
        .put("recordType", recordType)
        .put("createdAt", System.currentTimeMillis())
        .put("records", JSONArray(rawRecords))
        .toString()
    val encrypted = encryptBackup(plain, config.backupPassword)
    val connection = openConnection(config, buildBackupUrl(config.url, fileName), "PUT")
    try {
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/octet-stream")
        val bytes = encrypted.toByteArray(StandardCharsets.UTF_8)
        connection.setFixedLengthStreamingMode(bytes.size)
        connection.outputStream.use { it.write(bytes) }
        val code = connection.responseCode
        if (code !in 200..299) throwWebDavError("上传", code, connection, config)
    } finally {
        connection.disconnect()
    }
}

private fun downloadBackup(
    config: WebDavConfig,
    fileName: String,
    expectedRecordType: String,
): String {
    val connection = openConnection(config, buildBackupUrl(config.url, fileName), "GET")
    try {
        val code = connection.responseCode
        if (code == HttpURLConnection.HTTP_NOT_FOUND) error("云端还没有备份文件")
        if (code !in 200..299) throwWebDavError("下载", code, connection, config)
        val encrypted = connection.inputStream.bufferedReader(StandardCharsets.UTF_8).use { it.readText() }
        val payload = JSONObject(decryptBackup(encrypted, config.backupPassword))
        if (payload.optInt("version") != BACKUP_VERSION) error("不支持的备份版本")
        if (payload.optString("recordType") != expectedRecordType) error("云端备份类型不匹配")
        return payload.getJSONArray("records").toString()
    } catch (e: javax.crypto.AEADBadTagException) {
        error("备份密码错误或备份文件已损坏")
    } finally {
        connection.disconnect()
    }
}

private fun openConnection(config: WebDavConfig, url: String, method: String): HttpURLConnection {
    val connection = URL(url).openConnection() as HttpURLConnection
    connection.requestMethod = method
    connection.connectTimeout = 15_000
    connection.readTimeout = 30_000
    connection.instanceFollowRedirects = true
    val credentials = "${config.username}:${config.password}"
    val authorization = Base64.encodeToString(
        credentials.toByteArray(StandardCharsets.UTF_8),
        Base64.NO_WRAP,
    )
    connection.setRequestProperty("Authorization", "Basic $authorization")
    return connection
}

private fun throwWebDavError(
    action: String,
    code: Int,
    connection: HttpURLConnection,
    config: WebDavConfig,
): Nothing {
    val detail = runCatching {
        connection.errorStream?.bufferedReader(StandardCharsets.UTF_8)?.use { it.readText() }
            ?.replace(Regex("<[^>]+>"), " ")
            ?.replace(Regex("\\s+"), " ")
            ?.trim()
            ?.take(160)
    }.getOrNull().orEmpty()
    val message = when (code) {
        HttpURLConnection.HTTP_UNAUTHORIZED -> "认证失败，请检查用户名和 WebDAV 专用密码"
        HttpURLConnection.HTTP_FORBIDDEN -> {
            if (config.url.contains("teracloud", ignoreCase = true) ||
                config.url.contains("infini-cloud", ignoreCase = true)
            ) {
                "服务器拒绝写入。InfiniCLOUD 请使用 WebDAV 连接 ID 和专用密码，并确认目录已存在且可写"
            } else {
                "服务器拒绝写入，请检查账号写入权限和目录地址"
            }
        }
        HttpURLConnection.HTTP_NOT_FOUND -> "目录不存在，请先在 WebDAV 中创建该目录"
        else -> "$action 失败，服务器返回 HTTP $code"
    }
    error(if (detail.isBlank()) message else "$message（$detail）")
}

private fun buildBackupUrl(baseUrl: String, fileName: String): String {
    val normalized = baseUrl.trim().trimEnd('/')
    return if (fileName.isBlank()) "$normalized/" else "$normalized/$fileName"
}

private fun encryptBackup(plain: String, password: String): String {
    val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
    val iv = ByteArray(12).also { SecureRandom().nextBytes(it) }
    val key = deriveBackupKey(password, salt)
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))
    val ciphertext = cipher.doFinal(plain.toByteArray(StandardCharsets.UTF_8))
    return JSONObject()
        .put("format", "appdemo-encrypted-backup")
        .put("version", BACKUP_VERSION)
        .put("iterations", PBKDF2_ITERATIONS)
        .put("salt", Base64.encodeToString(salt, Base64.NO_WRAP))
        .put("iv", Base64.encodeToString(iv, Base64.NO_WRAP))
        .put("data", Base64.encodeToString(ciphertext, Base64.NO_WRAP))
        .toString()
}

private fun decryptBackup(encrypted: String, password: String): String {
    val envelope = JSONObject(encrypted)
    if (envelope.optString("format") != "appdemo-encrypted-backup") error("备份文件格式无效")
    val salt = Base64.decode(envelope.getString("salt"), Base64.NO_WRAP)
    val iv = Base64.decode(envelope.getString("iv"), Base64.NO_WRAP)
    val data = Base64.decode(envelope.getString("data"), Base64.NO_WRAP)
    val iterations = envelope.optInt("iterations", PBKDF2_ITERATIONS)
    val key = deriveBackupKey(password, salt, iterations)
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
    return String(cipher.doFinal(data), StandardCharsets.UTF_8)
}

private fun deriveBackupKey(
    password: String,
    salt: ByteArray,
    iterations: Int = PBKDF2_ITERATIONS,
): SecretKeySpec {
    val spec = PBEKeySpec(password.toCharArray(), salt, iterations, 256)
    val bytes = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
    spec.clearPassword()
    return SecretKeySpec(bytes, "AES")
}

private fun encryptLocalSecret(value: String): String {
    if (value.isEmpty()) return ""
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, getOrCreateLocalKey())
    val encrypted = cipher.doFinal(value.toByteArray(StandardCharsets.UTF_8))
    return Base64.encodeToString(cipher.iv + encrypted, Base64.NO_WRAP)
}

private fun decryptLocalSecret(value: String): String {
    if (value.isEmpty()) return ""
    return runCatching {
        val bytes = Base64.decode(value, Base64.NO_WRAP)
        val iv = bytes.copyOfRange(0, 12)
        val encrypted = bytes.copyOfRange(12, bytes.size)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, getOrCreateLocalKey(), GCMParameterSpec(128, iv))
        String(cipher.doFinal(encrypted), StandardCharsets.UTF_8)
    }.getOrDefault("")
}

private fun getOrCreateLocalKey(): java.security.Key {
    val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    keyStore.getKey(KEYSTORE_ALIAS, null)?.let { return it }
    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
    keyGenerator.init(
        KeyGenParameterSpec.Builder(
            KEYSTORE_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build(),
    )
    return keyGenerator.generateKey()
}
