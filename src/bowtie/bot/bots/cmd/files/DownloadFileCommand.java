package bowtie.bot.bots.cmd.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;
import bowt.bot.Bot;
import bowt.cmnd.Command;
import bowt.evnt.impl.CommandEvent;
import bowt.guild.GuildObject;
import bowt.util.perm.UserPermissions;
import bowtie.core.Main;

import com.vdurmont.emoji.EmojiManager;

/**
 * @author &#8904
 *
 */
public class DownloadFileCommand extends Command
{
    private Bot bot;
    private Main main;

    /**
     * @param validExpressions
     * @param permission
     */
    public DownloadFileCommand(String[] validExpressions, int permission, Bot bot, Main main)
    {
        super(validExpressions, permission);
        this.bot = bot;
        this.main = main;
    }

    /**
     * @param validExpressions
     * @param permission
     */
    public DownloadFileCommand(List<String> validExpressions, int permission, Bot bot, Main main)
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
        String[] parts = event.getMessage().getContent().trim().split(" ");
        String path = event.getMessage().getAttachments().get(0).getFilename();
        if (parts.length > 1)
        {
            path = parts[1];
        }
        download(path, event);
    }

    private void download(String path, CommandEvent event)
    {
        URL website;
        try
        {
            website = new URL(event.getMessage().getAttachments().get(0).getUrl());
            URLConnection conn = website.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            try (InputStream in = conn.getInputStream())
            {
                File file = new File(path);
                file.mkdirs();
                Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
                event.getMessage().addReaction(EmojiManager.getForAlias("white_check_mark"));
            }
        }
        catch (MalformedURLException e)
        {
            Bot.errorLog.print(this, e);
        }
        catch (DirectoryNotEmptyException e)
        {
            String newPath = path + (path.endsWith("/") ? "" : "/")
                    + event.getMessage().getAttachments().get(0).getFilename();
            download(newPath, event);
        }
        catch (IOException e)
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

        builder.withAuthorName("Download File Command");
        builder.withAuthorIcon(Main.BOWTIE_BOT_ICON);

        builder.withDescription("|  Soon\n"
                + "|");

        builder.appendField(
                "|  Usage:",
                "|  " + guild.getPrefix() + "\n"
                        + "|", false);

        builder.appendField(
                "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
                        + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",
                "[This command needs " + UserPermissions.getPermissionString(this.getPermissionOverride(guild))
                        + " permissions](http://www.bowtiebots.xyz/master-system.html)", false);

        return builder.build();
    }
}