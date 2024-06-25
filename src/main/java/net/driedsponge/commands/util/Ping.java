package net.driedsponge.commands.util;

import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public final class Ping implements SlashCommand {

    public static final Ping INSTANCE = new Ping();

    private Ping() {}

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.reply("Pong!").queue(response -> {
            response.editOriginal("Pong! **_" + event.getJDA().getGatewayPing() + "ms_**").queue();
        }); // reply immediately
    }

    @Override
    public SlashCommandData[] getCommand() {
        return new SlashCommandData[]{Commands.slash("ping", "Check the bots ping")};
    }

}
