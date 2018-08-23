package bowtie.bot.bots.cmd.activation;

import java.util.ArrayList;
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
public class ActivationsCommand extends Command
{
    private Bot bot;
    private Main main;

    /**
     * @param validExpressions
     * @param permission
     */
    public ActivationsCommand(String[] validExpressions, int permission, Bot bot, Main main)
    {
        super(validExpressions, permission, true);
        this.bot = bot;
        this.main = main;
    }

    public ActivationsCommand(List<String> validExpressions, int permission, Bot bot, Main main)
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
        if (event.getMessage().getContent().toLowerCase().contains("list"))
        {
            List<String> names = new ArrayList<>();

            List<String[]> ids = this.main.getDatabase().getActivatedGuilds();

            for (String[] pair : ids)
            {
                GuildObject guild = bot.getGuildObjectByID(pair[0]);

                IUser user = guild.getGuild().getUserByID(Long.parseLong(pair[1]));

                names.add(guild.getGuild().getName() + "\n(" + user.getName() + ")");
            }

            bot.sendListMessage("These are the servers on which this bot is activated:", names, event.getChannel(), 25,
                    false);
        }
        else
        {
            int count = this.main.getDatabase().activationCount();
            bot.sendMessage("The bot is currently active on `" + count + "` servers.", event.getChannel(), Colors.GREEN);
        }

    }

    /**
     * @see bowt.cmnd.Command#getHelp()
     */
    @Override
    public EmbedObject getHelp(GuildObject guild)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.withAuthorName("Activations Command");
        builder.withAuthorIcon(Main.BOWTIE_BOT_ICON);

        builder.withDescription("|  This command will show the number of servers that the bot\n"
                + "|  is activated on. You can add 'list' after the command to show a list with \n"
                + "|  the names of the servers.\n"
                + "|");

        builder.appendField(
                "|  Usage:",
                "|  " + guild.getPrefix() + "activations\n"
                        + "|  " + guild.getPrefix() + "activations list\n"
                        + "|", false);

        builder.appendField(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
                        + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
                "[This command needs " + UserPermissions.getPermissionString(this.getPermissionOverride(guild))
                        + " permissions](http://www.bowtiebots.xyz/master-system.html)", false);

        return builder.build();
    }
}