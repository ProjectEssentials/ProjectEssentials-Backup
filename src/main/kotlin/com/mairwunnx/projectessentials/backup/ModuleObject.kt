@file:Suppress("unused", "UNUSED_PARAMETER")

package com.mairwunnx.projectessentials.backup

import com.mairwunnx.projectessentials.backup.commands.BackupCommand
import com.mairwunnx.projectessentials.backup.commands.ConfigureBackupCommand
import com.mairwunnx.projectessentials.backup.configurations.BackupConfiguration
import com.mairwunnx.projectessentials.backup.managers.BackupManager
import com.mairwunnx.projectessentials.core.api.v1.localization.LocalizationAPI
import com.mairwunnx.projectessentials.core.api.v1.module.IModule
import com.mairwunnx.projectessentials.core.api.v1.providers.ProviderAPI
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent

@Mod("project_essentials_backup")
class ModuleObject : IModule {
    override val name = this::class.java.`package`.implementationTitle.split(" ").last()
    override val version = this::class.java.`package`.implementationVersion!!
    override val loadIndex = 6
    override fun init() = Unit

    init {
        EVENT_BUS.register(this).also { initProviders().also { initLocalization() } }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onServerStarting(event: FMLServerStartingEvent) = BackupManager.initialize()

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    fun onServerStopping(it: FMLServerStoppingEvent) = BackupManager.terminate()

    private fun initProviders() {
        listOf(
            BackupCommand::class.java,
            ConfigureBackupCommand::class.java,
            BackupConfiguration::class.java,
            ModuleObject::class.java
        ).forEach(ProviderAPI::addProvider)
    }

    private fun initLocalization() {
        LocalizationAPI.apply(this.javaClass) {
            mutableListOf(
                "/assets/projectessentialsbackup/lang/en_us.json",
                "/assets/projectessentialsbackup/lang/ru_ru.json"
            )
        }
    }
}
