import static ch.qos.logback.classic.Level.*
import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

appender("console", ConsoleAppender) {
    encoder (PatternLayoutEncoder) { pattern = "[%-5p-%thread] %c: %m%n" }
}

root(DEBUG, ["console"])
