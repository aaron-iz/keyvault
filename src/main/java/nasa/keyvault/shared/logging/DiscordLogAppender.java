package nasa.keyvault.shared.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import nasa.keyvault.shared.external.DiscordWebhookRequest;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class DiscordLogAppender extends AppenderBase<ILoggingEvent> {
    private final static String MessageFormat = "[%s]  %s: %s\n";
    private final static int DiscordMaxMessageLength = 2000;

    private final RestTemplate restTemplate = new RestTemplate();
    private final Queue<String> buffer = new ConcurrentLinkedDeque<>();
    private String webhookUri;

    public DiscordLogAppender() { }

    private void emptyBuffer() {
        while (!buffer.isEmpty()) {
            var length = 0;
            var list = new LinkedList<String>();

            while (!buffer.isEmpty()) {
                if (length + buffer.peek().length() > DiscordMaxMessageLength) {
                    break;
                }

                // Ignore messages too big :(
                if (buffer.peek().length() > DiscordMaxMessageLength) {
                    buffer.poll();
                    continue;
                }

                var next = buffer.poll();
                list.add(next);
                length += next.length();
            }

            var builder = new StringBuilder();
            while (!list.isEmpty()) {
                builder.append(list.poll());
            }

            sendToDiscord(builder.toString());
        }
    }

    private void sendToDiscord(String content) {
        var payload = new DiscordWebhookRequest(content);
        CompletableFuture.runAsync(() -> {
            try {
                restTemplate.postForEntity(webhookUri, payload, String.class);
            } catch (Exception ex) {
                // re-offer the message, this will mess with timeline
                buffer.offer(content);
            }
        });
    }

    @Override
    protected void append(ILoggingEvent iLoggingEvent) {
        var msg = MessageFormat.formatted(
                iLoggingEvent.getLevel(),
                iLoggingEvent.getLoggerName(),
                iLoggingEvent.getFormattedMessage());

        buffer.offer(msg);
    }

    @Override
    public void start() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this::emptyBuffer, 0, 1, TimeUnit.SECONDS);

        super.start();
    }

    public void setWebhookUri(String webhookUri) {
        this.webhookUri = webhookUri;
    }
}
