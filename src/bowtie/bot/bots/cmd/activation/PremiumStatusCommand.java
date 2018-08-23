package bowtie.bot.bots.cmd.activation;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

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
public class PremiumStatusCommand extends Command
{
    private Bot bot;
    private Main main;

    /**
     * @param validExpressions
     * @param permission
     */
    public PremiumStatusCommand(String[] validExpressions, int permission, Bot bot, Main main)
    {
        super(validExpressions, permission, true);
        this.bot = bot;
        this.main = main;
    }

    public PremiumStatusCommand(List<String> validExpressions, int permission, Bot bot, Main main)
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
        IUser user = null;
        boolean forAuthor = false;

        if (event.getMentions().isEmpty())
        {
            user = event.getAuthor();
            forAuthor = true;
        }
        else
        {
            user = event.getMentions().get(0);
        }

        long expDate = main.getDatabase().getExpDate(user.getStringID());

        if (expDate == -1)
        {
            bot.sendMessage((forAuthor ? "You don't have a temporary premium status."
                    : "That person doesn't have a temporary premium status."), event.getChannel(), Colors.RED);
            return;
        }

        Calendar date = Calendar.getInstance();
        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        date.setTimeInMillis(expDate);

        String dateString = date.get(Calendar.DAY_OF_MONTH) + " "
                + getMonthName(date.get(Calendar.MONTH)) + " "
                + date.get(Calendar.YEAR) + "  "
                + date.get(Calendar.HOUR_OF_DAY) + ":" + date.get(Calendar.MINUTE) + " UTC";

        bot.sendMessage("The premium status will expire on:\n\n"
                + dateString, event.getChannel(), Colors.GREEN);
    }

    /**
     * @see bowt.cmnd.Command#getHelp()
     */
    @Override
    public EmbedObject getHelp(GuildObject guild)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.withAuthorName("Premium Status Command");
        builder.withAuthorIcon(Main.BOWTIE_BOT_ICON);

        builder.withDescription("|  Shows when the temporary premium status of either the author\n"
                + "|  or the tagged user will expire.\n"
                + "|");

        builder.appendField(
                "|  Usage:",
                "|  " + guild.getPrefix() + "premium\n"
                        + "|  " + guild.getPrefix() + "premium @user\n"
                        + "|", false);

        builder.appendField(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
                        + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
                "[This command needs " + UserPermissions.getPermissionString(this.getPermissionOverride(guild))
                        + " permissions](http://www.bowtiebots.xyz/master-system.html)", false);

        return builder.build();
    }

    public static String getMonthName(int month)
    {
        String name = "-1";

        switch (month)
        {
        case 0:
            name = "January";
            break;
        case 1:
            name = "February";
            break;
        case 2:
            name = "March";
            break;
        case 3:
            name = "April";
            break;
        case 4:
            name = "May";
            break;
        case 5:
            name = "June";
            break;
        case 6:
            name = "July";
            break;
        case 7:
            name = "August";
            break;
        case 8:
            name = "September";
            break;
        case 9:
            name = "October";
            break;
        case 10:
            name = "November";
            break;
        case 11:
            name = "December";
            break;
        }

        return name;
    }
}