package bowtie.bot.bots.cmd.teams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SplittableRandom;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;
import bowt.bot.Bot;
import bowt.cmnd.Command;
import bowt.cons.Colors;
import bowt.evnt.impl.CommandEvent;
import bowt.guild.GuildObject;
import bowt.util.perm.UserPermissions;
import bowtie.core.Main;

/**
 * @author &#8904
 *
 */
public class TeamChannelCommand extends Command
{
    private Bot bot;
    private Main main;
    SplittableRandom r;

    /**
     * @param validExpressions
     * @param permission
     */
    public TeamChannelCommand(String[] validExpressions, int permission, Bot bot, Main main)
    {
        super(validExpressions, permission, true);
        this.bot = bot;
        this.main = main;
        r = new SplittableRandom();
    }

    public TeamChannelCommand(List<String> validExpressions, int permission, Bot bot, Main main)
    {
        super(validExpressions, permission, true);
        this.bot = bot;
        this.main = main;
        r = new SplittableRandom();
    }

    /**
     * @see bowt.cmnd.Command#execute(bowt.evnt.impl.CommandEvent)
     */
    @Override
    public void execute(CommandEvent event)
    {
        boolean exceptUser = false;
        boolean slowmode = event.getMessage().getContent().contains("slow");
        int numberOfTeams = 0;

        int delay = 5000;

        if (event.getMessage().getContent().replace("slow (", "slow(").contains("slow("))
        {
            String message = event.getMessage().getContent().replace("slow (", "slow(");
            for (String s : message.split(" "))
            {
                if (s.contains("slow("))
                {
                    try
                    {
                        delay = Integer.parseInt(s.replace("slow(", "").replace(")", "").trim()) * 1000;

                        if (delay > 300000)
                        {
                            this.bot.sendMessage(
                                    "The slowmode delay time may not be higher than 300 seconds. The bot will use the default 5 second delay.",
                                    event.getMessage().getChannel(), Colors.RED);
                            delay = 5000;
                        }
                        if (delay < 0)
                        {
                            this.bot.sendMessage(
                                    "The slowmode delay time may not be lower than 0 seconds. The bot will use the default 5 second delay.",
                                    event.getMessage().getChannel(), Colors.RED);
                            delay = 5000;
                        }
                    }
                    catch (NumberFormatException e)
                    {
                        this.bot.sendMessage(
                                "Wrong format for the desired slowmode delay time. The bot will use the default 5 second delay.",
                                event.getMessage().getChannel(), Colors.RED);
                        delay = 5000;
                    }
                }
            }
        }

        List<List<IUser>> teams = new ArrayList<List<IUser>>();
        String[] parts = event.getMessage().getContent().split(" ");
        if (parts.length > 1 && parts[1].trim().equals("n"))
        {
            exceptUser = true;
            if (parts.length > 2)
            {
                try
                {
                    numberOfTeams = Integer.parseInt(parts[2]);
                    if (numberOfTeams <= 0)
                    {
                        numberOfTeams = 2;
                    }
                }
                catch (NumberFormatException e)
                {
                    numberOfTeams = 2;
                }
            }
        }
        else if (parts.length > 1)
        {
            try
            {
                numberOfTeams = Integer.parseInt(parts[1]);
                if (numberOfTeams <= 0)
                {
                    numberOfTeams = 2;
                }
            }
            catch (NumberFormatException e)
            {
                numberOfTeams = 2;
            }
        }
        else
        {
            numberOfTeams = 2;
        }
        List<IUser> allUsers = null;
        List<IUser> users = new ArrayList<IUser>();
        List<IVoiceChannel> channels = event.getGuild().getVoiceChannels();
        IVoiceChannel wantedChannel = null;
        for (IVoiceChannel voiceChannel : channels)
        {
            if (voiceChannel.getConnectedUsers().contains(event.getAuthor()))
            {
                wantedChannel = voiceChannel;
            }
        }
        if (wantedChannel != null)
        {
            allUsers = wantedChannel.getConnectedUsers();
        }
        else
        {
            this.bot.sendMessage("You have to be in a voicechannel.", event.getMessage().getChannel(), Colors.RED);
            return;
        }
        for (IUser user : allUsers)
        {
            if (!user.isBot())
            {
                if (exceptUser && user.equals(event.getAuthor()))
                {

                }
                else
                {
                    users.add(user);
                }
            }
        }
        if (users.size() < numberOfTeams)
        {
            this.bot.sendMessage("You don't have enough users for the teams.", event.getMessage().getChannel(),
                    Colors.RED);
            return;
        }

        if (users.size() / numberOfTeams > 15)
        {
            this.bot.sendMessage("Please choose a higher number of teams. The bot can only display 15 users per team.",
                    event.getMessage().getChannel(), Colors.RED);
            return;
        }

        Collections.shuffle(users);

        for (int i = 0; i < numberOfTeams; i ++ )
        {
            teams.add(new ArrayList<IUser>());
        }
        int num = 0;
        int currentTeam = 0;
        if (!slowmode)
        {
            while (!users.isEmpty())
            {
                if (currentTeam == teams.size())
                {
                    currentTeam = 0;
                }
                num = r.nextInt(users.size());
                teams.get(currentTeam).add(users.remove(num));
                currentTeam ++ ;
            }
            List<String> mentions = null;
            for (int i = 0; i < teams.size(); i ++ )
            {
                mentions = new ArrayList<String>();
                for (IUser user : teams.get(i))
                {
                    mentions.add(user.mention(false) + " \n(" + user.getDisplayName(event.getGuild())
                            + ")");
                }
                this.bot.sendListMessage("Team " + (i + 1), mentions, event.getChannel(), 15, false);
            }
        }
        else
        // slowmode
        {
            // setting up teammessages
            List<IMessage> messages = new ArrayList<>();
            for (int i = 0; i < teams.size(); i ++ )
            {
                messages.add(this.bot.sendMessage("Team " + (i + 1), event.getChannel()));
            }

            List<String> mentions = null;

            while (!users.isEmpty())
            {
                try
                {
                    Thread.sleep(delay);
                }
                catch (InterruptedException e)
                {
                }
                if (currentTeam == teams.size())
                {
                    currentTeam = 0;
                }
                num = r.nextInt(users.size());
                teams.get(currentTeam).add(users.remove(num));

                mentions = new ArrayList<String>();
                for (IUser user : teams.get(currentTeam))
                {
                    mentions.add(user.mention(false) + " \n(" + user.getDisplayName(event.getGuild())
                            + ")");
                }

                final int team = currentTeam;
                final List<String> finalMentions = mentions;

                try
                {
                    RequestBuffer.request(
                            () -> messages.get(team).edit(
                                    this.bot.createListEmbeds("Team " + (team + 1), finalMentions, 15, false).get(0)))
                            .get();
                }
                catch (Exception e)
                {

                }

                currentTeam ++ ;
            }
        }
        this.bot.sendMessage("The teams are finished, master.", event.getMessage().getChannel(), Colors.GREEN);
    }

