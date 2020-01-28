package com.mairwunnx.projectessentials.backup

import com.mairwunnx.projectessentials.backup.configuration.BackupConfigurationController
import kotlinx.coroutines.*
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.DistExecutor
import org.apache.logging.log4j.LogManager
import org.zeroturnaround.zip.ZipUtil
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object BackupController {
    private val logger = LogManager.getLogger()
    private lateinit var job: Job
    var isFirstLaunch = true

    fun launchBackupLoop() {
        job = GlobalScope.launch {
            while (isActive) {
                backupLoop()
            }
        }
        job.start()
    }

    fun abortBackupLoop() = job.cancel()

    private suspend fun backupLoop() {
        val configuration = BackupConfigurationController.get()

        if (configuration.backupEnabled) {
            if (configuration.firstLaunchDelay) {
                if (isFirstLaunch) {
                    delay(configuration.backupCreationDelay)
                    isFirstLaunch = false
                }
            }

            val backupDirectory = File(configuration.backupDirectoryPath)
            backupDirectory.mkdirs()

            val fileList = backupDirectory.listFiles() ?: emptyArray()
            removeExtraFiles(fileList)

            if (fileList.count() >= configuration.maxBackupFiles) {
                if (configuration.rollingBackupFilesEnabled) {
                    doRollingFiles(fileList)
                } else {
                    fileList.forEach {
                        if (it.isFile && !it.isDirectory) {
                            it.delete()
                        }
                    }
                }
            }

            DistExecutor.runWhenOn(Dist.CLIENT) {
                Runnable {
                    makeBackup("saves${File.separator}${EntryPoint.serverInstance.worldName}")
                }
            }

            DistExecutor.runWhenOn(Dist.DEDICATED_SERVER) {
                Runnable {
                    makeBackup(EntryPoint.serverInstance.folderName)
                }
            }

            delay(configuration.backupCreationDelay)
        } else {
            abortBackupLoop()
        }
    }

    private fun removeExtraFiles(files: Array<File>) {
        if (!BackupConfigurationController.get().removeExtraFiles) return

        files.forEach {
            if (it.isDirectory || it.extension != "zip") {
                it.delete()
            }
        }
    }

    private fun doRollingFiles(fileList: Array<File>) {
        val lastModifiedDates = mutableListOf<Long>()
        fileList.forEach {
            if (it.isFile && !it.isDirectory) {
                lastModifiedDates.add(it.lastModified())
            }
        }

        val lastModified = lastModifiedDates.min()
        if (lastModified != null) {
            fileList.forEach {
                if (it.isFile && !it.isDirectory) {
                    if (it.lastModified() == lastModified) {
                        it.delete()
                    }
                }
            }
        }
    }

    private fun makeBackup(path: String) {
        val configuration = BackupConfigurationController.get()

        runBlocking {
            val savingJob = launch(Dispatchers.Default) {
                EntryPoint.serverInstance.save(false, false, true)
            }
            savingJob.invokeOnCompletion {
                val savingPath = buildFilePathName(path)
                logger.info("Saving backup as $savingPath")

                ZipUtil.pack(
                    File(path),
                    File(buildFilePathName(path)),
                    configuration.backupCompressionLevel
                )
            }

            savingJob.start()
        }
    }

    private fun buildFilePathName(path: String): String {
        val configuration = BackupConfigurationController.get()
        val dateFormat = SimpleDateFormat(configuration.backupDateFormat)
        val extension = ".zip"

        val backupDirectory = configuration.backupDirectoryPath
        val currentDateTime = dateFormat.format(Date())

        return if (path.contains(File.separator)) {
            val worldName = path.split(File.separator)[1]
            backupDirectory + File.separator + worldName + "-" + currentDateTime + extension
        } else {
            backupDirectory + File.separator + path + "-" + currentDateTime + extension
        }
    }
}
