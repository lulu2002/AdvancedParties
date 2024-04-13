package dev._2lstudios.advancedparties.parties;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public class PartyData {
    @Getter
    private String id;
    public PartyMember leader;
    public List<PartyMember> members;
    public int maxMembers;
    public boolean open = false;

    public PartyData(String id, PartyMember leader) {
        this.id = id;
        this.leader = leader;
    }
}
