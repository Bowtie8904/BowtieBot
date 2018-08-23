package bowtie.bot.bots.cmd.activation;

import java.util.List;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
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
public class DeactivateCommand extends Command
{
    private Bot bot;
    private Main main;

    /**
     * @param validExpressions
     * @param permission
     */
    public DeactivateCommand(String[] validExpressions, int permission, Bot bot, Main main)
    {
        super(validExpressions, permission, true);
        this.bot = bot;
        this.main = main;
    }

    public DeactivateCommand(List<String> validExpressions, int permission, Bot bot, Main main)
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

        if (event.getMessage().getContent().toLowerCase().contains("all"))
        {
            List<String> guildIDs = main.getDatabase().getGuildsForUser(userID);

            for (String guildID : guildIDs)
            {
                main.getDatabase().deactivateGuild(guildID, userID);
            }

            bot.sendMessage("The bot has been deactivated for all of your previously activated servers.",
                    event.getChannel(), Colors.GREEN);
        }
        else if (main.getDatabase().hasActivated(event.getGuildObject().getStringID(), userID))
        {
            main.getDatabase().deactivateGuild(event.getGuildObject().getStringID(), userID);
            bot.sendMessage(
                    "The bot has been deactivated for this server. You can now re-activate it on another server.",
                    event.getChannel(), Colors.GREEN);
        }

    }

    /**
     * @see bowt.cmnd.Command#getHelp()
     */
    @Override
    public EmbedObject getHelp(GuildObject guild)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.withAuthorName("Deactivate Command");
        builder.withAuthorUrl("http://www.bowtiebots.xyz/_commands_.html#activation");
        builder.withAuthorIcon(Main.BOWTIE_BOT_ICON);

        builder.withDescription("|  This command will deactivate the bot on this server.\n"
                + "|  You can add 'all' after the command to deactivate the bot on\n"
                + "|  all servers that you activated it on.\n"
                + "|");

        builder.appendField(
                "|  Usage:",
                "|  " + guild.getPrefix() + "deactivate\n"
                        + "|  " + guild.getPrefix() + "deactivate all\n"
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