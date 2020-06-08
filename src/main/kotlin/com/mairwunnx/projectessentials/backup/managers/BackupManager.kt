package com.mairwunnx.projectessentials.backup.managers

import com.mairwunnx.projectessentials.backup.ServerThreadWorker
import com.mairwunnx.projectessentials.backup.configurations.BackupConfiguration
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.extensions.empty
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import kotlinx.coroutines.*
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.CompressionLevel
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import net.minecraftforge.fml.server.ServerLifecycleHooks.getCurrentServer
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.MarkerManager
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.measureTimeMillis


object BackupManager {
    private val logger = LogManager.getLogger()
    private val marker = MarkerManager.getMarker("BACKUP")
    private var firstLaunch = true
    private lateinit var job: Job

    private val backupConfiguration by lazy {
        getConfigurationByName<BackupConfiguration>("backup").take()
    }

    fun initialize() {
        if (backupConfiguration.backupEnabled) {
            logger.debug(marker, "Initializing backup loop")
            job = CoroutineScope(Dispatchers.Default).launch {
                while (isActive) loop()
            }.also { it.start() }
        }
    }

    fun terminate() = logger.debug(marker, "Terminating backup loop").also {
        job.cancel().also { firstLaunch = true }
    }

    private suspend fun loop() {
        if (backupConfiguration.backupEnabled) {
            if (backupConfiguration.firstLaunchDelay) {
                if (firstLaunch) {
                    logger.debug(marker, "Backup loop do first launch delay")
                    delay(backupConfiguration.backupCreationDelay * 1000L)
                    firstLaunch = false
                }
            }

            File(backupConfiguration.backupDirectoryPath).also { it.mkdirs() }.let {
                purge(it).run { rotate(it) }.run { compile(it) }
            }.also { delay(backupConfiguration.backupCreationDelay * 1000L) }
        } else {
            logger.debug(marker, "Backup loop was aborted with configuration").also { terminate() }
        }
    }

    private fun purge(out: File) {
        if (backupConfiguration.purgeBackupOutDirectory) {
            logger.debug(marker, "Purging backup out directory")
            // @formatter:off
            out.listFiles()!!.asSequence().filter {
                it.extension !in backupConfiguration.purgeExtensionsExceptions &&
                it.nameWithoutExtension !in backupConfiguration.purgeNamesExceptions &&
                it.extension != "zip"
            }.forEach { it.deleteRecursively() }
            // @formatter:on
        } else logger.debug(marker, "Purging backup out directory skipped")
    }

    private fun rotate(out: File) {
        logger.debug(marker, "Starting rolling old backup files")
        out.listFiles()!!.asSequence().filter {
            it.extension == "zip"
        }.also { files ->
            if (files.count() >= backupConfiguration.maxBackupFiles) {
                if (backupConfiguration.rollingBackupFilesEnabled) {
                    files.map { it.lastModified() }.sorted().take(
                        files.count() - backupConfiguration.maxBackupFiles
                    ).forEach { date ->
                        files.filter { it.lastModified() == date }.forEach { it.delete() }
                    }
                } else files.forEach { it.delete() }
            }
        }
    }

    private fun compile(file: File) {
        runBlocking {
            ServerThreadWorker.execute(
                Runnable {
                    getCurrentServer().let {
                        it.playerList.saveAllPlayerData()
                        it.save(false, true, true)
                        if (backupConfiguration.backupConfigurations) ConfigurationAPI.saveAll()
                    }
                }
            ) {
                val path = outPath(file).also { path -> logger.debug("Saving backup to $path") }
                val inPath = inPath()
                measureTimeMillis {
                    ZipFile(path).addFolder(File(inPath), ZipParameters().apply {
                        compressionLevel = CompressionLevel.values().find { lvl ->
                            lvl.level == backupConfiguration.backupCompressionLevel
                        }
                        isIncludeRootFolder = true
                    })
                    if (backupConfiguration.backupConfigurations) {
                        ZipFile(path).addFolder(File("config"), ZipParameters().apply {
                            compressionLevel = CompressionLevel.values().find { lvl ->
                                lvl.level == backupConfiguration.backupCompressionLevel
                            }
                            isIncludeRootFolder = true
                        })
                    }
                }.also { time -> logger.debug("Backup saved to $path for ${time * 0.001} seconds") }
                notifyPlayer()
            }
        }
    }

    private fun inPath(): String {
        var path = String.empty
        DistExecutor.runWhenOn(Dist.CLIENT) {
            Runnable { path = "saves${File.separator}${getCurrentServer().folderName}" }
        }
        DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) {
            Runnable { path = getCurrentServer().folderName }
        }
        return if (path.isEmpty()) error("Backup `in` path was empty") else path
    }

    private fun outPath(file: File): String {
        val ext = ".zip"
        val dateTime = SimpleDateFormat(backupConfiguration.backupDateFormat).format(Date())
        return file.absolutePath + File.separator + getCurrentServer().folderName + "-" + dateTime + ext
    }

    private fun notifyPlayer() {
        if (!backupConfiguration.notifyPlayersAboutBackup) return
        getCurrentServer().playerList.players.asSequence().filter {
            hasPermission(it, "ess.backup.notify", 4)
        }.forEach { MessagingAPI.sendMessage(it, "${MESSAGE_MODULE_PREFIX}backup.notify") }
    }
}
