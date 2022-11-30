# Freddy Bot

An open source Discord bot I made to play music for my friends since all the other ones got taken down by google.

- To access the bots help menu, use `/help` or mention the bot in a text channel.
- [Invite The Bot](https://discord.com/api/oauth2/authorize?client_id=914454054808211476&permissions=414476271168&scope=bot%20applications.commands)
- [Trello Board](https://trello.com/b/Wo2BMjwA/freddybot)

## Features:
- Plays any song of your choice, as long as it's on YouTube!
- YouTube searching enabled, no need to lookup URLs!
- YouTube/Spotify playlist support for quickly adding your favorite songs to the queue!
- Discord slash command support making user interaction easy!

# Self Hosting (Not Finished):
The cool thing about an open source bot is that you can easily self-host it on your own system! If you 

## Setup:

### Getting Your Discord Bot Token:

### Getting Your Spotify API Credentials:
1. Visit the [Spotify developer dashboard](https://developer.spotify.com/dashboard/) to learn how to get a token.
2. One logged in, head to https://developer.spotify.com/dashboard/applications and click the green `CREATE AN APP` button.
3. One your app has been created, you should see Client ID and `SHOW CLIENT SECRET` directly below your app name and description. This will be your spotify api credentials.
### Installation & Configuration:
1. Clone the project from GitHub.
    - `git clone https://github.com/DriedSponge/Freddy.git`
2. In order for the bot to work, the following environment variables will need to be set. If you're using docker, they will bet set in the `settings.env` file (See Using Docker):
    - `DISCORD_TOKEN`
    - `SPOTIFY_CLIENT_ID`
    - `SPOTIFY_CLIENT_SECRET`
    - `OWNER_ID`

## Running The Bot:

### Using Docker:
1. Make sure the system you intend the bot to run on has Docker installed and properly configured. **If you're not sure, what docker is or if it's setup properly on your system, don't use it.**
2. Clone `settings.example.env` and rename the clone to `settings.env`.
3. Open your new `settings.env` file and set all the environment variables. If you're confused on how to do this, take a look at the example in `settings.example.env`.
4. Once you've set the environment variables, run `docker compose --profile prod up -d --build`. This should build & run the bot. If you encounter any errors when trying to start the bot, [open an issue on GitHub](https://github.com/DriedSponge/Freddy/issues).
    
.

### Owner Commands

These are a set of utility commands to help the bot owner. All owner commands use `!` has their prefix, so they can be used without having slash commands registered.

- `!statistics` - Shows how many guilds the bot is in and how many guilds it's entertaining.
- `!guildlist` - Shows a list of all guilds of which the bot is a member of.
- `!entertaining` - Shows a list of all guilds where music is playing.
