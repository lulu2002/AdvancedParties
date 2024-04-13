package dev._2lstudios.advancedparties.parties;

public interface PartyRepository {
    void removeParty(String id);

    void savePartyData(PartyData data);

    PartyData findDataByID(String id);

    PartyData findDataByOwnerName(String ownerName);
}
