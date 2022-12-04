package net.driedsponge.commands;

import net.driedsponge.PlayerStore;
import net.driedsponge.VoiceController;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.GenericCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;

public class Skip extends SlashCommand {
    public Skip() {
        super("skip");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        event.deferReply().queue();
        skip(event.getMember(),event.getGuild(),event.getHook());
    }

    public static void skip(Member member, Guild guild, InteractionHook hook){
        if(!CommonChecks.listeningMusic(member,guild)){
            hook.sendMessage("You need to be in a voice channel with the bot to skip.").setEphemeral(true).queue();
            return;
        }
        if(CommonChecks.playingMusic(guild)){
            VoiceController vc = PlayerStore.get(guild);
            hook.sendMessage(":fast_forward: "+member.getAsMention()+" skipped **"+vc.getNowPlaying().getInfo().title+"**").complete();
            vc.skip();
        }else{
            hook.sendMessage("Nothing to skip.").setEphemeral(true).queue();
        }
    }
}
