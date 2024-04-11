package dev._2lstudios.advancedparties;

import com.sammwy.milkshake.Milkshake;
import com.sammwy.milkshake.Provider;
import com.sammwy.milkshake.Repository;
import dev._2lstudios.advancedparties.api.PartyAPI;
import dev._2lstudios.advancedparties.api.events.PartyEvent;
import dev._2lstudios.advancedparties.cache.CacheEngine;
import dev._2lstudios.advancedparties.cache.impl.RedisCache;
import dev._2lstudios.advancedparties.cache.impl.RedisPoolCache;
import dev._2lstudios.advancedparties.commands.CommandListener;
import dev._2lstudios.advancedparties.commands.impl.PartyCommand;
import dev._2lstudios.advancedparties.config.ConfigManager;
import dev._2lstudios.advancedparties.config.Configuration;
import dev._2lstudios.advancedparties.hooks.HookManager;
import dev._2lstudios.advancedparties.hooks.impl.SkywarsReloadedHook;
import dev._2lstudios.advancedparties.i18n.LanguageManager;
import dev._2lstudios.advancedparties.listeners.AsyncChatListener;
import dev._2lstudios.advancedparties.listeners.PlayerJoinListener;
import dev._2lstudios.advancedparties.listeners.PlayerQuitListener;
import dev._2lstudios.advancedparties.messaging.RedisPubSub;
import dev._2lstudios.advancedparties.parties.PartyData;
import dev._2lstudios.advancedparties.parties.PartyDisbandHandler;
import dev._2lstudios.advancedparties.parties.PartyManager;
import dev._2lstudios.advancedparties.players.PartyPlayerData;
import dev._2lstudios.advancedparties.players.PartyPlayerManager;
import dev._2lstudios.advancedparties.requests.PartyRequestManager;
import dev._2lstudios.advancedparties.tasks.RedisPingTask;
import dev._2lstudios.advancedparties.tasks.ServerPartySyncTask;
import io.github.leonardosnt.bungeechannelapi.BungeeChannelApi;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.charset.Charset;
import java.util.Random;

public class AdvancedParties extends JavaPlugin {
    private ConfigManager configManager;
    private LanguageManager languageManager;
    private HookManager hookManager;
    private PartyDisbandHandler partyDisband;
    private PartyManager partyManager;
    private PartyPlayerManager playerManager;
    private PartyRequestManager requestManager;

    private RedisPubSub pubsub;
    private CacheEngine cache;

    private BungeeChannelApi bungeeChannelApi;

    private Repository<PartyData> partyDataRepository;
    private Repository<PartyPlayerData> playerDataRepository;

    private String tempServerID = null;

    private void addCommand(CommandListener command) {
        command.register(this, false);
    }

    private void addListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    private void registerOutgoingChannel(String channel) {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, channel);
    }

    public boolean callEvent(PartyEvent event) {
        this.getServer().getPluginManager().callEvent(event);
        return !event.isCancelled();
    }

    @Override
    public void onEnable() {
        // Initialize API
        new PartyAPI(this);

        // Register plugin channels.
        this.registerOutgoingChannel("BungeeCord");

        // Instantiate managers.
        this.bungeeChannelApi = new BungeeChannelApi(this);
        this.configManager = new ConfigManager(this);
        this.languageManager = new LanguageManager(this);
        this.hookManager = new HookManager(this);
        this.partyDisband = new PartyDisbandHandler(this);
        this.partyManager = new PartyManager(this);
        this.playerManager = new PartyPlayerManager(this);
        this.requestManager = new PartyRequestManager(this);

        // Connect to redis.
        String uri = this.getConfig().getString("settings.redis-uri");
        boolean useRedisPool = this.getConfig().getBoolean("settings.use-redis-pool");

        this.cache = useRedisPool ? new RedisPoolCache(uri) : new RedisCache(uri);
        this.pubsub = new RedisPubSub(this, uri);

        // Connect to database.
        Provider provider = Milkshake.connect(this.getConfig().getString("settings.mongo-uri"));
        this.partyDataRepository = Milkshake.addRepository(PartyData.class, provider, "Parties");
        this.playerDataRepository = Milkshake.addRepository(PartyPlayerData.class, provider, "PartyPlayers");

        // Load data.
        this.languageManager.loadLanguagesSafe();
        this.playerManager.addAll();

        // Load Hooks.
        if (this.getConfig().getBoolean("hooks.enabled")) {
            this.hookManager.registerHook(new SkywarsReloadedHook());
            this.hookManager.load();
        }

        // Register listeners.
        this.addListener(new AsyncChatListener(this));
        this.addListener(new PlayerJoinListener(this));
        this.addListener(new PlayerQuitListener(this));

        // Register commands.
        this.addCommand(new PartyCommand());

        // Register tasks.
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new RedisPingTask(this), 15, 15);
        this.getServer().getScheduler().runTaskTimerAsynchronously(this, new ServerPartySyncTask(this), 20 * 5, 20 * 5);
    }

    @Override
    public void onDisable() {
        this.pubsub.disconnect();
    }

    // Configuration getters
    public Configuration getConfig() {
        return this.configManager.getConfig("config.yml");
    }

    // Managers getters
    public HookManager getHookManager() {
        return this.hookManager;
    }

    public LanguageManager getLanguageManager() {
        return this.languageManager;
    }

    public PartyDisbandHandler getPartyDisbandHandler() {
        return this.partyDisband;
    }

    public PartyManager getPartyManager() {
        return this.partyManager;
    }

    public PartyPlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public PartyRequestManager getRequestManager() {
        return this.requestManager;
    }

    // Redis getters
    public CacheEngine getCache() {
        return this.cache;
    }

    public RedisPubSub getPubSub() {
        return this.pubsub;
    }

    public BungeeChannelApi getBungeeChannelApi() {
        return bungeeChannelApi;
    }

    // Repository getters
    public Repository<PartyData> getPartyRepository() {
        return this.partyDataRepository;
    }

    public Repository<PartyPlayerData> getPlayerRepository() {
        return this.playerDataRepository;
    }

    // Others getters
    public String getTempServerID() {
        if (this.tempServerID == null) {
            byte[] array = new byte[15];
            new Random().nextBytes(array);
            this.tempServerID = new String(array, Charset.forName("UTF-8"));
        }

        return this.tempServerID;
    }

    public boolean hasPlugin(String pluginName) {
        Plugin plugin = this.getServer().getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }
}
