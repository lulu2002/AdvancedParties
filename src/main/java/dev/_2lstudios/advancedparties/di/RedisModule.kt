package dev._2lstudios.advancedparties.di

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import dev._2lstudios.advancedparties.messaging.RedisPubSub

class RedisModule : AbstractModule() {

    @Provides
    @Singleton
    fun redisManager(): RedisPubSub {
        return RedisPubSub()
    }

}