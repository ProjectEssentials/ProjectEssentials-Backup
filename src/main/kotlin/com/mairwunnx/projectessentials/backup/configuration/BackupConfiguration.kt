package com.mairwunnx.projectessentials.backup.configuration

import kotlinx.serialization.Serializable

@Serializable
data class BackupConfiguration(
    var backupEnabled: Boolean = true,
    var firstLaunchDelay: Boolean = true,
    var backupCreationDelay: Long = 150_000,
    var backupCompressionLevel: Int = 3,
    var backupDirectoryPath: String = "backup",
    var backupDateFormat: String = "yyyy-MM-dd_HH.mm.ss",
    var maxBackupFiles: Int = 10,
    var rollingBackupFilesEnabled: Boolean = true,
    var removeExtraFiles: Boolean = true
)
