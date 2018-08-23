package bowtie.bot.bots.cmd.activation;

import java.util.List;

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
public class RevokePremiumCommand extends Command
{
    private Bot bot;
    private Main main;

    /**
     * @param validExpressions
     * @param permission
     */
    public RevokePremiumCommand(String[] validExpressions, int permission, Bot bot, Main main)
    {
        super(validExpressions, permission, true);
        this.bot = bot;
        this.main = main;
    }

    public RevokePremiumCommand(List<String> validExpressions, int permission, Bot bot, Main main)
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
        String nameString = "";

        for (IUser user : users)
        {
            IRole premiumRole = main.getHomeGuild().getRolesByName("Bowtie Premium").get(0);

            for (IRole role : Activation.getPremiumRoles())
            {
                RequestBuffer.request(() -> user.removeRole(role)).get();
                RequestBuffer.request(() -> user.removeRole(premiumRole)).get();
            }

            main.getDatabase().clearUserActivations(user.getStringID());
            main.getDatabase().revokeTempPremium(user.getStringID());

            nameString += user.mention() + "\n";
        }

        bot.sendMessage("Revoked all premium benefits of:\n\n"
                + nameString + "\n\n"
                + "All servers were deactivted.", event.getChannel(), Colors.GREEN);

        bot.sendMessage("Revoked all premium benefits of:\n\n"
                + nameString + "\n\n"
                + "All servers were deactivted.", main.getTempChannel(), Colors.RED);
    }

    /**
     * @see bowt.cmnd.Command#getHelp()
     */
    @Override
    public EmbedObject getHelp(GuildObject guild)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.withAuthorName("Revoke Premium Command");
        builder.withAuthorIcon(Main.BOWTIE_BOT_ICON);

        builder.withDescription("|  revokes the premium status of the tagged user/s.\n"
                + "|  This will deactivate all servers that they activated the bot on.\n"
                + "|");

        builder.appendField(
                "|  Usage:",
                "|  " + guild.getPrefix() + "revokepremium @user\n"
                        + "|", false);

        builder.appendField(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
                        + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
                "[This command needs " + UserPermissions.getPermissionString(this.getPermissionOverride(guild))
                        + " permissions](http://www.bowtiebots.xyz/master-system.html)", false);

        return builder.build();
    }
}