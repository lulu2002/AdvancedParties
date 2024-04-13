package dev._2lstudios.advancedparties.messaging.data;

import dev._2lstudios.advancedparties.parties.PartyMember;
import lombok.Data;

import java.util.UUID;

@Data
public class PartyMemberMessage {
    private String name;
    private String uuid;

    public PartyMember toMember() {
        return PartyMember.of(UUID.fromString(uuid), name);
    }

    public static PartyMemberMessage fromMember(final PartyMember member) {
        final PartyMemberMessage partyMemberMessage = new PartyMemberMessage();
        partyMemberMessage.setName(member.getName());
        partyMemberMessage.setUuid(member.getUuid().toString());
        return partyMemberMessage;
    }
}
