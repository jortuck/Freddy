package net.driedsponge.buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public final class ButtonListener extends ListenerAdapter {

    private static final HashMap<String, ButtonCommand> commands = new HashMap<>();

    public ButtonListener(){
        ButtonCommand[] cmds = new ButtonCommand[]{
                new GuildList(),
                new Entertaining(),
                new ShuffleButton(),
                new NextPageButton(),
                new PreviousPageButton(),
                new SkipButton()

        };
        ArrayList<ButtonCommand> botCommands = new ArrayList<>(Arrays.asList(cmds));
        for (ButtonCommand command : botCommands) {
            commands.put(command.getName().toUpperCase(), command);
        }
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        ButtonCommand command = commands.get(event.getComponent().getId().toUpperCase().substring(0,2));
        command.execute(event);
    }

}
