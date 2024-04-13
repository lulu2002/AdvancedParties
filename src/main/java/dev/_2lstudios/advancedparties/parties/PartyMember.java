package dev._2lstudios.advancedparties.parties;

import dev._2lstudios.advancedparties.players.PartyPlayer;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class PartyMember {
    private final String name;
    private final UUID uuid;

    public Player asBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;

        return o instanceof PartyMember && (( PartyMember ) o).uuid.equals(uuid);
    }

    public static PartyMember fromPlayer(Player player) {
        return new PartyMember(player.getName(), player.getUniqueId());
    }

    public static PartyMember fromPartyPlayer(PartyPlayer player) {
        return fromPlayer(player.getBukkitPlayer());
    }

    public static PartyMember of(UUID uuid, String name) {
        return new PartyMember(name, uuid);
    }
}
