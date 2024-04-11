package dev._2lstudios.advancedparties.players;

import com.sammwy.classserializer.annotations.Prop;
import com.sammwy.milkshake.Entity;

public class PartyPlayerData extends Entity {
    @Prop
    public String username;

    @Prop
    public String party;

    @Prop
    public boolean partyChat = false;

    @Prop
    public boolean leader = false;
}
