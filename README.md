
# FinchBot
A Discord bot built for the SICAGA community Discord server.

## About Sicaga
For more information about Sicaga (Seattle Independent Comic and Game Aritsts), please visit the [official website](http://sicaga.com/#/) or the [Meetup.com page](https://www.meetup.com/sicaga/).

## About FinchBot
### Current Features
- All necessary configuration done through `config.json` file.
- Adds and removes users from roles (groups) based on reactions
	- Option to auto-remove user reactions after role assignment has taken place
	- Compatible with built-in Unicode emojis, or custom user-uploaded emotes.
  - Roles can either be:
	  - Exclusive (user can only have one at a time), such as username colors
	*or*
	  - Non-Exclusive (users can have as many or as little as they choose), such as announcement subscriptions
- **(NEW)** Whitelisted users can post to Sicaga's social media pages with updates to their comics.
	- Relevant user data (comic name, artist name, link to website) is managed in Airtable and retrieved automatically when creating a post.
	- Users can select from a variety of templates to choose from.
	- Relevant Commands:
		- `!update`: Allows users to select one of several templates for their post.
		- `!post`: Sends a post to social media. 
- General Commands:
	- `!ping`: Provides information about FinchBot's latency.
	- `!whoami`: Provides information about the user, including their roles.
	- `!reportbug <description>`: Reports a bug with FinchBot to developers.
	- `!source`: Links to FinchBot's GitHub repository.
	- `!roadmap`: Links to FinchBot's roadmap of planned features.

### Technologies Used
- [JDA (Java Discord API)](https://github.com/DV8FromTheWorld/JDA)
- [JDA-Utilities](https://github.com/JDA-Applications/JDA-Utilities)
- [gson](https://github.com/google/gson)
- [Logback](https://logback.qos.ch/)

## Roadmap

More features are being planned and implemented! Here is a sneak peak at what is coming:

### Share Your Comics on Sicaga's Social Media (NOW LIVE!)
 Promote your comic via Sicaga's social media straight through FinchBot! By using commands in Discord, users can select from a list of post templates. Then with a couple of keystrokes, FinchBot will automatically post a link to your comic's latest update!
 
### Meetup RSVP

RSVP to an upcoming Sicaga event right in Discord! As long as you're logged into your Meetup.com account, you'll be able to let event organizers know you're coming just by typing a command.

*For more information about upcoming features, please contact IronOhki#9999 on Discord.*