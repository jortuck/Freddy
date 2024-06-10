package net.driedsponge;

import net.driedsponge.commands.music.Queue;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class QueueResponse {
    private MessageEmbed embed;
    private boolean empty;
    private int firstPage;
    private int lastPage;
    private int currentPage;
    public QueueResponse(MessageEmbed embed, boolean empty, int firstPage, int lastPage, int currentPage){
        this.embed = embed;
        this.empty = empty;
        this.firstPage = firstPage;
        this.lastPage = lastPage;
        this.currentPage = currentPage;
    }

    public MessageEmbed getEmbed() {
        return embed;
    }

    public boolean isEmpty() {
        return empty;
    }

    public int getFirstPage() {
        return firstPage;
    }

    // Returns 0 if there is only one page.
    public int getLastPage() {
        return lastPage;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}
