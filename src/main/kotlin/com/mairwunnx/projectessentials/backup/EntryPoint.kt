package com.mairwunnx.projectessentials.backup

import com.mairwunnx.projectessentials.backup.configuration.BackupConfigurationController
import com.mairwunnx.projectessentials.core.EssBase
import net.minecraft.server.MinecraftServer
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent
import org.apache.logging.log4j.LogManager

@Suppress("unused")
@Mod("project_essentials_backup")
class EntryPoint : EssBase() {
    private val logger = LogManager.getLogger()

    init {
        modVersion = "1.15.2-1.0.0"
        logBaseInfo()
        validateForgeVersion()
        MinecraftForge.EVENT_BUS.register(this)
        BackupConfigurationController.load()
    }

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun onServerStarting(event: FMLServerStartingEvent) {
        serverInstance = event.server
        BackupController.isFirstLaunch = true
        BackupController.launchBackupLoop()
    }

    @SubscribeEvent
    @Suppress("UNUSED_PARAMETER")
    fun onServerStopping(it: FMLServerStoppingEvent) {
        BackupController.abortBackupLoop()
        BackupConfigurationController.save()
    }

    companion object {
        lateinit var serverInstance: MinecraftServer
    }
}
