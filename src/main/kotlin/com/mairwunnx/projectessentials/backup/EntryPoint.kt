package com.mairwunnx.projectessentials.backup

import com.mairwunnx.projectessentials.core.EssBase
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import org.apache.logging.log4j.LogManager

@Suppress("unused")
@Mod("project_essentials_backup")
class EntryPoint : EssBase() {
    private val logger = LogManager.getLogger()

    init {
        modVersion = "1.14.4-1.0.0"
        logBaseInfo()
        validateForgeVersion()
        MinecraftForge.EVENT_BUS.register(this)
    }
}