    /**
     * @see bowt.cmnd.Command#getHelp()
     */
    @Override
    public EmbedObject getHelp(GuildObject guild)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.withAuthorName("Form Teams from Voicechannel Command");
        builder.withAuthorUrl("http://www.bowtiebots.xyz/_commands_.html#teams");
        builder.withAuthorIcon(Main.BOWTIE_BOT_ICON);

        builder.withDescription("|  Forms 2 (default) random teams with the users that \n"
                + "|  are in the same voicechannel as you.\n"
                + "|  \n"
                + "|  You can add a number after the command to change the number \n"
                + "|  of teams created. The bot can only put up to 15 people \n"
                + "|  into one team.\n"
                + "|  \n"
                + "|  You can make things exciting by letting the bot form the teams in \n"
                + "|  slow mode. For that simply add the word 'slow' in the command. \n"
                + "|  This will make the bot wait 5 seconds between every user that it \n"
                + "|  puts into a team. \n"
                + "|  You can specify a higher waiting time in brackets behind the 'slow'. \n"
                + "|  The time will be in seconds.\n"
                + "|");

        builder.appendField(
                "|  Usage:",
                "|  " + guild.getPrefix() + "teamchannel @user @user @role\n"
                        + "|  \n"
                        + "|  " + guild.getPrefix() + "teamchannel 4 @user @role\n"
                        + "|  \n"
                        + "|  " + guild.getPrefix() + "teamchannel 3 slow @user @user @role\n"
                        + "|  \n"
                        + "|  " + guild.getPrefix() + "teamchannel 5 slow(10) @user @user @role"
                        + "|", false);

        builder.appendField(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
                        + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
                "[This command needs " + UserPermissions.getPermissionString(this.getPermissionOverride(guild))
                        + " permissions](http://www.bowtiebots.xyz/master-system.html)", false);
        builder.withFooterText("Click the title to read about this command on the website.");
        return builder.build();
    }
}