package dev._2lstudios.advancedparties.messaging.data;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class PartyMemberMessageData {
    private String name;
    private String uuid;
}
