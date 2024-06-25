package net.driedsponge.commands.music;

import net.driedsponge.BadHostException;
import net.driedsponge.Embeds;
import net.driedsponge.Main;
import net.driedsponge.Player;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.net.URI;
import java.net.URISyntaxException;

public final class Play implements SlashCommand {

    public static final Play INSTANCE = new Play();

    private Play(){}

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getName().equals("play") || event.getName().equals("playskip")) {
            if (event.getMember().getVoiceState().inAudioChannel()) {
                String arg = event.getOptions().getFirst().getAsString();
                try {
                    if (!Player.contains(event.getGuild().getId())) {
                        Player.createPlayer(event.getMember().getVoiceState().getChannel());
                    }
                    Player player = Player.get(event.getGuild().getId());
                    if (!player.getVoiceChannel().asVoiceChannel().getId()
                            .equals(event.getMember().getVoiceState().getChannel().asVoiceChannel().getId())) {
                        player.updateChannel(event.getMember().getVoiceState().getChannel());
                    }

                    if (player.isFull()) {
                        event.reply("The queue is full! Please skip or remove a song." +
                                " The current queue limit is **" + Main.QUEUE_LIMIT + "**.").setEphemeral(true).queue();
                    } else if (isURL(arg)) {
                        try {
                            event.deferReply().queue();
                            player.play(new URI(arg), event, event.getName().equals("playskip"));
                        } catch (BadHostException | IllegalStateException e) {
                            event.getHook().sendMessage(e.getMessage()).queue();
                        }
                    } else {
                        event.deferReply().queue();
                        player.play("ytsearch:" + arg, event, event.getName().equals("playskip"));

                    }

                } catch (Exception e) {
                    event.replyEmbeds(Embeds.error("Error", e.getMessage()).build()).queue();
                }
            } else {
                event.reply("You must be a voice channel for me to play music for you!")
                        .setEphemeral(true).queue();
            }
        }
    }

    @Override
    public SlashCommandData[] getCommand() {
        return new SlashCommandData[]{
                Commands.slash("play", "Tells the bot to play a song. If a song is already playing, it will be added to the queue.")
                        .addOption(
                                OptionType.STRING,
                                "song",
                                "The song to play. This can be a song name, a YouTube link, or YouTube/Spotify playlist link.",
                                true)
                        .setGuildOnly(true),
                Commands.slash("playskip", "Tells the bot to play the song immediately instead of adding it to the queue.")
                        .addOption(
                                OptionType.STRING,
                                "song",
                                "The song to play. This can be a song name, a YouTube link, or YouTube/Spotify playlist link.",
                                true)
                        .setGuildOnly(true)
        };
    }

    /**
     * Helper method for determine if a string is a URL.
     *
     * @param url The url you want to check.
     * @return Returns true if the string is a URL, otherwise returns false.
     */
    private boolean isURL(String url) {
        try {
            URI u = new URI(url);
            if (u.isAbsolute()) {
                return true;
            }
        } catch (URISyntaxException e) {
            return false;
        }
        return false;
    }
}
