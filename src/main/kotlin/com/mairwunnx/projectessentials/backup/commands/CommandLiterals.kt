package com.mairwunnx.projectessentials.backup.commands

import com.mojang.brigadier.arguments.BoolArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.builder.LiteralArgumentBuilder.literal
import net.minecraft.command.CommandSource
import net.minecraft.command.Commands

inline val backupLiteral: LiteralArgumentBuilder<CommandSource>
    get() = literal<CommandSource>("backup").then(
        Commands.literal("now").executes { BackupCommand.now(it) }
    ).then(Commands.literal("off").executes { BackupCommand.off(it) }
    ).then(Commands.literal("on").executes { BackupCommand.on(it) })

inline val configureBackupLiteral: LiteralArgumentBuilder<CommandSource>
    get() = literal<CommandSource>("configure-backup")
        .then(
            Commands.literal("backup-enabled").then(
                Commands.literal("set").then(
                    Commands.argument("value", BoolArgumentType.bool()).executes {
                        ConfigureBackupCommand.backupEnabled(it)
                    }
                )
            )
        ).then(
            Commands.literal("first-launch-delay").then(
                Commands.literal("set").then(
                    Commands.argument("value", BoolArgumentType.bool()).executes {
                        ConfigureBackupCommand.firstLaunchDelay(it)
                    }
                )
            )
        ).then(
            Commands.literal("backup-configurations").then(
                Commands.literal("set").then(
                    Commands.argument("value", BoolArgumentType.bool()).executes {
                        ConfigureBackupCommand.backupConfigurations(it)
                    }
                )
            )
        ).then(
            Commands.literal("backup-creation-delay").then(
                Commands.literal("set").then(
                    Commands.argument("value", IntegerArgumentType.integer(30)).executes {
                        ConfigureBackupCommand.backupCreationDelay(it)
                    }
                )
            )
        ).then(
            Commands.literal("backup-compression-level").then(
                Commands.literal("set").then(
                    Commands.argument("value", IntegerArgumentType.integer(1, 9)).executes {
                        ConfigureBackupCommand.backupCompressionLevel(it)
                    }
                )
            )
        ).then(
            Commands.literal("max-backup-files").then(
                Commands.literal("set").then(
                    Commands.argument("value", IntegerArgumentType.integer(1)).executes {
                        ConfigureBackupCommand.maxBackupFiles(it)
                    }
                )
            )
        ).then(
            Commands.literal("rolling-backup-files-enabled").then(
                Commands.literal("set").then(
                    Commands.argument("value", BoolArgumentType.bool()).executes {
                        ConfigureBackupCommand.rollingBackupFilesEnabled(it)
                    }
                )
            )
        ).then(
            Commands.literal("notify-players-about-backup").then(
                Commands.literal("set").then(
                    Commands.argument("value", BoolArgumentType.bool()).executes {
                        ConfigureBackupCommand.notifyPlayersAboutBackup(it)
                    }
                )
            )
        ).then(
            Commands.literal("purge-backup-out-directory").then(
                Commands.literal("set").then(
                    Commands.argument("value", BoolArgumentType.bool()).executes {
                        ConfigureBackupCommand.purgeBackupOutDirectory(it)
                    }
                )
            )
        )
