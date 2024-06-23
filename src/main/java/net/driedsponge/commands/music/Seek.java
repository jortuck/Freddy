package net.driedsponge.commands.music;

import net.driedsponge.Embeds;
import net.driedsponge.Player;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public final class Seek extends SlashCommand {

    public static final Seek INSTANCE = new Seek();

    private Seek(){
        super("seek");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event)  {
        Player player = Player.get(event.getGuild().getId());
        if(player!=null){
            if(player.getVoiceChannel().asVoiceChannel() == event.getMember().getVoiceState().getChannel()){
                try {
                    String time = event.getOption("time").getAsString();
                    player.seek(convertTime(time));
                    event.replyEmbeds(Embeds.basic("Seeking to "+time+"!").build()).queue();
                } catch (IllegalStateException | IllegalArgumentException e){
                    event.reply(e.getMessage()).setEphemeral(true).queue();
                }
            }else{
                event.reply("You must be in the same channel as me to seek!").setEphemeral(true).queue();
            }
        }else {
            event.reply("There is nothing playing right now.").setEphemeral(true).queue();
        }
    }

    /**
     * Converts a time string of MINUTES:SECONDS to a long of milliseconds.
     * @param time The string time in form of MINUTES:SECONDS.
     * @return The duration in milliseconds as a long.
     * @throws IllegalArgumentException If the string is an invalid time (bad format, seconds greater
     * than 60, etc).
     */
    private long convertTime(String time){
        String[] timeComponents = time.split(":");
        try {
            int minutes = Integer.parseInt(timeComponents[0]);
            int seconds = Integer.parseInt(timeComponents[1]);
            if(seconds>60){
                throw new IllegalArgumentException("Your seconds can't be greater than 60.");
            }
            if(minutes < 0 || seconds < 0){
                throw new IllegalArgumentException("Please give a time greater than 0.");
            }
            int finalSecs = (minutes * 60)+seconds;
            System.out.println(finalSecs);
            return finalSecs* 1000L;
        }catch (NumberFormatException e){
            throw new IllegalArgumentException("There was an issue parsing your given time. Please make sure it is of the format `MINUTES:SECONDS` or just `SECONDS`.");
        }
    }

}
