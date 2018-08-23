package bowtie.dbl;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.RequestBuffer.IRequest;
import bowt.bot.Bot;
import bowtie.core.Main;
import bowtie.json.JSONBuilder;

/**
 * @author &#8904
 *
 */
@RestController
public final class VoterUpdater
{
    private static Main main;

    public static void setMain(Main main)
    {
        VoterUpdater.main = main;
    }

    @PostMapping("/api/bowtiebot/userVoted")
    public String userVoted(@RequestHeader("Authorization") String authPass, @RequestBody Map<String, String> body)
    {
        if (!authPass.equals(Main.AUTH_PASSWORD))
        {
            return new JSONBuilder().put("Error", "Authentication error").toString();
        }

        String userID = body.get("user");

        if (userID != null)
        {
            List<IRole> roles = main.getHomeGuild().getRolesByName("I voted!");
            List<IRole> giveawayRoles = main.getHomeGuild().getRolesByName("Monthly Premium Giveaway");

            if (!roles.isEmpty())
            {
                IRole voteRole = roles.get(0);

                if (voteRole != null)
                {
                    IUser user = RequestBuffer.request(new IRequest<IUser>()
                    {
                        @Override
                        public IUser request()
                        {
                            return main.getBot().getClient().fetchUser(Long.parseLong(userID));
                        }
                    }).get();

                    if (user != null)
                    {
                        int votes = main.getDatabase().getVoteCount(user.getStringID());
                        votes = votes == -1 ? 0 : votes;

                        main.getDatabase().setVoteCount(user.getStringID(), votes + 1);

                        if (!giveawayRoles.isEmpty())
                        {
                            IRole giveawayRole = giveawayRoles.get(0);

                            if ((votes + 1) >= Main.ENTER_VOTES)
                            {
                                RequestBuffer.request(() -> user.addRole(giveawayRole)).get();
                            }
                        }

                        RequestBuffer.request(() -> user.addRole(voteRole)).get();
                        Bot.log.print(user.getName() + "#" + user.getDiscriminator() + " voted!");

                        return new JSONBuilder().put("Response", "Received vote").toString();
                    }
                }
                else
                {
                    return new JSONBuilder().put("Response", "Received vote. Role not found though.").toString();
                }
            }
            else
            {
                return new JSONBuilder().put("Response", "Received vote. Role not found though.").toString();
            }
        }
        return new JSONBuilder().put("Error", "Invalid user").toString();
    }
}