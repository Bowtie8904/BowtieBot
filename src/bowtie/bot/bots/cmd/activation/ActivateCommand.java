package bowtie.bot.bots.cmd.activation;

import java.util.List;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import bowt.bot.Bot;
import bowt.cmnd.Command;
import bowt.cons.Colors;
import bowt.evnt.impl.CommandEvent;
import bowt.guild.GuildObject;
import bowt.util.perm.UserPermissions;
import bowtie.bot.util.Activation;
import bowtie.core.Main;

/**
 * @author &#8904
 *
 */
public class ActivateCommand extends Command
{
    private Bot bot;
    private Main main;

    /**
     * @param validExpressions
     * @param permission
     */
    public ActivateCommand(String[] validExpressions, int permission, Bot bot, Main main)
    {
        super(validExpressions, permission, true);
        this.bot = bot;
        this.main = main;
    }

    public ActivateCommand(List<String> validExpressions, int permission, Bot bot, Main main)
    {
        super(validExpressions, permission, true);
        this.bot = bot;
        this.main = main;
    }

    /**
     * @see bowt.cmnd.Command#execute(bowt.evnt.impl.CommandEvent)
     */
    @Override
    public void execute(CommandEvent event)
    {
        IUser user = event.getAuthor();
        String userID = user.getStringID();
        IGuild home = main.getHomeGuild();

        int allowedServers = Activation.getAllowedServers(user);

        if (allowedServers == 0)
        {
            bot.sendMessage(
                    "You need to be a member of the [Bowtie Bots Discord](https://discord.gg/KRdQK8q) "
                            + "server and have a premium role in order to activate the bot.\n\n"
                            + "Support me on [Patreon](https://www.patreon.com/bowtiebots) to get a premium role, then you will be "
                            + "able to activate all of my bots.", event.getChannel(), Colors.RED);
            return;
        }

        int activatedServers = main.getDatabase().activationCount(userID);

        if (allowedServers <= activatedServers)
        {
            bot.sendMessage("You have reached your activation limit of " + allowedServers + ". "
                    + "You can use the '" + event.getGuildObject().getPrefix()
                    + "deactivate' command to deactivate the bot for the current server, after "
                    + "that you can activate it on another one."
                    + "To increase the server limit please select a higher Patreon tier.", event.getChannel(),
                    Colors.RED);
            return;
        }

        if (!main.getDatabase().activateGuild(event.getGuildObject().getStringID(), userID))
        {
            return;
        }

        this.bot.sendMessage("The bot has successfully been activated for this server, have fun! \n\n"
                + "You currently have " + (activatedServers + 1) + "/" + allowedServers + " servers activated.\n\n"
                + "Remember NOT to leave the Bowtie Bots server, the bot will deactivate if you do.",
                event.getMessage().getChannel(), Colors.GREEN);
    }

    /**
     * @see bowt.cmnd.Command#getHelp()
     */
    @Override
    public EmbedObject getHelp(GuildObject guild)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.withAuthorName("Activate Command");
        builder.withAuthorUrl("http://www.bowtiebots.xyz/_commands_.html#activation");
        builder.withAuthorIcon(Main.BOWTIE_BOT_ICON);

        builder.withDescription("|  This command will activate the bot on this server if\n"
                + "|  you are supporting [Bowtie Bots on Patreon](https://www.patreon.com/bowtiebots)\n"
                + "|  or received a premium role on the bowtie Bots Discord server \n"
                + "|  for some other reason.\n"
                + "|  \n"
                + "|  You can read more about the activation process [here](http://www.bowtiebots.xyz/how-do-i-activate-the-bot.html).\n"
                + "|");

        builder.appendField(
                "|  Usage:",
                "|  " + guild.getPrefix() + "activate\n"
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