package net.driedsponge.commands.music;

import net.driedsponge.PlayerStore;
import net.driedsponge.VoiceController;
import net.driedsponge.commands.CommonChecks;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Shuffle extends SlashCommand {
    public Shuffle() {
        super("shuffle");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event)  {

        try {
            shuffle(event.getMember(),event.getGuild());
            event.reply("The queue has been shuffled!").queue();
        } catch (Exception e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    /**
     * Shuffle the queue
     * @param member
     * @param guild
     */
    public static void shuffle(Member member, Guild guild) throws Exception{
        if (CommonChecks.listeningMusic(member, guild) && CommonChecks.listeningMusic(member, guild)) {
            VoiceController vc = PlayerStore.get(guild);
            if (!vc.getTrackScheduler().shuffle()) {
                throw new Exception("There are no songs in the queue to shuffle.");
            }
        } else {
            throw new Exception("You need to be in a call listening to music to use this command!");
        }
    }

    public static String replyMessage(Member member){
        return  member.getAsMention()+" shuffled the queue!";
    }
}
