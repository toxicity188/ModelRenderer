package kr.toxicity.model.manager

import dev.jorel.commandapi.CommandAPI
import dev.jorel.commandapi.CommandAPIBukkitConfig
import dev.jorel.commandapi.CommandAPICommand
import dev.jorel.commandapi.executors.CommandExecutionInfo
import dev.jorel.commandapi.executors.PlayerCommandExecutor
import kr.toxicity.model.api.ModelRenderer.ReloadResult.Failure
import kr.toxicity.model.api.ModelRenderer.ReloadResult.OnReload
import kr.toxicity.model.api.ModelRenderer.ReloadResult.Success
import kr.toxicity.model.api.manager.CommandManager
import kr.toxicity.model.util.PLUGIN
import org.bukkit.entity.EntityType

object CommandManagerImpl : CommandManager, GlobalManagerImpl {
    override fun start() {
        CommandAPI.onLoad(CommandAPIBukkitConfig(PLUGIN).silentLogs(true))
        CommandAPICommand("modelrenderer")
            .withAliases("mr")
            .withPermission("modelrenderer")
            .withSubcommands(
                CommandAPICommand("orc")
                    .withAliases("o")
                    .withPermission("modelrenderer.orc")
                    .executesPlayer(PlayerCommandExecutor { player, _ ->
                        val warrior = ModelManagerImpl.renderer("orc_warrior")!!
                        warrior.create(player.world.spawnEntity(player.location, EntityType.HUSK)).spawn(player)
                        val archer = ModelManagerImpl.renderer("orc_archer")!!
                        archer.create(player.world.spawnEntity(player.location, EntityType.SKELETON)).spawn(player)
                    }),
                CommandAPICommand("reload")
                    .withAliases("re", "rl")
                    .withPermission("modelrenderer.reload")
                    .executes(CommandExecutionInfo {
                        when (val result = PLUGIN.reload()) {
                            is OnReload -> it.sender().sendMessage("The plugin still on reload!")
                            is Success -> it.sender().sendMessage("Reload completed (${result.time} time)")
                            is Failure -> {
                                it.sender().sendMessage("Reload failed.")
                                it.sender().sendMessage("Reason: ${result.throwable.message ?: result.throwable.javaClass.simpleName}")
                            }
                        }
                    }),
            )
            .executes(CommandExecutionInfo {
                it.sender().sendMessage("/modelrenderer orc - summons test orc.")
            })
            .register(PLUGIN)
    }

    override fun reload() {

    }
}