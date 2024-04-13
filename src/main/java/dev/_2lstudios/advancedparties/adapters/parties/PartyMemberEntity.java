package dev._2lstudios.advancedparties.adapters.parties;

import com.sammwy.classserializer.annotations.Prop;
import com.sammwy.milkshake.Entity;
import dev._2lstudios.advancedparties.parties.PartyMember;

import java.util.UUID;

public class PartyMemberEntity extends Entity {
    @Prop
    public String name;

    public PartyMember toPartyMember() {
        return new PartyMember(
                this.name,
                UUID.fromString(this.getID())
        );
    }

    public static PartyMemberEntity fromPartyMember(PartyMember member) {
        PartyMemberEntity partyMemberEntity = new PartyMemberEntity();
        partyMemberEntity.setID(member.getUuid().toString());
        partyMemberEntity.name = member.getName();
        return partyMemberEntity;
    }
}
