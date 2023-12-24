package net.driedsponge.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.driedsponge.Main;
import net.driedsponge.PlayerStore;
import net.driedsponge.Song;
import net.driedsponge.VoiceController;
import net.driedsponge.buttons.ShuffleButton;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;

import java.util.concurrent.BlockingQueue;

public class Queue extends SlashCommand {


    public Queue() {
        super("queue");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        event.deferReply().queue();


        if (event.getGuild().getAudioManager().isConnected() && PlayerStore.get(event.getGuild()) != null) {
            int page = 1;

            try{
                page = Math.abs(event.getInteraction().getOption("page").getAsInt());
            }catch (NullPointerException ignored){}
            try{
                MessageEmbed embed = qEmbed(event.getGuild(), page);
                WebhookMessageCreateAction<Message> response = event.getHook().sendMessageEmbeds(embed);
                if(!PlayerStore.get(event.getGuild()).getTrackScheduler().getQueue().isEmpty()){
                    response.addActionRow(ShuffleButton.SHUFFLE_BUTTON);
                }
                response.queue();
            } catch (Exception e){
                event.getHook().sendMessage(e.getMessage()).setEphemeral(true).queue();
            }
        } else {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Main.PRIMARY_COLOR);
            embedBuilder.setTitle("Nothing is playing.");
            event.getHook().sendMessageEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        }
    }

    public static MessageEmbed qEmbed(Guild guild, int page) throws Exception{
        int songsPerPage = 10;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Queue Page "+page);
        embedBuilder.setColor(Main.PRIMARY_COLOR);

        VoiceController vc = PlayerStore.get(guild);
        AudioTrackInfo np = vc.getNowPlaying().getInfo();
        BlockingQueue<Song> songs = vc.getTrackScheduler().getQueue();
        int songCount = songs.size();
        int pages = (songCount + songsPerPage - 1) / songsPerPage;
        if((page>pages && pages > 0) || page==0){
            throw new Exception("Invalid queue page! Please enter a page between 1 and "+pages+"!");
        }

        embedBuilder.setTitle("Queue");
        StringBuilder queue = new StringBuilder();

        queue.append("**Now Playing - ").append(np.title).append("**");
        queue.append("\n");
        queue.append("\n**Up Next:**");

        int loopLimit = Math.min(songCount, 10);
        int loopStart = ((page*songsPerPage)-songsPerPage);
        int loopEnd = Math.min(page*songsPerPage,songCount);
        if (songs.isEmpty()) {
            queue.append(" No songs in the queue!");
        } else {

            for (int i = loopStart; i < loopEnd; i++) {
                Song song = (Song) songs.toArray()[i];
                queue.append("\n").append(i+1)
                        .append(" - ")
                        .append("[")
                        .append(song.getInfo().title)
                        .append("](" + song.getInfo().uri + ")")
                        .append(" `(Requested by: " + song.getRequester().getUser().getName() + ")`");
            }
            embedBuilder.setFooter("Page "+page+" of "+ pages + " | Showing song(s) "+(loopStart+1)+"-"+(loopEnd)+" of "+(songCount+1)+".");
        }

        embedBuilder.setDescription(queue);
        return embedBuilder.build();
    }
}
