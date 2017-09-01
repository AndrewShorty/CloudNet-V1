package de.dytanic.cloudnet.logging;

import jline.console.ConsoleReader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 * Created by Tareko on 21.05.2017.
 */
@Getter
public class CloudNetLogging
        extends Logger {

    private final String separator = System.getProperty("line.separator");
    private final LoggingFormatter formatter = new LoggingFormatter();
    private final ConsoleReader reader;
    private final CloudNetLoggingConfig config;

    private final String value;
    private final boolean calc;

    public CloudNetLogging(String value) throws Exception
    {
        super("CloudNetServerLogger", null);

        config = new CloudNetLoggingConfig();
        calc = !System.getProperty("os.name").contains("Windows") && !System.getProperty("os.name").contains("windows");

        if(calc)
        AnsiConsole.systemInstall();

        new File("database").mkdir();
        new File("database/logs").mkdir();

        this.value = value;

        setLevel(Level.ALL);

        this.reader = new ConsoleReader(System.in, System.out);
        this.reader.setExpandEvents(false);

        FileHandler handler = new FileHandler("database/logs/cloudnet.log", 1 << 24, 8, true);
        handler.setFormatter(new FileFormatter());
        addHandler(handler);

        LoggingHandler loggingHandler = new LoggingHandler(reader);
        loggingHandler.setFormatter(formatter);
        loggingHandler.setLevel(Level.INFO);
        addHandler(loggingHandler);

        System.setOut(new PrintStream(new LoggingOutputStream(Level.INFO), true));
        System.setErr(new PrintStream(new LoggingOutputStream(Level.SEVERE), true));

    }

    public void shutdownAll()
    {
        AnsiConsole.systemUninstall();
        try
        {
            this.reader.killLine();
        } catch (IOException e) {}
        for(Handler handler : getHandlers()) handler.close();
    }

    @RequiredArgsConstructor
    private class LoggingOutputStream extends ByteArrayOutputStream {
        /*========================================================================*/
        private final Level level;

        @SuppressWarnings("deprecation")
        @Override
        public void flush() throws IOException
        {
            String contents = toString(StandardCharsets.UTF_8.name());
            super.reset();
            if (!contents.isEmpty() && !contents.equals(separator))
            {
                logp(level, "", "", contents);
            }
        }
    }

    private class LoggingHandler
            extends Handler {

        private final ConsoleReader reader;

        public LoggingHandler(ConsoleReader reader)
        {
            this.reader = reader;
        }

        @Override
        public void publish(LogRecord record)
        {
            if(isLoggable(record))handle(getFormatter().format(record));

        }

        public void handle(String message)
        {
            try{

                reader.print(message);
                reader.drawLine();
                reader.flush();

            }catch (Exception ex){

            }
        }

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException {}
    }

    private class FileFormatter
            extends Formatter {

        private final DateFormat format = new SimpleDateFormat("HH:mm:ss");

        @Override
        public String format(LogRecord record)
        {
            StringBuilder builder = new StringBuilder();
            if (record.getThrown() != null)
            {
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                builder.append(writer).append("\n");
            }

            return "[" + format.format(new Date()) + "/" + record.getLevel().getLocalizedName() + "]: " +
                    " " + formatMessage(record) + "\n" + builder.substring(0);
        }

    }

    private class LoggingFormatter
            extends Formatter {

        private final DateFormat format = new SimpleDateFormat("HH:mm:ss");
        private final DateFormat timeEather = new SimpleDateFormat("HH");

        @Override
        public String format(LogRecord record)
        {
            StringBuilder builder = new StringBuilder();
            if (record.getThrown() != null)
            {
                StringWriter writer = new StringWriter();
                record.getThrown().printStackTrace(new PrintWriter(writer));
                builder.append(writer).append("\n");
            }

            if(calc)
            {
                //!config.getProperties().getProperty("state").equalsIgnoreCase("OFF")
                if(config.getProperties().getProperty("dodaylightconsole").equalsIgnoreCase("true"))
                {
                    int value = Integer.parseInt(timeEather.format(System.currentTimeMillis()));
                    if(value < 19 && value > 7)
                        return ConsoleReader.RESET_LINE + AnsiColor.BLACK + "[" + AnsiColor.WHITE + format.format(System.currentTimeMillis()) + "/"
                                + record.getLevel().getLocalizedName() + AnsiColor.BLACK + "]: " + AnsiColor.GRAY + formatMessage(record) + "\n" + builder.substring(0);
                    else
                    {
                        return ConsoleReader.RESET_LINE + AnsiColor.GRAY + "[" + AnsiColor.RED + format.format(System.currentTimeMillis()) + "/"
                                + record.getLevel().getLocalizedName() + AnsiColor.GRAY + "]: " + AnsiColor.BLACK + formatMessage(record) + "\n" + builder.substring(0);
                    }
                }
                else
                {
                    switch (config.getProperties().getProperty("state").toUpperCase())
                    {
                        case "NIGHT":
                            return ConsoleReader.RESET_LINE + AnsiColor.GRAY + "[" + AnsiColor.RED + format.format(System.currentTimeMillis()) + "/"
                                    + record.getLevel().getLocalizedName() + AnsiColor.GRAY + "]: " + AnsiColor.BLACK + formatMessage(record) + "\n" + builder.substring(0);
                        case "DAY":
                            return ConsoleReader.RESET_LINE + AnsiColor.BLACK + "[" + AnsiColor.WHITE + format.format(System.currentTimeMillis()) + "/"
                                    + record.getLevel().getLocalizedName() + AnsiColor.BLACK + "]: " + AnsiColor.GRAY + formatMessage(record) + "\n" + builder.substring(0);
                        default:
                            return ConsoleReader.RESET_LINE + "[" + format.format(new Date()) + "/"
                                    + record.getLevel().getLocalizedName() + "]: " + formatMessage(record) + "\n" + builder.substring(0);
                    }
                }
            }
            else
                return ConsoleReader.RESET_LINE + "[" + format.format(new Date()) + "/"
                        + record.getLevel().getLocalizedName() + "]: " + formatMessage(record) + "\n" + builder.substring(0);
        }

    }
}