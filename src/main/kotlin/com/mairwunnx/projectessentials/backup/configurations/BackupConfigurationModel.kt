package com.mairwunnx.projectessentials.backup.configurations

import kotlinx.serialization.Serializable

@Serializable
data class BackupConfigurationModel(
    var backupEnabled: Boolean = true,
    var firstLaunchDelay: Boolean = true,
    var backupConfigurations: Boolean = true,
    var backupCreationDelay: Int = 300,
    var backupCompressionLevel: Int = 3,
    var backupDirectoryPath: String = "backup",
    var backupDateFormat: String = "yyyy-MM-dd_HH.mm.ss",
    var maxBackupFiles: Int = 10,
    var rollingBackupFilesEnabled: Boolean = true,
    var removeExtraFiles: Boolean = true
)
