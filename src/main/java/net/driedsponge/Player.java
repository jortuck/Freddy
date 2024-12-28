package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.driedsponge.buttons.SkipButton;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.model_objects.specification.PlaylistTrack;

import java.net.URI;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
// TODO: Break this beast of a class up some time.

/**
 * This is the big class that controls the core features of the music player. It has many exposed
 * methods for controlling music based on other inputs.
 * <p>
 * We do not use the onTrackStart event to note when songs start playing. There are four
 * important places where this is done instead:
 *     <ul>
 *         <li>
 *              At {@link #skip(int)} when the user skips some songs and we need to set now playing
 *              on the new song they are listening to.
 *         </li>
 *         <li>
 *             At {@link StandardResultLoader#playlistLoaded(AudioPlaylist)}  } when we need to play
 *             the first song in a playlist.
 *         </li>
 *         <li>
 *             At {@link TrackEvents#onTrackEnd(AudioPlayer, AudioTrack, AudioTrackEndReason)} when
 *             it's time to move on in the queue.
 *         </li>
 *         <li>
 *             At {@link StandardResultLoader#trackLoaded(AudioTrack)} for standard initial playing
 *             of a song.
 *         </li>
 *     </ul>
 *     Under the hood we use {@link LinkedBlockingQueue} because it is a good implementation of
 *     {@link BlockingQueue}
 * </p>
 */
public final class Player {
    private static final HashMap<String, Player> PLAYERS = new HashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(Player.class);
    private final Guild guild;
    private final AudioPlayer player;
    private AudioChannelUnion voiceChannel;
    private GuildMessageChannel textChannel;
    private final TrackEvents schedule;
    private QueuedSong nowPlaying;
    private BlockingQueue<QueuedSong> queue;

    /**
     * @param channel
     * @throws IllegalArgumentException        If the provided channel was null. If the provided channel is
     *                                         not part of the Guild that the current audio connection is connected to.
     * @throws UnsupportedOperationException   If audio is disabled due to an internal JDA error
     * @throws InsufficientPermissionException If the currently
     *                                         logged in account does not have the Permission VOICE_MOVE_OTHERS and the user limit has been
     *                                         exceeded!
     */
    private Player(AudioChannelUnion channel) {
        this.guild = channel.getGuild();
        this.voiceChannel = channel;
        this.player = Main.PLAYER_MANAGER.createPlayer();
        this.schedule = new TrackEvents();
        player.addListener(schedule);
        guild.getAudioManager().openAudioConnection(channel);
        guild.getAudioManager().setSelfDeafened(true);
        guild.getAudioManager().setSendingHandler(new MusicHandler(player));
        this.queue = new LinkedBlockingQueue<>(Main.QUEUE_LIMIT);
        this.textChannel = channel.asGuildMessageChannel();
        logger.info("Initialized a new Player in {} ({} - {})",
                voiceChannel.getName(),
                guild.getName(),
                guild.getId()
        );
    }

    /**
     * Creates a new player for the given voice channel. If there is already a player in the guild
     * it will attempt to move the bot, throwing an error if it does not have permission.
     *
     * @return The player created, or if the bot is already in a call, the player stored
     */
    public static Player createPlayer(AudioChannelUnion channel) {
        if (PLAYERS.containsKey(channel.getGuild().getId())) {
            Player player = PLAYERS.get(channel.getGuild().getId());
            player.updateChannel(channel);
            return player;
        }
        Player newPlayer = new Player(channel);
        PLAYERS.put(channel.getGuild().getId(), newPlayer);
        return newPlayer;
    }

    public static void destroy(String guildId) {
        if (PLAYERS.containsKey(guildId)) {
            Player player = PLAYERS.remove(guildId);
            player.textChannel = null;
            try {
                if (player.voiceChannel.asVoiceChannel().getStatus().startsWith(":musical_note:")) {
                    player.voiceChannel.asVoiceChannel().modifyStatus("").queue();
                }
            } catch (InsufficientPermissionException e) {
                logger.warn("Tried to set voice status in {} ({} - {}) but did not have permission to.",
                        player.voiceChannel.getName(),
                        player.guild.getName(),
                        player.guild.getId()
                );
            }
            player.player.destroy();
            logger.info("Destroyed player in {} ({})",
                    player.guild.getName(),
                    player.guild.getId());
            player.getGuild().getAudioManager().closeAudioConnection();
        }else{
            logger.warn("Destroy was used when there was no active player.");
        }
    }

    public static Player get(String guildId) {
        return PLAYERS.get(guildId);
    }

    public static boolean contains(String guildId) {
        return PLAYERS.containsKey(guildId);
    }

