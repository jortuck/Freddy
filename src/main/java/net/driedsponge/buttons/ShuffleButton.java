package net.driedsponge.buttons;

import net.driedsponge.Embeds;
import net.driedsponge.commands.music.Queue;
import net.driedsponge.commands.music.Shuffle;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

public final class ShuffleButton extends ButtonCommand {
    public static final Button SHUFFLE_BUTTON = Button.success("SH","Shuffle");
    public ShuffleButton() {
        super("SH");
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        try {
                Shuffle.shuffle(event.getMember(),event.getGuild());
                event.getMessage().editMessage(Shuffle.replyMessage(event.getMember())).queue();
                event.getMessage().editMessageEmbeds(Queue.qResponse(event.getGuild(),1).embed()).queue();
                event.replyEmbeds(Embeds.basic("The queue has been shuffled!").setFooter(
                        "Shuffled by "+event.getMember().getEffectiveName(),
                        event.getMember().getEffectiveAvatarUrl()
                ).build()).queue();
        } catch (Exception e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }

    }

}
