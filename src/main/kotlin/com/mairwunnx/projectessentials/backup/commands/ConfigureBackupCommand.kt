package com.mairwunnx.projectessentials.backup.commands

import com.mairwunnx.projectessentials.backup.configurations.BackupConfiguration
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_CORE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandAPI
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.extensions.isPlayerSender
import com.mairwunnx.projectessentials.core.api.v1.extensions.playerName
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mairwunnx.projectessentials.core.impl.commands.ConfigureEssentialsCommandAPI
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import org.apache.logging.log4j.LogManager

object ConfigureBackupCommand : CommandBase(configureBackupLiteral, false) {
    override val name = "configure-backup"

    private val backupConfiguration by lazy {
        getConfigurationByName<BackupConfiguration>("backup").take()
    }

    init {
        ConfigureEssentialsCommandAPI.required("backup-enabled")
        ConfigureEssentialsCommandAPI.required("first-launch-delay")
    }

    fun backupEnabled(context: CommandContext<CommandSource>) = 0.also {
        val setting = "backup-enabled"
        validate(context, "ess.configure.backup.$setting", setting) {
            val value = CommandAPI.getBool(context, "value")
            val oldValue = backupConfiguration.backupEnabled
            backupConfiguration.backupEnabled = value
            changed(
                context, setting, oldValue.toString(), value.toString()
            ).also { super.process(context) }
        }
    }

    fun firstLaunchDelay(context: CommandContext<CommandSource>) = 0.also {
        val setting = "first-launch-delay"
        validate(context, "ess.configure.backup.$setting", setting) {
            val value = CommandAPI.getBool(context, "value")
            val oldValue = backupConfiguration.firstLaunchDelay
            backupConfiguration.firstLaunchDelay = value
            changed(
                context, setting, oldValue.toString(), value.toString()
            ).also { super.process(context) }
        }
    }

    fun backupConfigurations(context: CommandContext<CommandSource>) = 0.also {
        val setting = "backup-configurations"
        validate(context, "ess.configure.backup.$setting", setting) {
            val value = CommandAPI.getBool(context, "value")
            val oldValue = backupConfiguration.backupConfigurations
            backupConfiguration.backupConfigurations = value
            changed(
                context, setting, oldValue.toString(), value.toString()
            ).also { super.process(context) }
        }
    }

    fun backupCreationDelay(context: CommandContext<CommandSource>) = 0.also {
        val setting = "backup-creation-delay"
        validate(context, "ess.configure.backup.$setting", setting) {
            val value = CommandAPI.getInt(context, "value")
            val oldValue = backupConfiguration.backupCreationDelay
            backupConfiguration.backupCreationDelay = value
            changed(
                context, setting, oldValue.toString(), value.toString()
            ).also { super.process(context) }
        }
    }

    fun backupCompressionLevel(context: CommandContext<CommandSource>) = 0.also {
        val setting = "backup-compression-level"
        validate(context, "ess.configure.backup.$setting", setting) {
            val value = CommandAPI.getInt(context, "value")
            val oldValue = backupConfiguration.backupCompressionLevel
            backupConfiguration.backupCompressionLevel = value
            changed(
                context, setting, oldValue.toString(), value.toString()
            ).also { super.process(context) }
        }
    }

    fun maxBackupFiles(context: CommandContext<CommandSource>) = 0.also {
        val setting = "max-backup-files"
        validate(context, "ess.configure.backup.$setting", setting) {
            val value = CommandAPI.getInt(context, "value")
            val oldValue = backupConfiguration.maxBackupFiles
            backupConfiguration.maxBackupFiles = value
            changed(
                context, setting, oldValue.toString(), value.toString()
            ).also { super.process(context) }
        }
    }

    fun rollingBackupFilesEnabled(context: CommandContext<CommandSource>) = 0.also {
        val setting = "rolling-backup-files-enabled"
        validate(context, "ess.configure.backup.$setting", setting) {
            val value = CommandAPI.getBool(context, "value")
            val oldValue = backupConfiguration.rollingBackupFilesEnabled
            backupConfiguration.rollingBackupFilesEnabled = value
            changed(
                context, setting, oldValue.toString(), value.toString()
            ).also { super.process(context) }
        }
    }

    fun notifyPlayersAboutBackup(context: CommandContext<CommandSource>) = 0.also {
        val setting = "notify-players-about-backup"
        validate(context, "ess.configure.backup.$setting", setting) {
            val value = CommandAPI.getBool(context, "value")
            val oldValue = backupConfiguration.notifyPlayersAboutBackup
            backupConfiguration.notifyPlayersAboutBackup = value
            changed(
                context, setting, oldValue.toString(), value.toString()
            ).also { super.process(context) }
        }
    }

    fun purgeBackupOutDirectory(context: CommandContext<CommandSource>) = 0.also {
        val setting = "purge-backup-out-directory"
        validate(context, "ess.configure.backup.$setting", setting) {
            val value = CommandAPI.getBool(context, "value")
            val oldValue = backupConfiguration.purgeBackupOutDirectory
            backupConfiguration.purgeBackupOutDirectory = value
            changed(
                context, setting, oldValue.toString(), value.toString()
            ).also { super.process(context) }
        }
    }

    private fun validate(
        context: CommandContext<CommandSource>,
        node: String,
        setting: String,
        action: (isServer: Boolean) -> Unit
    ) = context.getPlayer()?.let {
        if (hasPermission(it, node, 4)) {
            action(false)
        } else {
            MessagingAPI.sendMessage(
                context.getPlayer()!!,
                "$MESSAGE_CORE_PREFIX.configure.restricted",
                args = *arrayOf(setting)
            )
        }
    } ?: run { action(true) }

    private fun changed(
        context: CommandContext<CommandSource>,
        setting: String,
        oldValue: String,
        value: String
    ) = if (context.isPlayerSender()) {
        LogManager.getLogger().info(
            "Setting name `$setting` value changed by ${context.playerName()} from `$oldValue` to $value"
        )
        MessagingAPI.sendMessage(
            context.getPlayer()!!,
            "$MESSAGE_CORE_PREFIX.configure.successfully",
            args = *arrayOf(setting, oldValue, value)
        )
    } else {
        ServerMessagingAPI.response {
            "Setting name `$setting` value changed from `$oldValue` to $value"
        }
    }
}
