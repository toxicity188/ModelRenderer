package kr.toxicity.model.manager

import com.destroystokyo.paper.event.entity.EntityRemoveFromWorldEvent
import kr.toxicity.model.api.manager.EntityManager
import kr.toxicity.model.api.tracker.EntityTracker
import kr.toxicity.model.util.PLUGIN
import kr.toxicity.model.util.registerListener
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityDeathEvent
import java.util.concurrent.TimeUnit

object EntityManagerImpl : EntityManager, GlobalManagerImpl {
    override fun reload() {
        registerListener(object : Listener {
            @EventHandler
            fun EntityRemoveFromWorldEvent.remove() {
                EntityTracker.tracker(entity)?.close()
            }
            @EventHandler
            fun EntityDeathEvent.death() {
                EntityTracker.tracker(entity)?.let {
                    if (!it.animateSingle("death", { true }) {
                            it.close()
                        }) it.close()
                }
            }
            @EventHandler(ignoreCancelled = false)
            fun EntityDamageEvent.damage() {
                val e = entity
                if (e is LivingEntity) EntityTracker.tracker(e)?.let {
                    if (it.animateSingle("damage", { true }) {
                        it.tint(false)
                    }) it.tint(true)
                    else {
                        it.tint(true)
                        Bukkit.getAsyncScheduler().runDelayed(PLUGIN, { _ ->
                            it.tint(false)
                        }, e.maximumNoDamageTicks.toLong() * 25 , TimeUnit.MILLISECONDS)
                    }
                }
            }
        })
    }
}