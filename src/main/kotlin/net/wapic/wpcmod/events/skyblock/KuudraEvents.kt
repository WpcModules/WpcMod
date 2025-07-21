package net.wapic.wpcmod.events.skyblock

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory

object KuudraEvents {

    @JvmField
    val START: Event<Start> = EventFactory.createArrayBacked(Start::class.java) { listeners ->
        Start { ->
            for (listener in listeners) {
                listener.onStart()
            }
        }
    }

    @JvmField
    val END: Event<End> = EventFactory.createArrayBacked(End::class.java) { listeners ->
        End { ->
            for (listener in listeners) {
                listener.onEnd()
            }
        }
    }

    fun interface Start {
        fun onStart()
    }

    fun interface End {
        fun onEnd()
    }
}