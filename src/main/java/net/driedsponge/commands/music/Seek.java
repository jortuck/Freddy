package net.driedsponge.commands.music;

import net.driedsponge.PlayerStore;
import net.driedsponge.TrackScheduler;
import net.driedsponge.VoiceController;
import net.driedsponge.commands.CommonChecks;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class Seek extends SlashCommand {
    public Seek(){
        super("seek");
    }
    @Override
    public void execute(SlashCommandInteractionEvent event)  {
        if(!CommonChecks.playingMusic(event.getGuild())){
            event.reply("I am not playing music anywhere.").setEphemeral(true).queue();
            return;
        }
        if(!CommonChecks.listeningMusic(event.getMember(),event.getGuild())){
            event.reply("You need to be in a call listening to music with me to seek music.").setEphemeral(true).queue();
            return;
        }

        try {
            int seconds = checkTime(event.getOption("time").getAsString());
            VoiceController vc = PlayerStore.get(event.getGuild());
            long milliseconds = seconds * 1000;
            if(milliseconds > vc.getPlayer().getPlayingTrack().getDuration()){
                event.reply("The amount of time specified is longer than the track.").setEphemeral(true).queue();
                return;
            }
            vc.getPlayer().getPlayingTrack().setPosition(milliseconds);
            event.reply(":fast_forward: "+event.getMember().getAsMention()+" seeked to `"+ TrackScheduler.duration(milliseconds)+"`/`"+TrackScheduler.duration(vc.getNowPlaying().getTrack().getDuration())+"`").queue();
        }catch (Exception e){
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    private int checkTime(String time) throws Exception{
        String timeString = time;

        String[] timeComponents = timeString.split(":");
        try {
            int minutes = Integer.parseInt(timeComponents[0]);
            int seconds = Integer.parseInt(timeComponents[1]);
            if(seconds>60){
                throw new Exception("Your seconds can't be greater than 60.");
            }
            if(minutes < 0 || seconds < 0){
                throw new Exception("Please give a time greater than 0.");
            }
            int finalSecs = (minutes * 60)+seconds;
            return finalSecs;
        }catch (NumberFormatException e){
            throw new Exception("There was an issue parsing your given time. Please make sure it is of the format `MINUTES:SECONDS` or just `SECONDS`.");
        }
    }

}
