package dev._2lstudios.advancedparties.parties;

import dev._2lstudios.advancedparties.messaging.RedisPubSub;
import dev._2lstudios.advancedparties.messaging.packets.*;
import dev._2lstudios.advancedparties.players.PartyPlayer;
import dev._2lstudios.advancedparties.players.PartyPlayerManager;
import dev._2lstudios.advancedparties.players.PartyPlayerRepository;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class Party {
    private PartyPlayerRepository playerRepository;
    private PartyRepository partyRepository;
    private PartyPlayerManager playerManager;
    private RedisPubSub pubSub;
    private PartyData data;

    public Party(PartyPlayerRepository playerRepository,
                 PartyRepository partyRepository,
                 PartyPlayerManager playerManager,
                 RedisPubSub pubSub,
                 PartyData data) {
        this.playerRepository = playerRepository;
        this.partyRepository = partyRepository;
        this.playerManager = playerManager;
        this.pubSub = pubSub;
        this.data = data;
    }

    public void disband(PartyDisbandReason reason) {
        PartyDisbandPacket packet = new PartyDisbandPacket(this.getID(), reason);

        this.playerRepository.deletePlayersInParty(this.getID());
        this.partyRepository.removeParty(this.getID());
        this.pubSub.publish(packet);
    }

    public boolean hasMember(UUID uuid) {
        return this.data.members.stream().anyMatch(member -> member.getUuid().equals(uuid));
    }

    public boolean hasMember(PartyPlayer player) {
        return this.hasMember(player.getBukkitPlayer().getUniqueId());
    }

    public PartyMember removeMember(PartyMember member) {
        return this.removeMember(member.getUuid());
    }

    public PartyMember removeMember(UUID uuid) {
        Iterator<PartyMember> iterator = this.data.members.iterator();
        PartyMember member = null;

        while (iterator.hasNext()) {
            member = iterator.next();

            if (member.getUuid().equals(uuid)) {
                iterator.remove();
                break;
            }
        }
        this.savePartyData();
        return member;
    }

    public PartyMember removeMember(PartyPlayer player) {
        return this.removeMember(player.getBukkitPlayer().getUniqueId());
    }

    public void addMember(PartyMember partyMember) {
        this.data.members.add(partyMember);
        this.savePartyData();
    }

    public void addMember(PartyPlayer player) {
        this.addMember(PartyMember.fromPartyPlayer(player));
    }

    public String getID() {
        return this.data.getId();
    }

    public PartyMember getLeader() {
        return this.data.leader;
    }

    public void setLeader(PartyMember member) {
        this.data.leader = member;
        this.partyRepository.savePartyData(this.data);
    }

    public PartyMember getMemberByUUID(UUID uuid) {
        return this.data.members.stream().filter(member -> member.getUuid().equals(uuid)).findFirst().orElse(null);
    }

    public PartyMember getMemberByName(String name) {
        return this.data.members.stream().filter(member -> member.getName().equals(name)).findFirst().orElse(null);
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

    public List<PartyPlayer> getOnlinePlayers() {
        List<PartyPlayer> result = new ArrayList<>();

        for (PartyMember member : this.data.members) {
            Player bukkitPlayer = member.asBukkitPlayer();
            if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
                result.add(this.playerManager.getPlayer(bukkitPlayer));
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
        this.pubSub.publish(new PartyJoinPacket(playerName, this.getID()));
    }

    public void announcePlayerLeave(String playerName) {
        this.pubSub.publish(new PartyLeavePacket(playerName, this.getID()));
    }

    public void announcePlayerPromoted(String playerName) {
        this.pubSub.publish(new PartyPromotePacket(playerName, this.getID()));
    }

    public int getMembersCount() {
        return this.getMembers().size();
    }

    public int getMaxMembers() {
        return this.data.maxMembers;
    }

    public boolean isMaxMembersReached() {
        return this.getMembersCount() >= this.getMaxMembers();
    }

    public boolean isOpen() {
        return this.data.open;
    }

    public void setOpen(boolean b) {
        this.data.open = b;
        this.savePartyData();
    }

    public void sendPartyUpdate() {
        this.pubSub.publish(new PartyUpdatePacket(this.getID()));
    }

    public void sync() {
        this.refreshPartyData();
    }

    public void sendToServer(String server) {
        this.pubSub.publish(new PartySendPacket(this.getID(), server));
    }

    public void savePartyData() {
        this.partyRepository.savePartyData(this.data);
    }

    public void refreshPartyData() {
        this.data = this.partyRepository.findDataByID(this.getID());
    }
}
