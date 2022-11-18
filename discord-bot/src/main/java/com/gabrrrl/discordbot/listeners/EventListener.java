package com.gabrrrl.discordbot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.GenericMessageReactionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class EventListener extends ListenerAdapter {
    @Override
    public void onGenericMessageReaction(GenericMessageReactionEvent event) {
        User user = event.getUser();
        String emoji = event.getReaction().getEmoji().getAsReactionCode();
        String whatChannel = event.getChannel().getAsMention();

        String message = user.getAsTag() + " has given you a " + emoji + " in the " + whatChannel + " channel!";

        event.getGuild().getDefaultChannel().asStandardGuildMessageChannel().sendMessage(message).queue();

    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        if (message.contains("bot")) {
//            event.getChannel().sendMessage("I'm here. \n ").queue();
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Hello There!")
                    .setDescription("Here are some commands:\n" +
                            " **/meme** will return a random meme from Reddit \n\n If you forget just say **bot**")
//                    .setImage(url)
                    .setColor(Color.RED);
            event.getChannel().sendMessageEmbeds(builder.build()).queue();
        } else if (message.contains("/meme")) {
            String postLink = "";
            String title = "";
            String url = "";
            JSONParser parse = new JSONParser();
            try {
                URL memeLink = new URL("https://meme-api.herokuapp.com/gimme");
                BufferedReader read = new BufferedReader(new InputStreamReader(memeLink.openConnection().getInputStream()));
                String input;
                while ((input = read.readLine()) != null) {
                    JSONArray array = new JSONArray();
                    array.add(parse.parse(input));
                    for (Object data : array) {
                        JSONObject jsonObject = (JSONObject) data;
                        postLink = (String) jsonObject.get("postLink");
                        title = (String) jsonObject.get("title");
                        url = (String) jsonObject.get("url");
                    }
                }
                read.close();
                event.getMessage().delete().queue();

                EmbedBuilder builder = new EmbedBuilder()
                        .setTitle(title, postLink)
                        .setImage(url)
                        .setColor(Color.RED);
                event.getChannel().sendMessageEmbeds(builder.build()).queue();
            } catch (MalformedURLException e) {
                System.out.println("ERROR: Invalid link!");
            } catch (IOException e) {
                System.out.println("ERROR: Reading file!");
            } catch (ParseException e) {
                System.out.println("ERROR: Couldn't parse data!");
            }
        }
    }
}
