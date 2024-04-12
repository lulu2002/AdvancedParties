package dev._2lstudios.advancedparties.commands.impl;

import dev._2lstudios.advancedparties.commands.Command;
import dev._2lstudios.advancedparties.commands.CommandContext;
import dev._2lstudios.advancedparties.commands.CommandListener;
import dev._2lstudios.advancedparties.interfaces.menus.PartyMenu;
import dev._2lstudios.advancedparties.parties.Party;
import dev._2lstudios.advancedparties.players.PartyPlayer;

@Command(
  name = "info"
)
public class PartyInfoCommand extends CommandListener {
    @Override
    public void onExecuteByPlayer(CommandContext ctx) {
        PartyPlayer player = ctx.getPlayer();
        Party party = player.getParty();

        if (party != null) {
            if (ctx.getPlugin().hasPlugin("InterfaceMaker")) {
                new PartyMenu(player, 1).build(player.getBukkitPlayer());
            } else {
                player.sendMessage(
                    player.getI18nMessage("info.as-text")
                        .replace("{leader}", party.getLeader().getName())
                        .replace("{members}", party.getMembersAsString())  
                        .replace("{memberscount}", party.getMembersCount() + "")
                        .replace("{maxmembers}", party.getMaxMembers() + "")
                );
            }
        } else {
            player.sendI18nMessage("common.not-in-party");
        }
    }
}
