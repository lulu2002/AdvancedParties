package dev._2lstudios.advancedparties.adapters.parties;


import com.sammwy.classserializer.annotations.Prop;
import com.sammwy.milkshake.Entity;
import dev._2lstudios.advancedparties.parties.PartyData;

import java.util.List;
import java.util.stream.Collectors;

public class PartyDataEntity extends Entity {

    @Prop
    public PartyMemberEntity leader;
    @Prop
    public List<PartyMemberEntity> members;
    @Prop
    public int maxMembers;
    @Prop
    public boolean open = false;


    public PartyData toPartyData() {
        return new PartyData(
                this.getID(),
                this.leader.toPartyMember(),
                this.members.stream().map(PartyMemberEntity::toPartyMember).collect(Collectors.toList()),
                this.maxMembers,
                this.open
        );
    }

    public static PartyDataEntity fromPartyData(PartyData partyData) {
        PartyDataEntity partyDataEntity = new PartyDataEntity();
        partyDataEntity.setID(partyData.getId());
        partyDataEntity.leader = PartyMemberEntity.fromPartyMember(partyData.leader);
        partyDataEntity.members = partyData.members.stream().map(PartyMemberEntity::fromPartyMember).collect(Collectors.toList());
        partyDataEntity.maxMembers = partyData.maxMembers;
        partyDataEntity.open = partyData.open;
        return partyDataEntity;
    }
}
