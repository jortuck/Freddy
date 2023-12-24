package net.driedsponge.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class RemoveSongButton extends ButtonCommand {
    public static final Button REMOVE_BUTTON = Button.danger("RS","Remove Song");
    public RemoveSongButton() {
        super("RS");
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        try {
            TextInput number = TextInput.create("number","Song # To Remove From Queue", TextInputStyle.SHORT)
                    .setRequiredRange(1,100)
                    .build();
            Modal modal = Modal.create("removeSong","Remove Song")
                            .addActionRow(number)
                            .build();
            event.replyModal(modal).queue();
        } catch (Exception e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }

    }

}
