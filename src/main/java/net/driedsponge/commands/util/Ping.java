package net.driedsponge.commands.util;

import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.io.Serializable;

public final class  Ping extends SlashCommand {

    public static final Ping INSTANCE = new Ping();

    private Ping(){
        super("ping");
    }
    @Override
    public void execute(SlashCommandInteractionEvent event) {
            event.reply("Pong!").queue(response ->{
                response.editOriginal("Pong! **_"+event.getJDA().getGatewayPing()+"ms_**").queue();
            }); // reply immediately
    }

}
