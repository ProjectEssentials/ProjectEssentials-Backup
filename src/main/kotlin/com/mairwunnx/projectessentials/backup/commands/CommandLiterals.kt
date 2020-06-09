package com.mairwunnx.projectessentials.backup.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands

inline val backupLiteral: LiteralArgumentBuilder<CommandSource>
    get() = literal<CommandSource>("backup").then(
        Commands.literal("now").executes { BackupCommand.now(it) }
    ).then(Commands.literal("off").executes { BackupCommand.off(it) }
    ).then(Commands.literal("on").executes { BackupCommand.on(it) })
