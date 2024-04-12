package dev._2lstudios.advancedparties.commands.impl;

import dev._2lstudios.advancedparties.commands.Argument;
import dev._2lstudios.advancedparties.commands.Command;
import dev._2lstudios.advancedparties.commands.CommandContext;
import dev._2lstudios.advancedparties.commands.CommandListener;
import dev._2lstudios.advancedparties.parties.Party;
import dev._2lstudios.advancedparties.parties.PartyMember;
import dev._2lstudios.advancedparties.players.PartyPlayer;

@Command(
  name = "promote",
  arguments = {Argument.STRING},
  minArguments = 1
)
public class PartyPromoteCommand extends CommandListener {
    @Override
    public void onExecuteByPlayer(CommandContext ctx) {
        PartyPlayer player = ctx.getPlayer();
        Party party = player.getParty();
        String targetName = ctx.getArguments().getString(0);

        if (party == null) {
            player.sendI18nMessage("common.not-in-party");
            return;
        }

        if (!party.isLeader(player)) {
            player.sendI18nMessage("promote.not-leader");
            return;
        }

        PartyMember member = party.getMemberByName(targetName);

        if (member == null) {
            player.sendI18nMessage("promote.target-not-in-party");
            return;
        }

        if (party.isLeader(member)) {
            player.sendI18nMessage("promote.cant-promote-self");
            return;
        }

        party.setLeader(member);
        party.announcePlayerPromoted(targetName);
    }
}
