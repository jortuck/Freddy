# Freddy Bot

An open source Discord bot I made to play music for my friends since all the other ones were taken
down by Google.

- To access the bots help menu, use `/help` or mention the bot in a text channel.

## Features:

- Plays any song of your choice, as long as it's on YouTube!
- YouTube searching enabled, no need to lookup URLs!
- YouTube/Spotify playlist support for quickly adding your favorite songs to the queue!
- Discord slash command support making it easy to control your music!
- Queue up to 500 (can be configured) songs for you and your friends to listen to!

## Getting Started

- Join a voice channel. Make sure the bot has permission to connect to the channel you join!
- Use `/play [query]` in a channel if your choice. The query can be a search term, a YouTube link,
  YouTube playlist link, or a Spotify playlist link.
    - Please note: The text channel where you put the first play command will become the text
      channel
      associated with your listening session. This means the bot will send updates and notifications
      about
      song playback to that channel. If this channel is not accessible by the bot, it will attempt
      to use
      the text channel built into your current voice call. If that channel is not available, it will
      not send
      any notification messages. Please make sure you give the bot proper permissions in order for
      everything
      to work as intended.
    - If you want to restrict commands to certain roles or channels, go
      to `Server Settings` > `Integrations` > `Freddy Fazbear` > `Manage`.
- That's it! The bot is playing music. There are several other commands for altering your playback
  experience, use `/help` to view them all.

## Additional Information

- The queue has a hard limit of 500. I may raise this later on but for now it is 500. If you are 
self-hosting the bot, you can change the `QUEUE_LIMIT` environment variable.
- I am limiting my hosted version of the bot (the one with the invite link here) to 100 servers.
If you are unable to invite my instance of the bot to your server, you may try the self-hosting
instructions below.
- If you have any questions or need to reach out to me, please fill out the contact form on
  [my website](https://jortuck.com/#contact), email the email listed on my
  [GitHub profile](https://github.com/jortuck), or add me on Discord `@driedsponge`.

## Self Hosting

> [!NOTE]
> As you can see from the repository, this bot was written in Java. Any attempt to run this bot
requires **Java 21**, or any later version.

<details>

<summary>Environment Variables</summary>

There are certain variables required for the operation of the bot. Below is a description of each one.
Where each one is set depends on how you decide to host the bot.

##### DISCORD_TOKEN
This is the most important variable for the bot. This is how the bot connects to
the Discord API in order to play music to people in calls. Here is how you can get your token:
1. Go to the [Discord Developer Portal](https://discord.com/developers/applications). 
2. Click on the `New Application` button on the top right. 
3. Give your application a name and click `Create`. 
4. You will be redirected to your application's page. Click on the `Bot` tab on the left side of the page. 
5. Click the `Reset Token` button in order to obtain your token. 
6. Remember to keep your bot's token a secret as it can be used to control your bot. If your token 
is ever exposed or compromised, you can regenerate it by clicking the `Reset Token` button.
7. On this page you can also customize your bots profile picture, banner, and username to your liking.
8. **While you are on this page, please enable the `Server Members Intent` and `Message Content Intent`.**
9. To generate the invite link, navigate to the `OAuth2` tab, scroll to the URL generator, and select
`bot`. Now you can enable the following permission: `Read Messages/View Channels`, `Send Messages`,
`Send Messages In Threads`, `Connect`, and `Speak`. 
10. Make sure the `Integration Type` is set to `Guild Install`
11. You can open the generated URL in your browser to invite the bot to your server. Save this URL
for later if you plan on inviting the bot to other servers.

##### OWNER_ID
This is where you put your Discord ID. This variable will allow you to use the owner 
commands associated with the bot. Here is how you can obtain it:
1. Open your Discord client.
2. Open settings and navigate to advanced.
3. Enable "Developer Mode".
4. Exit settings, click on your username in the bottom left, and click `Copy User ID`.

##### QUEUE_LIMIT
This one is pretty simple. It's just an integer that indicates the maximum amount of songs that can 
put into a queue per server. It must be a positive whole number or there will be issues with trying 
to use the bot (I would recommend 500). If you set the value to 0, the max amount of songs will be 
infinite.

##### SPOTIFY_CLIENT_ID & SPOTIFY_CLIENT_SECRET (Optional)
These variables are not required but **necessary if you want to support Spotify playlist**, as the bot 
uses the Spotify web API to fetch playlist data;

If you do not plan on needing Spotify support, please set both of these variables to `null`.
<details>
<summary>Steps To Obtain Spotify API Credentials</summary>

1. Visit the [Spotify Developer Dashboard](https://developer.spotify.com/dashboard/).  
2. Log in with your Spotify account. If you don't have a Spotify account, you'll need to create one.  
3. Once you're logged in, click on the `Create app` button.  
4. You'll be asked to enter a name for your app and a description. Fill in these fields with appropriate information.  
5. You will also be asked to supply a redirect URL, just put `http://localhost`.
6. For API/SDKs, select `Web API`.
7. Once you have crated your app, click `Settings` in the top right, and under `Basic Information`
you will see your Client ID and a button that says `View client secret`. Those are the credentials you will need.
8. Remember to keep your Client ID and Client Secret confidential. If your Client Secret gets compromised,
you can regenerate it from the app's dashboard.

</details>
</details>

<details>
<summary>Hosting Without Docker (Easy)</summary>

1. Head to the [releases page](https://github.com/jortuck/Freddy/releases) and download the latest
jar artifact from the most recent release. 
2. In addition, download the corresponding script file from the release page.
    - If you are on Windows, download `start.bat`.
    - If you are on Mac/Linux, download `start.sh`.
3. Move the jar and the executable to a folder on your system. It does not matter where the folder is
located, as long both files are together.
4. Double-click the executable (the sh/bat file) to start the bot. It will crash the first time around, but it should
generate a `config.json`.
5. Open the `config.json` file, and set the variables inside to the environment variables you have
from the previous section. 
6. Finally, double-click the executable one more time to start the bot. If everything was configured
correctly, it should be running, and you should be able to use the commands. If you are encountering
any issues, check the `freddy.log` file for errors. If you cannot resolve your issue, [create a new
issue on GitHub](https://github.com/jortuck/Freddy/issues/new/choose).
7. You might get messages from your operating system saying that the file is "not safe" because it is
not from a verified developer. There is no way for me to stop these messages, but I can guarentee
everything from this repository is safe. If you do not believe me, feel free to download the source
code and compile the application yourself.

</details>

<details>
<summary>Hosting With Docker (Advanced)</summary>

These instructions assume you already have docker installed on your system, and you have some
technical knowledge. If these instructions don't make sense, use the above guide.
1. Download the code by cloning it with git or downloading the zip from GitHub.
    - `git clone https://github.com/jortuck/Freddy.git`
2. Move the cloned folder to any desired location on your computer.
3. Make a copy of the `settings.example.env` and rename it to `settings.env`. Once you have done this,
open `settings.env` and fill in the environment variables from the earlier instructions. 
4. After you have done this, you can run `docker compose up --build` to start the bot.
5. The `Dockerfile` is also included in the repository. While there is no hosted image, feel free to
make your own build.

</details>

