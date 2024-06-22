package net.driedsponge.buttons;

import net.driedsponge.commands.music.Skip;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public final class SkipButton extends  ButtonCommand{
        public static final Button SKIP_BUTTON = Button.primary("SK", "Skip");
        public SkipButton(){
            super("SK");
        }

        @Override
        public void execute(ButtonInteractionEvent event){
                try {
                        String skip = Skip.skip(event.getMember(),event.getGuild(), 1);
                        MessageEmbed original = event.getMessage().getEmbeds().get(0);
                        event.reply(skip).setEphemeral(true).queue();
                        // Set skip button as disabled when clicked. Maybe remove it entirely? idk.
                        event.editButton(SKIP_BUTTON.asDisabled()).queue();
                }catch (Exception e){
                        event.reply(e.getMessage()).setEphemeral(true).queue();
                }
        }

}
