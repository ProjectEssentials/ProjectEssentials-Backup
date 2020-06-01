@file:Suppress("unused", "UNUSED_PARAMETER")

package com.mairwunnx.projectessentials.backup

import com.mairwunnx.projectessentials.backup.configurations.BackupConfiguration
import com.mairwunnx.projectessentials.backup.managers.BackupManager
import com.mairwunnx.projectessentials.core.api.v1.IMCLocalizationMessage
import com.mairwunnx.projectessentials.core.api.v1.IMCProvidersMessage
import com.mairwunnx.projectessentials.core.api.v1.events.ModuleEventAPI.subscribeOn
import com.mairwunnx.projectessentials.core.api.v1.events.forge.ForgeEventType
import com.mairwunnx.projectessentials.core.api.v1.events.forge.InterModEnqueueEventData
import com.mairwunnx.projectessentials.core.api.v1.module.IModule
import net.minecraftforge.common.MinecraftForge.EVENT_BUS
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.InterModComms
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.server.FMLServerStartingEvent
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent

@Mod("project_essentials_backup")
class ModuleObject : IModule {
    override val name = this::class.java.`package`.implementationTitle.split(" ").last()
    override val version = this::class.java.`package`.implementationVersion!!
    override val loadIndex = 4
    override fun init() = Unit

    init {
        EVENT_BUS.register(this)
        subscribeOn<InterModEnqueueEventData>(
            ForgeEventType.EnqueueIMCEvent
        ) {
            sendLocalizationRequest()
            sendProvidersRequest()
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    fun onServerStarting(event: FMLServerStartingEvent) {
        BackupManager::initialize
    }

    @SubscribeEvent
    fun onServerStopping(it: FMLServerStoppingEvent) {
        BackupManager::terminate
    }

    private fun sendLocalizationRequest() {
        InterModComms.sendTo(
            "project_essentials_core",
            IMCLocalizationMessage
        ) {
            fun() = mutableListOf<String>(
//                "/assets/projectessentialsbackup/lang/en_us.json",
//                "/assets/projectessentialsbackup/lang/ru_ru.json"
            )
        }
    }

    private fun sendProvidersRequest() {
        InterModComms.sendTo(
            "project_essentials_core",
            IMCProvidersMessage
        ) {
            fun() = listOf(
                BackupConfiguration::class.java,
                ModuleObject::class.java
            )
        }
    }
}
