package net.driedsponge.commands.music;

import net.driedsponge.Player;
import net.driedsponge.PlayerStore;
import net.driedsponge.VoiceController;
import net.driedsponge.commands.CommonChecks;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public final class Skip extends SlashCommand {
    public Skip() {
        super("skip");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            int skipAmount = 1;
            try {
                skipAmount = Math.abs(event.getInteraction().getOption("position").getAsInt());
            }catch (Exception ignored){}
            String skip = skip(event.getMember(),event.getGuild(),skipAmount);
            event.reply(skip).setEphemeral(true).queue();
        }catch (Exception e){
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    /**
     * Skips the current song.
     * @param member Who is skipping the song?
     * @param guild Where the song is being skipped?
     * @param amount The amount of songs to skip.
     * @return
     * @throws Exception
     */
    public static String skip(Member member, Guild guild, int amount) throws Exception{
        if(!CommonChecks.listeningMusic(member,guild)){
            throw new Exception("You need to be in a voice channel with the bot to skip.");
        }
        if(Player.contains(guild.getId()) && Player.get(guild.getId()).getNowPlaying() != null){
            Player player = Player.get(guild.getId());
            player.skip(amount);
            return "Skipping "+amount+" song(s)...";
        }else{
            throw new IllegalStateException("Nothing to skip.");
        }
    }
}
