package dev._2lstudios.advancedparties.adapters.parties;

import com.sammwy.milkshake.Repository;
import com.sammwy.milkshake.find.FindFilter;
import dev._2lstudios.advancedparties.parties.PartyData;
import dev._2lstudios.advancedparties.parties.PartyRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PartyRepositoryImpl implements PartyRepository {

    private final Repository<PartyDataEntity> partyDataRepository;

    @Override
    public void removeParty(String id) {
        partyDataRepository.deleteMany(new FindFilter("party", id));
    }

    @Override
    public void savePartyData(PartyData data) {
        this.partyDataRepository.save(PartyDataEntity.fromPartyData(data));
    }

    @Override
    public PartyData findDataByID(String id) {
        PartyDataEntity entity = partyDataRepository.findByID(id);
        return entity == null ? null : entity.toPartyData();
    }

    @Override
    public PartyData findDataByOwnerName(String ownerName) {
        PartyDataEntity entity = partyDataRepository.findOne(new FindFilter("leader.name", ownerName));
        return entity == null ? null : entity.toPartyData();
    }
}