    /**
     * Get a list of active players.
     *
     * @return An unmodifiable list of the active players.
     */
    public static List<Player> getPlayers() {
        return List.of(PLAYERS.values().toArray(new Player[]{}));
    }

    public void play(String song, SlashCommandInteractionEvent event, boolean now) {
        if (Main.QUEUE_LIMIT - queue.size() == 0) {
            throw new IllegalStateException("The queue is full!");
        }
        textChannel = event.getChannel().asTextChannel();
        boolean playNow = now || player.getPlayingTrack() == null;
        Main.PLAYER_MANAGER.loadItem(song, new StandardResultLoader(event, playNow));
    }

    public void play(URI song, SlashCommandInteractionEvent event, boolean now) throws BadHostException {
        if (Main.QUEUE_LIMIT - queue.size() == 0) {
            throw new IllegalStateException("The queue is full!");
        }
        textChannel = event.getChannel().asTextChannel();
        if (Main.getAllowedHosts().contains(song.getHost())) {
            if (song.getHost().equals("open.spotify.com")) {
                String[] paths = song.getPath().split("/", 3);
                if (paths[1].equals("playlist") && paths[2] != null) {
                    try {
                        System.out.println("test 1");
                        SpotifyPlaylist playlist = SpotifyPlaylist.fromId(paths[2], Main.QUEUE_LIMIT - queue.size());
                        SpotifyResultLoader loader = new SpotifyResultLoader(event);
                        List<PlaylistTrack> songs = playlist.getSongs();
                        if (player.getPlayingTrack() == null || now) {
                            PlaylistTrack firstSong = songs.removeFirst();
                            Main.PLAYER_MANAGER.loadItem(
                                    "ytsearch:" + firstSong.getTrack().getName(),
                                    new StandardResultLoader(event, true)
                            );
                        }
                        for (PlaylistTrack spotify : songs) {
                            Main.PLAYER_MANAGER.loadItem(
                                    "ytsearch:" + spotify.getTrack().getName(), loader);
                        }
                        event.getHook().sendMessageEmbeds(
                                Embeds.playlistEmbed(
                                        playlist.getName(),
                                        playlist.getSongs().size(),
                                        playlist.getImage(),
                                        event.getUser()
                                ).build()
                        ).addActionRow(Button.link(playlist.getUrl(), "Spotify Playlist")).queue();
                    } catch (Exception e) {
                        throw new BadHostException("Make sure it's a public playlist " + e.getMessage());
                    }
                } else {
                    throw new BadHostException("Invalid spotify Playlist link");
                }
            } else {
                boolean playNow = now || player.getPlayingTrack() == null;
                Main.PLAYER_MANAGER.loadItem(song.toString(), new StandardResultLoader(event, playNow));
            }
        } else {
            throw new BadHostException("The URL must be valid YouTube URL or Spotify Playlist Link");
        }
    }

    public AudioChannelUnion getVoiceChannel() {
        return voiceChannel;
    }

    /**
     * Moves the bot to a new voice channel.
     *
     * @param channel The channel to move the bot to.
     */
    public void updateChannel(AudioChannelUnion channel) {
        try {
            String previousStatus = this.voiceChannel.asVoiceChannel().getStatus();
            if (previousStatus.startsWith(":musical_note:")) {
                if (channel.asVoiceChannel().getStatus().isBlank()
                        || channel.asVoiceChannel().getStatus().startsWith(":musical_note:")) {
                    channel.asVoiceChannel().modifyStatus(
                            previousStatus
                    ).queue();
                }
                this.voiceChannel.asVoiceChannel().modifyStatus("").queue();
            }
        } catch (InsufficientPermissionException e) {
            logger.warn("Tried to set voice status in {} ({} - {}) but did not have permission to.",
                    channel.getName(),
                    guild.getName(),
                    guild.getId()
            );
        }
        this.guild.getAudioManager().openAudioConnection(channel);
        this.voiceChannel = channel;
    }

    /**
     * Returns the currently playing song as a Queued song. Returns null if no song is being played.
     */
    public QueuedSong getNowPlaying() {
        return nowPlaying;
    }

    public List<QueuedSong> getQueue() {
        QueuedSong[] songs = this.queue.toArray(new QueuedSong[0]);
        return new ArrayList<>(List.of(songs));
    }

    /**
     * Returns the guild that is associated with this player.
     */
    public Guild getGuild() {
        return this.guild;
    }

    /**
     * Clears the queue for the player.
     */
    public void clearQueue() {
        this.queue.clear();
    }

    public void setPaused(boolean pause) {
        if (pause && player.isPaused()) {
            throw new IllegalArgumentException("I am already paused! Use `/resume` to continue playback!");
        }
        if (!pause && !player.isPaused()) {
            throw new IllegalArgumentException("Please use `/pause` if you would like to pause playback!");
        }
        player.setPaused(pause);
    }

