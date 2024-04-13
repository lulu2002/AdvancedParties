package dev._2lstudios.advancedparties.di

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import dev._2lstudios.advancedparties.players.PartyPlayerManager

class PlayerModule : AbstractModule() {

    @Provides
    @Singleton
    fun playerManager(): PartyPlayerManager {
        return PartyPlayerManager()
    }


}