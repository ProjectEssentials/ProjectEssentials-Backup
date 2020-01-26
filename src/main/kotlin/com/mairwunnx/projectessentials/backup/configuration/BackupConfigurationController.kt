package com.mairwunnx.projectessentials.backup.configuration

import com.mairwunnx.projectessentials.core.helpers.MOD_CONFIG_FOLDER
import com.mairwunnx.projectessentials.core.helpers.jsonInstance
import org.apache.logging.log4j.LogManager
import java.io.File

object BackupConfigurationController {
    private val backupConfigurationPath = MOD_CONFIG_FOLDER + File.separator + "backup.json"
    private var backupConfiguration = BackupConfiguration()
    private val logger = LogManager.getLogger()

    fun load() {
        logger.info("Loading backup configuration")
        if (!File(backupConfigurationPath).exists()) {
            logger.warn("Backup config not exist! creating it now!")
            File(MOD_CONFIG_FOLDER).mkdirs()
            val defaultConfig = jsonInstance.stringify(
                BackupConfiguration.serializer(), backupConfiguration
            )
            File(backupConfigurationPath).writeText(defaultConfig)
        }
        backupConfiguration = jsonInstance.parse(
            BackupConfiguration.serializer(), File(backupConfigurationPath).readText()
        )
    }

    fun save() {
        logger.info("Saving backup configuration")
        File(MOD_CONFIG_FOLDER).mkdirs()
        val backupConfigurationString = jsonInstance.stringify(
            BackupConfiguration.serializer(), backupConfiguration
        )
        File(backupConfigurationPath).writeText(backupConfigurationString)
    }

    fun get() = backupConfiguration
}