    /**
     * Skips through the queues x amount of times.
     *
     * @throws IllegalArgumentException if x greater than queue size or < 1 and the queue is not empty
     * @throws IllegalStateException    if nothing is playing.
     */
    public void skip(int x) {
        if (queue.isEmpty() || x == queue.size() + 1) {
            player.stopTrack();
            return;
        }
        if (x < 1 || x > queue.size()) {
            throw new IllegalArgumentException("Can't skip *" + x + "* song(s) in a queue" +
                    " with *" + queue.size() + "* song(s)!");
        }
        if (this.nowPlaying == null) {
            throw new IllegalArgumentException();
        }
        QueuedSong nextSong = null;
        for (int i = 0; i < x; i++) {
            nextSong = queue.poll();
        }
        assert nextSong != null;
        player.playTrack(nextSong.getTrack());
        nowPlaying = nextSong;
        sendMessageEmbed(Embeds.songCard("Now Playing", nextSong).build(), true);
        // TODO: Look into how we can prevent slight clip of previous song if un-pausing.
        player.setPaused(false);
    }

    /**
     * Shuffles the queue.
     *
     * @throws IllegalStateException If queue.size() <= 1
     */
    public void shuffle() {
        if (queue.size() <= 1) {
            throw new IllegalStateException("The queue is not big enough to be shuffled!");
        }
        List<QueuedSong> tempList = this.getQueue();
        Collections.shuffle(tempList);
        this.queue = new LinkedBlockingQueue<>(Main.QUEUE_LIMIT);
        this.queue.addAll(tempList);
    }

    /**
     * Remove a song from the queue. This is  runs in O(n).
     *
     * @param x The index of the song you want to remove (0 being the first song).
     * @return The song that was removed from the queue.
     * @throws IllegalArgumentException If x is > 0 or greater than queue size.
     */
    public QueuedSong remove(int x) {
        if (x < 0 || x >= queue.size()) {
            throw new IllegalArgumentException(x + 1 + " is an invalid song to remove!");
        }
        Iterator<QueuedSong> itr = this.queue.iterator();
        int index = 0;
        while (itr.hasNext()) {
            QueuedSong songToRemove = itr.next();
            if (index == x) {
                System.out.println(songToRemove.getInfo().title);
                itr.remove();
                return songToRemove;
            }
            index++;
        }
        return null;
    }

    /**
     * Change the position of the player.
     *
     * @param milliseconds The position of the song in milliseconds to skip to.
     * @throws IllegalStateException    If the bot is not playing music.
     * @throws IllegalArgumentException If the number of milliseconds is out of bounds of 0 and the song.
     */
    public void seek(long milliseconds) {
        if (this.nowPlaying == null) {
            throw new IllegalStateException("I am not playing any music!");
        }
        if (milliseconds < 0 || milliseconds > this.nowPlaying.getInfo().length) {
            throw new IllegalArgumentException("Invalid time to skip to!");
        }
        this.player.getPlayingTrack().setPosition(milliseconds);
    }

    /**
     * Returns true if the queue is full.
     */
    public boolean isFull() {
        return queue.size() == Main.QUEUE_LIMIT;
    }

    private void sendMessage(String text) {
        sendMessageEmbed(Embeds.basic(text).build());
    }

    private void sendMessageEmbed(MessageEmbed embed, boolean includeSkip) {
        if (this.textChannel == null) {
            return;
        }
        try {
            MessageCreateAction action = textChannel.sendMessageEmbeds(embed);
            if (includeSkip) {
                action.addActionRow(SkipButton.SKIP_BUTTON);
            }
            action.queue();
        } catch (InsufficientPermissionException e) {
            try {
                logger.warn("Failed to send message to #{} in {} ({}), trying voice channel text" +
                                "channel.",
                        textChannel.getName(), guild.getName(), guild.getId());
                MessageCreateAction action2 = this.voiceChannel.asGuildMessageChannel().sendMessageEmbeds(embed);
                if (includeSkip) {
                    action2.addActionRow(SkipButton.SKIP_BUTTON);
                }
                action2.queue();
            } catch (InsufficientPermissionException e2) {
                logger.warn("Failed to send message voice channel in {} ({})!",
                        guild.getName(), guild.getId());
            }
        }
    }

    private void sendMessageEmbed(MessageEmbed embed) {
        sendMessageEmbed(embed, false);
    }

