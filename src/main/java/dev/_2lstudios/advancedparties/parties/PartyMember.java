package dev._2lstudios.advancedparties.parties;

import com.sammwy.classserializer.annotations.Prop;
import com.sammwy.milkshake.Entity;
import dev._2lstudios.advancedparties.players.PartyPlayer;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class PartyMember extends Entity {
    @Prop
    public String name;

    @Prop
    public UUID uuid;

    public Player asBukkitPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public boolean equals(Object o) {
        if (o == this)
            return true;

        return o instanceof PartyMember && (( PartyMember ) o).uuid.equals(uuid);
    }

    public static PartyMember fromPlayer(Player player) {
        PartyMember member = new PartyMember();
        member.name = player.getName();
        member.uuid = player.getUniqueId();
        return member;
    }

    public static PartyMember fromPartyPlayer(PartyPlayer player) {
        return fromPlayer(player.getBukkitPlayer());
    }

    public static PartyMember of(UUID uuid, String name) {
        PartyMember member = new PartyMember();
        member.name = name;
        member.uuid = uuid;
        return member;
    }
}
