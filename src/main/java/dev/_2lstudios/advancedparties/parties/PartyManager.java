package dev._2lstudios.advancedparties.parties;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import dev._2lstudios.advancedparties.messaging.RedisPubSub;
import dev._2lstudios.advancedparties.players.PartyPlayerManager;
import dev._2lstudios.advancedparties.players.PartyPlayerRepository;
import org.bukkit.configuration.Configuration;

import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class PartyManager {
    private PartyPlayerRepository playerRepository;
    private PartyRepository partyRepository;
    private PartyPlayerManager playerManager;
    private RedisPubSub pubSub;
    private Configuration config;

    private LoadingCache<String, Party> cache;

    public PartyManager(
            PartyPlayerRepository playerRepository,
            PartyRepository partyRepository,
            PartyPlayerManager playerManager,
            RedisPubSub pubSub,
            Configuration config
    ) {
        this.playerRepository = playerRepository;
        this.partyRepository = partyRepository;
        this.playerManager = playerManager;
        this.pubSub = pubSub;
        this.config = config;
        this.cache = CacheBuilder.newBuilder()
                .expireAfterAccess(config.getInt("cache.time-after-read"), TimeUnit.SECONDS)
                .expireAfterWrite(config.getInt("cache.time-after-write"), TimeUnit.SECONDS)
                .build(new CacheLoader<String, Party>() {
                    @Override
                    public Party load(final String id) {
                        return lookupParty(id);
                    }
                });
    }

    public Party lookupParty(String id) {
        PartyData data = partyRepository.findDataByID(id);
        if (data == null) {
            return null;
        }
        return constructPartyWithData(data);
    }

    public Party createParty(PartyMember owner) {
        PartyData data = new PartyData(UUID.randomUUID().toString(), owner);
        data.members = new ArrayList<>();
        data.members.add(owner);
        data.open = false;
        data.maxMembers = config.getInt("parties.max-members");
        partyRepository.savePartyData(data);

        Party party = constructPartyWithData(data);
        this.cache.put(party.getID(), party);
        return party;
    }

    public Party getParty(String id) {
        try {
            return this.cache.get(id);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Party getPartyIfCached(String id) {
        return this.cache.getIfPresent(id);
    }

    public void delete(String id) {
        this.cache.invalidate(id);
    }

    public Party getPartyByLeader(String leaderName) {
        PartyData data = partyRepository.findDataByOwnerName(leaderName);

        if (data == null)
            return null;

        return constructPartyWithData(data);
    }

    private Party constructPartyWithData(PartyData data) {
        return new Party(
                this.playerRepository,
                this.partyRepository,
                this.playerManager,
                this.pubSub,
                data
        );
    }
}


