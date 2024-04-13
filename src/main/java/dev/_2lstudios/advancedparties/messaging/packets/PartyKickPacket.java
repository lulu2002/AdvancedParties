package dev._2lstudios.advancedparties.messaging.packets;

import dev._2lstudios.advancedparties.messaging.RedisChannel;
import dev._2lstudios.advancedparties.messaging.data.PartyMemberMessage;

public class PartyKickPacket implements Packet {
  private String party;
  private PartyMemberMessage target;
  
  public PartyKickPacket(String party, PartyMemberMessage target) {
    this.party = party;
    this.target = target;
  }

  public String getTargetName() {
    return this.target.getName();
  }

  public String getPartyID() {
    return this.party;
  }

  @Override
  public String getChannel() {
    return RedisChannel.PARTY_KICK;
  }
}
