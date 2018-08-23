package bowtie.bot.bots.cmd.activation;

import java.util.ArrayList;
import java.util.List;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import bowt.bot.Bot;
import bowt.cmnd.Command;
import bowt.evnt.impl.CommandEvent;
import bowt.guild.GuildObject;
import bowt.util.perm.UserPermissions;
import bowtie.bot.util.Activation;
import bowtie.core.Main;

/**
 * @author &#8904
 *
 */
public class MyActivationsCommand extends Command
{
    private Bot bot;
    private Main main;

    /**
     * @param validExpressions
     * @param permission
     */
    public MyActivationsCommand(String[] validExpressions, int permission, Bot bot, Main main)
    {
        super(validExpressions, permission, true);
        this.bot = bot;
        this.main = main;
    }

    public MyActivationsCommand(List<String> validExpressions, int permission, Bot bot, Main main)
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

        List<String> guildIDs = main.getDatabase().getGuildsForUser(userID);

        List<String> guildNames = new ArrayList<>();

        for (String guildID : guildIDs)
        {
            GuildObject guild = bot.getGuildObjectByID(guildID);

            if (guild != null)
            {
                guildNames.add(guild.getGuild().getName());
            }
        }

        for (int i = guildNames.size(); i < allowedServers; i ++ )
        {
            guildNames.add("<available activation>");
        }

        bot.sendListMessage("These are the servers on which you activated this bot:", guildNames, event.getChannel(),
                25, false);
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

        builder.withDescription("|  This command will show all servers that you have activated the bot on.\n"
                + "|");

        builder.appendField(
                "|  Usage:",
                "|  " + guild.getPrefix() + "myactivations\n"
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