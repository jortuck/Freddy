package net.driedsponge.buttons;

import net.driedsponge.QueueResponse;
import net.driedsponge.commands.music.Queue;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;

public class NextPageButton extends ButtonCommand {
    public static final Button NEXT_PAGE_BUTTON = Button.primary("NP","Next Page");
    public NextPageButton() {
        super("NP");
    }

    @Override
    public void execute(ButtonInteractionEvent event) {
        try {
            int currentPage = event.getComponent().getId().charAt(2) - '0';
            int page = currentPage + 1;
            QueueResponse queue = Queue.qResponse(event.getGuild(), page);
            event.getMessage().editMessageEmbeds(queue.getEmbed()).queue();
            if(!queue.isEmpty()){
                event.getMessage().editMessageComponents(ActionRow.of(ShuffleButton.SHUFFLE_BUTTON,
                        PreviousPageButton.PREVIOUS_PAGE_BUTTON.withId("PP"+page)
                                .withDisabled(page==queue.getFirstPage()),
                        NextPageButton.NEXT_PAGE_BUTTON.withId("NP"+page)
                                .withDisabled(page==queue.getLastPage()))).queue();
            }
            event.reply("Going to page "+page+"!").setEphemeral(true).queue();
        } catch (Exception e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }

    }

}
