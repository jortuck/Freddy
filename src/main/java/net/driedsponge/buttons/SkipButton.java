package net.driedsponge.buttons;

import net.driedsponge.commands.Skip;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class SkipButton extends  ButtonListener{
        public static final Button SKIP_BUTTON = Button.primary("skip", "Skip");
        public SkipButton(){
            super("skip");
        }

        @Override
        public void execute(ButtonInteractionEvent event){
                event.deferReply().queue();
                Skip.skip(event.getMember(),event.getGuild(),event.getHook());
        }

}
