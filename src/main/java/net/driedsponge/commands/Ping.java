package net.driedsponge.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class Ping extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        if (event.getName().equals("ping")) {
            event.reply("Pong!").queue(response ->{
                response.editOriginal("Pong! "+String.valueOf(event.getJDA().getGatewayPing())).queue();
            }); // reply immediately
        }
    }
}
