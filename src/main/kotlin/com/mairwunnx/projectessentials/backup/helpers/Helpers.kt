package com.mairwunnx.projectessentials.backup.helpers

import com.mairwunnx.projectessentials.core.api.v1.MESSAGE_MODULE_PREFIX
import com.mairwunnx.projectessentials.core.api.v1.extensions.getPlayer
import com.mairwunnx.projectessentials.core.api.v1.messaging.MessagingAPI
import com.mairwunnx.projectessentials.core.api.v1.permissions.hasPermission
import com.mojang.brigadier.context.CommandContext
import net.minecraft.command.CommandSource

inline fun validateAndExecute(
    context: CommandContext<CommandSource>,
    node: String,
    opLevel: Int,
    action: (isServer: Boolean) -> Unit
) = context.getPlayer()?.let {
    if (hasPermission(it, node, opLevel)) {
        action(false)
    } else {
        MessagingAPI.sendMessage(
            context.getPlayer()!!, "${MESSAGE_MODULE_PREFIX}backup.restricted"
        )
    }
} ?: run { action(true) }
