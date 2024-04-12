package dev._2lstudios.advancedparties.parties;

import com.sammwy.milkshake.find.FindFilter;
import dev._2lstudios.advancedparties.AdvancedParties;
import dev._2lstudios.advancedparties.messaging.packets.*;
import dev._2lstudios.advancedparties.players.PartyPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Party {
    private AdvancedParties plugin;
    private PartyData data;

    public Party(AdvancedParties plugin, PartyData data) {
        this.plugin = plugin;
        this.data = data;
    }

    public void disband(PartyDisbandReason reason) {
        PartyDisbandPacket packet = new PartyDisbandPacket(this.getID(), reason);

        this.plugin.getPlayerRepository().deleteMany(new FindFilter("party", this.getID()));
        this.data.delete();
        this.plugin.getPubSub().publish(packet);
    }

    public boolean hasMember(UUID uuid) {
        return this.data.members.stream().anyMatch(member -> member.uuid.equals(uuid));
    }

    public boolean hasMember(PartyPlayer player) {
        return this.hasMember(player.getBukkitPlayer().getUniqueId());
    }

    public PartyMember removeMember(PartyMember member) {
        return this.removeMember(member.uuid);
    }

    public PartyMember removeMember(UUID uuid) {
        Iterator<PartyMember> iterator = this.data.members.iterator();
        PartyMember member = null;

        while (iterator.hasNext()) {
            member = iterator.next();

            if (member.uuid.equals(uuid)) {
                iterator.remove();
                break;
            }
        }

        this.data.save();
        return member;
    }

    public PartyMember removeMember(PartyPlayer player) {
        return this.removeMember(player.getBukkitPlayer().getUniqueId());
    }

    public void addMember(PartyMember partyMember) {
        this.data.members.add(partyMember);
        this.data.save();
    }

    public void addMember(PartyPlayer player) {
        this.addMember(PartyMember.fromPartyPlayer(player));
    }

    public String getID() {
        return this.data.getID();
    }

    public PartyMember getLeader() {
        return this.data.leader;
    }

    public void setLeader(PartyMember member) {
        this.data.leader = member;
        this.data.save();
    }

    public PartyMember getMemberByUUID(UUID uuid) {
        return this.data.members.stream().filter(member -> member.uuid.equals(uuid)).findFirst().orElse(null);
    }

    public PartyMember getMemberByName(String name) {
        return this.data.members.stream().filter(member -> member.name.equals(name)).findFirst().orElse(null);
    }

    public List<PartyMember> getMembers() {
        return this.data.members;
    }

    public String getMembersAsString() {
        StringBuilder result = new StringBuilder();

        for (PartyMember member : this.getMembers()) {
            if (!result.toString().isEmpty())
                result.append(", ");

            result.append(member.getName());
        }

        return result.toString();
    }

    public List<PartyPlayer> getPlayers() {
        List<PartyPlayer> result = new ArrayList<>();

        for (PartyMember member : this.data.members) {
            Player bukkitPlayer = member.asBukkitPlayer();
            if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
                result.add(this.plugin.getPlayerManager().getPlayer(bukkitPlayer));
            }
        }

        return result;
    }

    public boolean isLeader(PartyMember member) {
        return this.data.leader.equals(member);
    }

    public boolean isLeader(PartyPlayer player) {
        return this.isLeader(PartyMember.fromPartyPlayer(player));
    }

    public void announcePlayerJoin(String playerName) {
        this.plugin.getPubSub().publish(new PartyJoinPacket(playerName, this.getID()));
    }

    public void announcePlayerLeave(String playerName) {
        this.plugin.getPubSub().publish(new PartyLeavePacket(playerName, this.getID()));
    }

    public void announcePlayerPromoted(String playerName) {
        this.plugin.getPubSub().publish(new PartyPromotePacket(playerName, this.getID()));
    }

    public int getMembersCount() {
        return this.getMembers().size();
    }

    public int getMaxMembers() {
        return this.plugin.getConfig().getInt("parties.max-members");
    }

    public boolean isMaxMembersReached() {
        return this.getMembersCount() >= this.getMaxMembers();
    }

    public boolean isOpen() {
        return this.data.open;
    }

    public void setOpen(boolean b) {
        this.data.open = b;
        this.data.save();
    }

    public void sendPartyUpdate() {
        this.plugin.getPubSub().publish(new PartyUpdatePacket(this.getID()));
    }

    public void sync() {
        this.data.refresh();
    }

    public void sendToServer(String server) {
        this.plugin.getPubSub().publish(new PartySendPacket(this.getID(), server));
    }
}