    /**
     * Private class for tracking player events such as trackEnd, tracKStart, etc.
     */
    private final class TrackEvents extends AudioEventAdapter {
        @Override
        public void onTrackStart(AudioPlayer player, AudioTrack track) {
            try {
                String currentStatus = voiceChannel.asVoiceChannel().getStatus();
                if (currentStatus.isBlank() || currentStatus.startsWith(":musical_note:")) {
                    voiceChannel.asVoiceChannel().modifyStatus(":musical_note: " + player.getPlayingTrack()
                            .getInfo().title).queue();
                }
            } catch (InsufficientPermissionException e) {
                logger.warn("Tried to set voice status in {} ({} - {}) but did not have permission to.",
                        voiceChannel.getName(),
                        guild.getName(),
                        guild.getId()
                );
            }

        }

        @Override
        public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
            if (endReason == AudioTrackEndReason.LOAD_FAILED) {
                sendMessageEmbed(Embeds.error(
                                "Failed Loading *" + nowPlaying.getInfo().title + "*."
                                        + " Moving onto the next track.",
                                "For an unknown reason, this song could not be loaded."
                                        + " Please try requesting the song again. If this issue persists, make a new bug" +
                                        " report using /bug.")
                        .build());
            }
            // Start next song if there is one in the queue and the current one finished.
            if (!queue.isEmpty() && endReason.mayStartNext) {
                QueuedSong nextSong = queue.poll();
                player.playTrack(nextSong.getTrack());
                nowPlaying = nextSong;
                sendMessageEmbed(Embeds.songCard("Now Playing", nextSong).build(), true);
            } else if (queue.isEmpty() && player.getPlayingTrack() == null) {
                // this works because we are checking getPlayingTrack form the player, not nowPlaying
                sendMessage("Queue is empty! No more songs to play!");
                nowPlaying = null;
            }
        }
    }

    private final class SpotifyResultLoader implements AudioLoadResultHandler {
        private final SlashCommandInteractionEvent event;

        public SpotifyResultLoader(SlashCommandInteractionEvent event) {
            this.event = event;
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            queue.offer(new QueuedSong(track, event));
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            trackLoaded(playlist.getTracks().getFirst());
        }

        @Override
        public void noMatches() {
            // do nothing
        }

        @Override
        public void loadFailed(FriendlyException exception) {
            logger.warn("Loading of song failed from spotify: " + exception.getMessage());
        }
    }

    private final class StandardResultLoader implements AudioLoadResultHandler {
        private final SlashCommandInteractionEvent event;
        private boolean now;

        public StandardResultLoader(SlashCommandInteractionEvent event, boolean now) {
            this.event = event;
            this.now = now;
        }

        @Override
        public void trackLoaded(AudioTrack track) {
            QueuedSong song = new QueuedSong(track, event);
            if (now) {
                nowPlaying = song;
                player.playTrack(track);
                event.getHook().sendMessageEmbeds(
                        Embeds.songCard("Now Playing", song).build()
                ).addActionRow(SkipButton.SKIP_BUTTON).queue();
            } else {
                if (queue.offer(song)) { // returns false if the queue is full
                    event.getHook().sendMessageEmbeds(
                            Embeds.songCard("Song Added To Queue", song).build()
                    ).queue();
                } else {
                    event.getHook().sendMessageEmbeds(
                            Embeds.basic("The queue is full!").build()
                    ).queue();
                }

            }
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            // ignore playlist from search results
            if (playlist.isSearchResult()) {
                trackLoaded(playlist.getTracks().getFirst());
            } else {
                int numAdded = 0;
                for (AudioTrack track : playlist.getTracks()) {
                    QueuedSong song = new QueuedSong(track, event);
                    if (now) {
                        player.playTrack(track);
                        nowPlaying = song;
                        event.getHook().sendMessageEmbeds(
                                Embeds.songCard("Now Playing", song).build()
                        ).addActionRow(SkipButton.SKIP_BUTTON).queue();
                        now = false;
                    } else {
                        if (queue.offer(song)) {
                            numAdded++;
                        } else {
                            break; // stop loading the playlist, no more room
                        }
                    }
                }
                if (numAdded == 0) {
                    event.getHook().sendMessageEmbeds(
                            Embeds.basic("The queue is full!").build()
                    ).queue();
                    return;
                }
                event.getHook().sendMessageEmbeds(Embeds.playlistEmbed(
                        playlist.getName(),
                        numAdded,
                        null,
                        event.getUser()
                ).build()).queue();
            }
        }

        @Override
        public void noMatches() {
            event.getHook().sendMessage("We could not find your song!").queue();
        }

        @Override
        public void loadFailed(FriendlyException exception) {
            logger.warn("Loading of song failed: " + exception.getMessage());
            event.getHook().sendMessageEmbeds(
                    Embeds.error("Unfortunately that song failed to load.", exception.getMessage()).build()
            ).queue();
        }
    }

}