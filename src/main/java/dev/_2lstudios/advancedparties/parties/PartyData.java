package dev._2lstudios.advancedparties.parties;


import com.sammwy.classserializer.annotations.Prop;
import com.sammwy.milkshake.Entity;

import java.util.List;

public class PartyData extends Entity {
    @Prop
    public PartyMember leader;

    @Prop
    public List<PartyMember> members;

    @Prop
    public boolean open;
}
