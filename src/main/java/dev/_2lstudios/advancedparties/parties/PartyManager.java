package dev._2lstudios.advancedparties.parties;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import dev._2lstudios.advancedparties.AdvancedParties;

public class PartyManager {
    private AdvancedParties plugin;
    private LoadingCache<String, Party> cache;

    public PartyManager(AdvancedParties plugin) {
        this.plugin = plugin;
        if (plugin.getConfig().getBoolean("cache.enabled")) {
            this.cache = CacheBuilder.newBuilder()
                .expireAfterAccess(plugin.getConfig().getInt("cache.time-after-read"), TimeUnit.SECONDS)
                .expireAfterWrite(plugin.getConfig().getInt("cache.time-after-write"), TimeUnit.SECONDS)
                .build(new CacheLoader<String, Party>() {
                    @Override
                    public Party load(final String id) throws Exception {
                        return lookupParty(id);
                    }
                });
        }
    }

    public Party lookupParty(String id) {
        PartyData data = this.plugin.getPartyRepository().findByID(id);
        if (data == null) {
            return null;
        }
        return new Party(plugin, data);
    }

    public Party createParty(String leader) {
        PartyData data = new PartyData();
        data.leader = leader;
        data.members = new ArrayList<>();
        data.members.add(leader);
        data.save();

        Party party = new Party(this.plugin, data);
        this.cache.put(party.getID(), party);
        return party;
    }

    public Party getParty(String id) {
        if (this.cache == null) {
            return this.lookupParty(id);
        } else {
            try {
                return this.cache.get(id);
            } catch (ExecutionException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}