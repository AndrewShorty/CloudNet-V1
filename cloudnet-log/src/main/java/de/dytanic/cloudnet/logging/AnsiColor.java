package de.dytanic.cloudnet.logging;

import org.fusesource.jansi.Ansi;

/**
 * Created by Tareko on 05.07.2017.
 */
public final class AnsiColor {

    private AnsiColor()
    {
    }

    public static final String WHITE = Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).bold().toString();

    public static final String RED = Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.RED).bold().toString();

    public static final String GREEN = Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.GREEN).bold().toString();

    public static final String BLUE = Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLUE).bold().toString();

    public static final String CYAN = Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.CYAN).bold().toString();

    public static final String BLACK = Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.BLACK).bold().toString();

    public static final String GRAY = Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.WHITE).boldOff().toString();

    public static final String DEFAULT = Ansi.ansi().a(Ansi.Attribute.RESET).fg(Ansi.Color.DEFAULT).boldOff().toString();

}