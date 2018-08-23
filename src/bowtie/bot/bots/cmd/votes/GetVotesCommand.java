package bowtie.bot.bots.cmd.votes;

import java.util.List;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.RequestBuffer;
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
public class GetVotesCommand extends Command
{
    private Main main;

    /**
     * @param validExpressions
     * @param permission
     */
    public GetVotesCommand(String[] validExpressions, int permission, Main main)
    {
        super(validExpressions, permission);
        this.main = main;
    }

    /**
     * @see bowtie.bot.obj.Command#execute(bowtie.evnt.impl.CommandEvent)
     */
    @Override
    public void execute(CommandEvent event)
    {
        int votes = main.getDatabase().getVoteCount(event.getAuthor().getStringID());
        votes = votes == -1 ? 0 : votes;

        if (event.getGuild().getStringID().equals(main.getHomeGuild().getStringID()))
        {
            List<IRole> roles = main.getHomeGuild().getRolesByName("I voted!");
            List<IRole> giveawayRoles = main.getHomeGuild().getRolesByName("Monthly Premium Giveaway");

            if (!roles.isEmpty())
            {
                IRole voteRole = roles.get(0);
                RequestBuffer.request(() -> event.getAuthor().addRole(voteRole)).get();
            }

            if (!giveawayRoles.isEmpty())
            {
                IRole giveawayRole = giveawayRoles.get(0);

                if (votes >= Main.ENTER_VOTES)
                {
                    RequestBuffer.request(() -> event.getAuthor().addRole(giveawayRole)).get();

                    main.getBot().sendMessage("You have voted " + votes + " times this month.\n\n"
                            + "You are now able to enter the monthly giveaway of "
                            + "a free Botwie Bot premium month, which allows you to "
                            + "use all commands! If you are already a Patron I will cover "
                            + "your payment for the next month over PayPal.\n\n"
                            + "You have been granted the giveaway role. Keep an eye on this server "
                            + "to see if you won. Good luck!", event.getChannel(), Colors.GREEN);
                    return;
                }
            }
        }

        if (votes < Main.ENTER_VOTES)
        {
            main.getBot().sendMessage("You have voted " + votes + " times this month.\n\n"
                    + "You have to vote at least " + Main.ENTER_VOTES + " times in total "
                    + "to enter the free premium month giveaway.", event.getChannel(), Colors.DEFAULT);
        }
        else
        {
            main.getBot().sendMessage("You have voted " + votes + " times this month.\n\n"
                    + "You are now able to enter the monthly giveaway of "
                    + "a free Botwie Bot premium month, which allows you to "
                    + "use all commands! If you are already a Patron I will cover "
                    + "your payment for the next month over PayPal.\n\n"
                    + "Join the [Bowtie Bots server](https://discord.gg/KRdQK8q) and use "
                    + "this command again to claim your giveaway roles to enter. Good luck!", event.getChannel(),
                    Colors.GREEN);
        }
    }

    /**
     * @see bowt.cmnd.Command#getHelp(bowt.guild.GuildObject)
     */
    @Override
    public EmbedObject getHelp(GuildObject guild)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.withAuthorName("Get Votes Command");
        builder.withAuthorUrl("http://www.bowtiebots.xyz/_commands_.html#votes");
        builder.withAuthorIcon(Main.BOWTIE_BOT_ICON);

        builder.withDescription("|  Shows the number of times you have voted \n"
                + "|  this month. \n"
                + "|  \n"
                + "|  You need to vote at least " + Main.ENTER_VOTES + " times\n"
                + "|  to enter the premium giveaway!\n"
                + "|");

        builder.appendField(
                "|  Usage:",
                "|  " + guild.getPrefix() + "votes \n"
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