# Freddy Bot

An open source discord bot I made to play music for my friends since all the other ones got taken down by google.

Invite The Bot (Here)[https://discord.com/api/oauth2/authorize?client_id=914454054808211476&permissions=414476271168&scope=bot%20applications.commands]

## Features:
- Plays any song of your choice, as long as it's on YouTube!
- YouTube searching enabled, no need to lookup URLs!
- YouTube/Spotify playlist support for quickly adding your favorite songs to the queue!
- Discord slash command support making user interaction easy!

# Self Hosting (Not Finished):
The cool thing about an open source bot is that you can easily self-host it on your own system! If you 

## Setup:

### Getting Your Discord Bot Token:

### Getting Your Spotify Api Credentials:

### Installation & Configuration:
1. Clone the project from github.
    - `git clone git@github.com:DriedSponge/Freddy.git`
2. In order for the bot to work, the following environment variables will need to be set. If you're using docker, they will bet set in the settings.env file (See Using Docker):
    - `DISCORD_TOKEN`
    - `SPOTIFY_CLIENT_ID`
    - `SPOTIFY_CLIENT_SECRET`
    - `OWNER_ID`

## Running The Bot:

### Using Docker:
1. Make sure the system you intend the bot to run on has Docker installed and properly configured. **If you're not sure, what docker is or if it's setup properly on your system, don't use it.**
2. Clone `settings.example.env` and rename the clone to `settings.env`.
3. Open your new `settings.env` file and set all of the environment variables. If you're confused on how to do this, take a look at the example in `settings.example.env`.

    
