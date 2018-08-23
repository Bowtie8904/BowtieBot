package bowtie.bot.bots.cmd.activation;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;
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
public class GrantPremiumCommand extends Command
{
    private Bot bot;
    private Main main;

    /**
     * @param validExpressions
     * @param permission
     */
    public GrantPremiumCommand(String[] validExpressions, int permission, Bot bot, Main main)
    {
        super(validExpressions, permission, true);
        this.bot = bot;
        this.main = main;
    }

    public GrantPremiumCommand(List<String> validExpressions, int permission, Bot bot, Main main)
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
        List<IUser> users = event.getMentions();

        String servers = event.getParameter("servers");
        String durationString = event.getParameter("duration");

        if (servers == null)
        {
            this.bot.sendMessage("You have to add the amount of servers.", event.getMessage().getChannel(), Colors.RED);
            return;
        }

        if (durationString == null)
        {
            this.bot.sendMessage("You have to add the duration.", event.getMessage().getChannel(), Colors.RED);
            return;
        }

        if (users.isEmpty())
        {
            this.bot.sendMessage("You have to mention the users that should receive the premium status.", event
                    .getMessage().getChannel(), Colors.RED);
            return;
        }

        long duration = getDuration(durationString);

        Calendar date = Calendar.getInstance();
        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        date.setTimeInMillis(System.currentTimeMillis() + duration);

        String dateString = date.get(Calendar.DAY_OF_MONTH)
                + " "
                + getMonthName(date.get(Calendar.MONTH))
                + " "
                + date.get(Calendar.YEAR)
                + "  "
                + (date.get(Calendar.HOUR_OF_DAY) < 10 ? "0" + date.get(Calendar.HOUR_OF_DAY) : date
                        .get(Calendar.HOUR_OF_DAY))
                + ":" + (date.get(Calendar.MINUTE) < 10 ? "0" + date.get(Calendar.MINUTE) : date.get(Calendar.MINUTE))
                + " UTC";

        IRole serverRole = Activation.getPremiumRole("Premium " + servers + " ");
        IRole premiumRole = main.getHomeGuild().getRolesByName("Bowtie Premium").get(0);

        String nameString = "";

        if (serverRole != null)
        {
            for (IUser user : users)
            {
                RequestBuffer.request(() -> user.addRole(serverRole)).get();
                RequestBuffer.request(() -> user.addRole(premiumRole)).get();
                main.getDatabase().grantTempPremium(user.getStringID(), date.getTimeInMillis());

                nameString += user.mention() + "\n";
            }
        }

        bot.sendMessage("Temporary premium status was granted to:\n\n"
                + nameString + "\n\n"
                + "It will expire on\n\n"
                + dateString, event.getChannel(), Colors.GREEN);

        bot.sendMessage("Temporary premium status was granted to:\n\n"
                + nameString + "\n\n"
                + "It will expire on\n\n"
                + dateString, main.getTempChannel(), Colors.GREEN);
    }

    public long getDuration(String durationString)
    {
        long duration = 0;

        int weeks = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;
        int seconds = 0;

        if (durationString.toLowerCase().contains("w"))
        {
            weeks = getNumber(durationString, "w");
        }
        else if (durationString.toLowerCase().contains("d"))
        {
            days = getNumber(durationString, "d");
        }
        else if (durationString.toLowerCase().contains("h"))
        {
            hours = getNumber(durationString, "h");
        }
        else if (durationString.toLowerCase().contains("m"))
        {
            minutes = getNumber(durationString, "m");
        }
        else if (durationString.toLowerCase().contains("s"))
        {
            seconds = getNumber(durationString, "s");
        }

        duration += seconds * 1000L;
        duration += minutes * 60000L;
        duration += hours * 3600000L;
        duration += days * 86400000L;
        duration += weeks * 604800000L;

        return duration;
    }

    private int getNumber(String text, String replace)
    {
        String numberText = text.toLowerCase().replace(replace.toLowerCase(), "");
        int number = -1;
        try
        {
            number = Integer.parseInt(numberText);
        }
        catch (NumberFormatException e)
        {
        }
        return number;
    }

    /**
     * @see bowt.cmnd.Command#getHelp()
     */
    @Override
    public EmbedObject getHelp(GuildObject guild)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.withAuthorName("Grant Premium Command");
        builder.withAuthorIcon(Main.BOWTIE_BOT_ICON);

        builder.withDescription("|  Grants the tagged user/s the premium status.\n"
                + "|  You have to specify the parameter 'servers' and 'duration'.\n"
                + "|");

        builder.appendField(
                "|  Usage:",
                "|  " + guild.getPrefix() + "grantpremium -servers=1 -duration=30d @user\n"
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