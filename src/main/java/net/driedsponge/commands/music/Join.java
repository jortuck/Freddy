package net.driedsponge.commands.music;

import net.driedsponge.Player;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public final class Join extends SlashCommand {

    public Join() {
        super("Join");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        join(event);
    }

    private void join(SlashCommandInteractionEvent event) {
        if (event.getMember().getVoiceState().inAudioChannel()) {
            try {
                Player.createPlayer(event.getMember().getVoiceState().getChannel());
                event.reply("Joining your channel").queue();
            }catch (Exception e){
                event.reply(e.getMessage()).queue();
            }
        } else {
            event.reply("You must be in a call for me to join you!").queue();
        }
    }
}
