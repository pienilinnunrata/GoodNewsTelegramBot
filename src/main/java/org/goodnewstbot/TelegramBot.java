package org.goodnewstbot;

import org.goodnewstbot.exceptions.NoNewsException;
import org.goodnewstbot.exceptions.RSSException;
import org.goodnewstbot.model.NewsArticle;
import org.goodnewstbot.news.NewsClient;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.goodnewstbot.news.NewsClient.*;


public class TelegramBot extends TelegramLongPollingBot {

    Environment env = new Environment();

    private enum MessageType {
        START,
        INFO,
        NEWS_RSS,
        NEWS_RU,
        NEWS_WORLD,
        INVALID
    }

    @Override
    public String getBotUsername() {
        return env.getEnvValue("BOT_NAME");
    }

    @Override
    public String getBotToken() {
        return env.getEnvValue("TOKEN");
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());
            message.enableMarkdown(true);
            String responseText = getResponseText(parseUserMessage(update.getMessage().getText())).replaceAll("([_])", "\\\\$1");
            message.setText(responseText);
            logMessage(update.getMessage().getChat().getUserName(), update.getMessage().getText(), responseText);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                System.out.println("Error while sending response");
                e.printStackTrace();
            }
        }
    }

    private void logMessage(String userName, String userMessage, String botResponseMessage) {
        System.out.println("\n ----------------------------");
        LocalDateTime date = LocalDateTime.now();
        System.out.println(DateTimeFormatter.ofPattern("dd MMM yyyy в HH:mm").format(date));
        System.out.printf("Received message from %s: \n%s\n", userName, userMessage);
        System.out.println("\nResponded with: \n" + botResponseMessage);
    }

    private String getResponseText(MessageType type) {
        switch (type) {
            case INVALID:
                return "Что-то странное вы спросили меня. \nНапишите */news*, */news мир* или */news россия*, чтобы получить новость или */info*, чтобы узнать информацию о боте";
            case START:
                return "Даров! Напиши */news*, чтобы получить рандомную новость 🌝 \nПодробнее о возможностях бота можешь узнать написав */info*";
            case INFO:
                return "Я отфильтровываю новости по ключевым словам, связанными с трагическими вестями. " +
                        "\nНапиши */news*, чтобы получить какую-нибудь новость 🌚" +
                        "\nНапиши */news мир*, чтобы получить новость из мира 🌍" +
                        "\nНапиши */news россия*, чтобы получить новость из России 👀";
            case NEWS_RSS:
                return getNewsArticleMessage(RSS);
            case NEWS_RU:
                return getNewsArticleMessage(RU_NEWS);
            case NEWS_WORLD:
                return getNewsArticleMessage(WORLD_NEWS);
            default:
                return "Я не понимаю что происходит, чувак...";
        }
    }

    private String getNewsArticleMessage(String newsType) {
        try {
            NewsArticle article = new NewsClient(newsType).getRandomArticle();
            return String.format("👁👄👁 *%s*\n\n"
                            + "%s\n\n\n"
                            + "Источник: %s\n"
                            + "Категория: %s\n\n"
                            + "Дата публикации: %s\n\n"
                            + "✨",
                    article.getTitle(),
                    article.getDesc(),
                    article.getLink(),
                    article.getCategory(),
                    article.getFormattedPubDate()
            );
        } catch (RSSException e) {
            return "Источник новостей походу все :( Можешь пойти чай налить пока";
        }
        catch (NoNewsException e) {
            return "А вот нету статей, которые бы прошли фильтр :( Может пора посмотреть сериальчик?";
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private MessageType parseUserMessage(String message) {
        String[] words = message.split("\\s+");
        if (words.length > 2) return MessageType.INVALID;
        switch (words[0]) {
            case "/start":
                return MessageType.START;
            case "/info":
                return MessageType.INFO;
            case "/news":
                if (words.length == 1) return MessageType.NEWS_RSS;
                switch (words[1].toUpperCase()) {
                    case "РОССИЯ":
                        return MessageType.NEWS_RU;
                    case "МИР":
                        return MessageType.NEWS_WORLD;
                    default:
                        return MessageType.INVALID;
                }
            default:
                return MessageType.INVALID;
        }
    }
}
