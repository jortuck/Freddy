package net.driedsponge;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import dev.lavalink.youtube.YoutubeAudioSourceManager;
import dev.lavalink.youtube.clients.*;
import dev.lavalink.youtube.clients.skeleton.Client;
import net.driedsponge.buttons.ButtonListener;
import net.driedsponge.commands.CommandListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.hc.core5.http.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
    public static final String SETTINGS_FILE = "config.json";
    public static final Color PRIMARY_COLOR = Color.MAGENTA;
    private static final List<String> allowedHosts = new ArrayList<>();
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    public static String OWNER_ID;
    public static AudioPlayerManager PLAYER_MANAGER;
    public static int QUEUE_LIMIT;
    private static String DISCORD_TOKEN;

    public static void main(String[] args) throws LoginException, IOException, ParseException, SpotifyWebApiException {

        logger.info("Loading Environment Variables...");
        loadEnvironment();
        logger.info("Starting bot...");
        // Register player manager
        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        YoutubeAudioSourceManager youtube = new YoutubeAudioSourceManager(true,
                new Client[] {new Tv(), new TvHtml5Embedded()});
        playerManager.registerSourceManager(youtube);
        PLAYER_MANAGER = playerManager;
        logger.info("Registered player manager with YouTube source.");

        allowedHosts.add("youtube.com");
        allowedHosts.add("www.youtube.com");
        allowedHosts.add("youtu.be");
        allowedHosts.add("music.youtube.com");
        allowedHosts.add("open.spotify.com");

        JDABuilder builder = JDABuilder.createDefault(DISCORD_TOKEN);
        // Manage parts of the cache
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES);
        builder.enableCache(CacheFlag.VOICE_STATE);
        // Disable compression (not recommended)
        builder.setCompression(Compression.NONE);

        builder.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_VOICE_STATES);

        builder.setActivity(Activity.watching("for /help"));

        //Voice
        builder.addEventListeners(new UserVoiceEvents());
        //Commands
        builder.addEventListeners(CommandListener.INSTANCE);
        // Messages
        builder.addEventListeners(new MessageListener());
        // Buttons
        builder.addEventListeners(new ButtonListener());

        JDA jda = builder.build();

        CommandListener.upsertCommands(jda.updateCommands());
    }

    public static List<String> getAllowedHosts() {
        return Collections.unmodifiableList(allowedHosts);
    }

    private static void loadEnvironment() {
        try {
            OWNER_ID = System.getenv("OWNER_ID");
            QUEUE_LIMIT = Integer.parseInt(System.getenv("QUEUE_LIMIT"));
            DISCORD_TOKEN = System.getenv("DISCORD_TOKEN");
            if (OWNER_ID == null || QUEUE_LIMIT == 0 || DISCORD_TOKEN == null) {
                throw new NullPointerException("A variable is null");
            }
        } catch (NullPointerException | NumberFormatException e) {
            logger.error("Failed loading variables from environment. Attempting to read " + SETTINGS_FILE);
            File file = new File(SETTINGS_FILE);
            Gson gson = new Gson();
            try {
                FileReader reader = new FileReader(file);
                JsonReader jsonReader = new JsonReader(reader);
                Environment env = gson.fromJson(jsonReader, Environment.class);
                Main.OWNER_ID = env.ownerID();
                Main.DISCORD_TOKEN = env.discordBotToken();
                if (env.queueLimit() == 0) {
                    Main.QUEUE_LIMIT = Integer.MAX_VALUE;
                } else if (env.queueLimit() < 0) {
                    Main.QUEUE_LIMIT = -env.queueLimit();
                } else {
                    Main.QUEUE_LIMIT = env.queueLimit();
                }
                SpotifyPlaylist.updateCredentials(env.spotifyClientID(), env.spotifyClientSecret());
            } catch (FileNotFoundException fileNotFound) {
                Environment env = new Environment(
                        "YOUR DISCORD ID",
                        "YOUR DISCORD BOT TOKEN",
                        500,
                        "SPOTIFY CLIENT ID",
                        "SPOTIFY CLIENT SECRET");
                try {
                    PrintStream printStream = new PrintStream(file);
                    printStream.println("// Please view full instructions on how to configure the bot here: https://github.com/jortuck/freddy");
                    printStream.println(gson.toJson(env));
                    printStream.close();
                    logger.info("Config File Created At " + file.getAbsoluteFile());
                    logger.info("The application will soon exit. Please configure config.json and restart the application.");
                    System.exit(1);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (JsonIOException | JsonSyntaxException exception) {
                logger.error("There is an error with your config file located at {}. Make sure " +
                        "your variables are valid and follow the conventions outlined here: https://github.com/jortuck/freddy." +
                        " If you continue to have issues, please delete the json file and allow the" +
                        " program to create a new one.",file.getAbsoluteFile());
                System.exit(1);
            }

        }

    }

}
