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
import java.io.File

object BackupCommand : CommandBase(backupLiteral, false) {
    override val name = "configure-home"

    private val backupConfiguration by lazy {
        getConfigurationByName<BackupConfiguration>("backup").take()
    }

    fun now(context: CommandContext<CommandSource>) = 0.also {
        validateAndExecute(context, "ess.backup.now", 4) { isServer ->
            File(backupConfiguration.backupDirectoryPath).also { it.mkdirs() }.let {
                with(BackupManager) { purge(it).run { rotate(it) }.run { compile(it) } }
            }.run {
                if (isServer) {
                    ServerMessagingAPI.response { "Backup created successfully" }
                } else {
                    MessagingAPI.sendMessage(
                        context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}backup.now.success"
                    )
                }
            }
        }
    }
}
