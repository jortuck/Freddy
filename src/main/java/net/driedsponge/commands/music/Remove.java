package net.driedsponge.commands.music;

import net.driedsponge.Main;
import net.driedsponge.PlayerStore;
import net.driedsponge.Song;
import net.driedsponge.VoiceController;
import net.driedsponge.commands.CommonChecks;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class Remove extends SlashCommand {
    public Remove() {
        super("remove");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            int skipAmount = 1;
            try {
                skipAmount = Math.abs(event.getInteraction().getOption("position").getAsInt());
            }catch (Exception e){
                throw new Exception("That is an invalid number!");
            }
            MessageEmbed remove = remove(event.getMember(),event.getGuild(),skipAmount);
            event.replyEmbeds(remove).queue();
        }catch (Exception e){
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    /**
     * Skips the current song.
     * @param member Who is removing the song?
     * @param guild Where the song is being removed?
     * @param position The current position in the queue of the song to be removed.
     * @return The message to tell the user.
     * @throws Exception Something went wrong with removing the song.s
     */
    public static MessageEmbed remove(Member member, Guild guild, int position) throws Exception{
        if(!CommonChecks.listeningMusic(member,guild)){
            throw new Exception("You need to be in a voice channel with the bot to skip.");
        }
        VoiceController vc = PlayerStore.get(guild);
        BlockingQueue <Song> queue = vc.getTrackScheduler().getQueue();
        if (!queue.isEmpty()) {
            if(position < 1 || position>queue.size()){
                throw new Exception("Please select a song between 1 and "+queue.size()+".");
            }
            List<Song> tempQueue = new ArrayList<>();
            queue.drainTo(tempQueue);
            Song removedSong = tempQueue.get(position-1);
            EmbedBuilder embedBuilder = new EmbedBuilder()
                    .setColor(Main.PRIMARY_COLOR)
                    .setTitle(String.format(":wastebasket: Removing %s from the queue!",removedSong.getInfo().title));
            tempQueue.remove(position-1);
            queue.addAll(tempQueue);
            return embedBuilder.build();
        }else{
            throw new Exception("The queue is empty! No songs to remove!");
        }

    }
}
