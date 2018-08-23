package bowtie.dbl;

import org.discordbots.api.client.DiscordBotListAPI;

import bowt.prop.Properties;

/**
 * @author &#8904
 *
 */
public final class BotListAPI
{
    private static DiscordBotListAPI api = new DiscordBotListAPI.Builder()
            .token(Properties.getValueOf("dblApiToken"))
            .botId(Properties.getValueOf("botID"))
            .build();

    public synchronized static void updateServerCount(int count)
    {
        api.setStats(count);
    }
}