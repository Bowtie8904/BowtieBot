package bowtie.bot.bots.cmd.votes;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;
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
public class ResetVotesCommand extends Command
{
    private Main main;

    /**
     * @param validExpressions
     * @param permission
     */
    public ResetVotesCommand(String[] validExpressions, int permission, Main main)
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
        main.getDatabase().resetVoteCounts();
        main.getBot().sendMessage("Votes have been reset.", event.getChannel(), Colors.GREEN);
    }

    /**
     * @see bowt.cmnd.Command#getHelp(bowt.guild.GuildObject)
     */
    @Override
    public EmbedObject getHelp(GuildObject guild)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.withAuthorName("Reset Votes Command");
        builder.withAuthorIcon(Main.BOWTIE_BOT_ICON);

        builder.withDescription("|  Resets the vote countss for the current month.\n"
                + "|");

        builder.appendField(
                "|  Usage:",
                "|  " + guild.getPrefix() + "resetvotes \n"
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