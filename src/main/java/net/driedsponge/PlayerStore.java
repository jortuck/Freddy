package net.driedsponge;

import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;

public class PlayerStore {
    private static final HashMap<Long, VoiceController> controllers = new HashMap<Long, VoiceController>();

    /**
     * Gets a controller from the controllers store.
     * @param guildId The ID of the guild to get.
     * @return The Voice Controller.
     */
    public static VoiceController get(String guildId){
        return controllers.get(Long.valueOf(guildId));
    }
    /**
     * Gets a controller from the controllers store.
     * @param guildId The ID of the guild to get.
     * @return The Voice Controller.
     */
    public static VoiceController get(Long guildId){
        return controllers.get(guildId);
    }
    /**
     * Gets a controller from the controllers store.
     * @param guild The guild to get.
     * @return The Voice Controller.
     */
    public static VoiceController get(Guild guild){
        return controllers.get(guild.getIdLong());
    }

    /**
     * Stores a controller in the store.
     * @param guild The guild associated with the controller.
     * @param voiceController The voice controller.
     */
    public static void store(Guild guild, VoiceController voiceController){
        controllers.putIfAbsent(guild.getIdLong(),voiceController);
    }

    /**
     * Removes a controller from the controllers store.
     * @param guildId The ID of the guild to remove.
     */
    public static void remove(String guildId){
        controllers.remove(Long.valueOf(guildId));
    }

    /**
     * Removes a controller from the controllers store.
     * @param guildId The ID of the guild to remove.
     */
    public static VoiceController remove(Long guildId){
        return controllers.remove(guildId);
    }

    /**
     * Removes a controller from the controllers store.
     * @param guild The guild.
     */
    public static VoiceController remove(Guild guild){
        return controllers.remove(guild.getIdLong());
    }

    /**
     * Returns the number of controllers (active calls).
     * @return The number of controllers.
     */
    public static int size(){
        return controllers.size();
    }
}
