package net.driedsponge.buttons;

import net.driedsponge.commands.music.Shuffle;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public class ShuffleButton extends ButtonCommand {
    public static final Button SHUFFLE_BUTTON = Button.success("shuffle","Shuffle");
    public ShuffleButton() {
        super("shuffle");
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        event.deferReply().queue();
        Shuffle.shuffle(event.getMember(),event.getGuild(),event.getHook());
    }

}
