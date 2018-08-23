package bowtie.bot.bots.cmd.misc;

import java.util.List;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;
import bowt.bot.Bot;
import bowt.cmnd.Command;
import bowt.evnt.impl.CommandEvent;
import bowt.guild.GuildObject;
import bowt.hand.impl.GuildCommandHandler;
import bowt.util.perm.UserPermissions;
import bowtie.core.Main;

/**
 * @author &#8904
 *
 */
public class HelpCommand extends Command
{
    private Bot bot;
    private Main main;

    /**
     * @param validExpressions
     * @param permission
     */
    public HelpCommand(String[] validExpressions, int permission, Bot bot, Main main)
    {
        super(validExpressions, permission);
        this.bot = bot;
        this.main = main;
    }

    public HelpCommand(List<String> validExpressions, int permission, Bot bot, Main main)
    {
        super(validExpressions, permission);
        this.bot = bot;
        this.main = main;
    }

    /**
     * @see bowtie.bot.obj.Command#execute(bowtie.evnt.impl.CommandEvent)
     */
    @Override
    public void execute(CommandEvent event)
    {
        String type = null;

        String cleanText = event.getMessage().getContent().replaceAll("(<.+>)", "").toLowerCase().trim();

        if (cleanText.equals("help") || cleanText.equals("info") || cleanText.equals("commands"))
        {
            type = "";
        }
        else
        {
            type = event.getMessage().getContent().toLowerCase()
                    .replace(event.getGuildObject().getPrefix() + event.getCommand(), "").trim();
        }
        EmbedObject helpMessage = null;

        if (type.equals(""))
        {
            helpMessage = getHelp(event.getGuildObject());
        }
        else
        {
            GuildObject guild = event.getGuildObject();
            Command command = ((GuildCommandHandler)guild.getCommandHandler()).getCommand(type);
            if (command != null)
            {
                helpMessage = command.getHelp(guild);
            }
        }
        if (helpMessage == null)
        {
            return;
        }
        try
        {
            bot.sendMessage(helpMessage, event.getChannel());
        }
        catch (Exception e)
        {
            Bot.errorLog.print(this, e);
        }
    }

    /**
     * @see bowtie.bot.obj.Command#getHelp()
     */
    @Override
    public EmbedObject getHelp(GuildObject guild)
    {
        EmbedBuilder builder = new EmbedBuilder();

        builder.withAuthorName("Help");
        builder.withAuthorIcon(Main.BOWTIE_BOT_ICON);

        builder.withDescription("|  This is the Bowtie Bot, a multi-purpose bot written by Lukas&#8904.\n"
                + "|  To read about the commands please [visit the website here](http://www.bowtiebots.xyz) or click\n"
                + "|  the direct links below. \n"
                + "|");

        builder.appendField(
                "| Free Premium Month Giveaway:",
                "|  [Vote at least 5 times for this bot to enter the giveaway](https://discordbots.org/bot/467705661745135629/vote).\n"
                        + "|  More information on the Bowtie Bots Discord server.\n"
                        + "|\n"
                        + "|\n"
                        + "|", false);

        if (main.getDatabase().isActivatedGuild(guild.getStringID()))
        {
            builder.appendField("| Commands:",
                    "|  [A full list of commands on the website](http://www.bowtiebots.xyz/_commands_.html)\n"
                            + "|", false);
        }
        else
        {
            builder.appendField("| Patron Commands:",
                    "|  [A full list of commands](http://www.bowtiebots.xyz/_commands_.html)\n"
                            + "|", false);

            builder.appendField(
                    "| Non-Patron Commands:",
                    "|  [A list of all commands that you can use without having to pay]"
                            + "(http://www.bowtiebots.xyz/_commands_non_patron_.html)\n"
                            + "|",
                    false);

            builder.appendField(
                    "| How do I unlock all features?",
                    "|  [A guide on how to activate the bot for your server]"
                            + "(http://www.bowtiebots.xyz/how-do-i-activate-the-bot.html)\n"
                            + "|",
                    false);
        }

        builder.appendField(
                "| What is the master system?",
                "|  [An explaination of the bots own master system]"
                        + "(http://www.bowtiebots.xyz/master-system.html)\n"
                        + "|",
                false);

        builder.appendField(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
                        + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
                "[This command needs " + UserPermissions.getPermissionString(this.getPermissionOverride(guild))
                        + " permissions](http://www.bowtiebots.xyz/master-system.html)", false);
        builder.appendField(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
                        + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
                "\n[Invite the bot](https://discordapp.com/api/oauth2/authorize?client_id=467705661745135629&permissions=0&scope=bot)"
                        + "\n[Join the Bowtie Bots server for feedback or questions](https://discord.gg/KRdQK8q)"
                        + "\n[If you like the bot please consider upvoting it](https://discordbots.org/bot/467705661745135629/vote)"
                        + "\n[Donate via PayPal](https://www.paypal.me/LukasHartwig)"
                        + "\n[Become a Patron](https://www.patreon.com/bowtiebots)"
                        + "\n[Stay informed on Twitter](https://twitter.com/Bowtie_Bots)", false);

        return builder.build();
    }
}