package com.mairwunnx.projectessentials.backup.commands

import com.mairwunnx.projectessentials.backup.configurations.BackupConfiguration
import com.mairwunnx.projectessentials.backup.helpers.validateAndExecute
import com.mairwunnx.projectessentials.backup.managers.BackupManager
import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.commands.CommandBase
import com.mairwunnx.projectessentials.core.api.v1.configuration.ConfigurationAPI.getConfigurationByName
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.messaging.ServerMessagingAPI
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource
import net.minecraft.entity.player.ServerPlayerEntity
import java.io.File

object BackupCommand : CommandBase(backupLiteral, false) {
    override val name = "backup"

    private val backupConfiguration by lazy {
        getConfigurationByName<BackupConfiguration>("backup").take()
    }

    private fun out(player: ServerPlayerEntity, result: String) = MessagingAPI.sendMessage(
        player, "${MESSAGE_MODULE_PREFIX}backup.$result.success"
    )

    fun now(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.backup.now", 4) { isServer ->
            File(backupConfiguration.backupDirectoryPath).also { it.mkdirs() }.let {
                with(BackupManager) { purge(it).run { rotate(it) }.run { compile(it) } }
            }.run {
                if (isServer) {
                    ServerMessagingAPI.response { "Backup created successfully." }
                } else out(context.getPlayer()!!, "now")
            }
        }
    }

    fun off(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.backup.off", 4) { isServer ->
            BackupManager.shutdown().run {
                if (isServer) {
                    ServerMessagingAPI.response { "Backup cycle was stopped." }
                } else out(context.getPlayer()!!, "off")
            }
        }
    }

    fun on(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.backup.on", 4) { isServer ->
            BackupManager.shutdown().run {
                if (isServer) {
                    ServerMessagingAPI.response { "Backup cycle was launched." }
                } else out(context.getPlayer()!!, "on")
            }
        }
    }
}
