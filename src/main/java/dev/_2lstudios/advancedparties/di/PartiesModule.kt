package dev._2lstudios.advancedparties.di

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.sammwy.milkshake.Repository
import com.walrusone.skywarsreloaded.managers.PlayerManager
import dev._2lstudios.advancedparties.adapters.parties.PartyDataEntity
import dev._2lstudios.advancedparties.adapters.parties.PartyRepositoryImpl
import dev._2lstudios.advancedparties.parties.PartyManager
import dev._2lstudios.advancedparties.players.PartyPlayerManager

class PartiesModule : AbstractModule() {

    @Provides
    @Singleton
    fun manager(): PartyManager {
        return PartyManager()
    }

    @Provides
    @Singleton
    fun partyRepository(repository: Repository<PartyDataEntity>): PartyRepositoryImpl {
        return PartyRepositoryImpl(repository)
    }


}
