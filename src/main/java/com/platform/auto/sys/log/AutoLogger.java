package com.platform.auto.sys.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoLogger implements Logger {

    String name;

    public AutoLogger(String name) {
        this.name = name;
    }

    public static Logger getLogger(Class<?> clazz) {
        return new AutoLogger(clazz.getName());
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void info(String format, Object... arguments) {
        this.recordEventArgArray(Level.INFO, format, arguments);
    }

    private void recordEventArgArray(Level level, String msg, Object[] args) {
        Throwable throwableCandidate = MessageFormatter.getThrowableCandidate(args);
        if (throwableCandidate != null) {
            Object[] trimmedCopy = MessageFormatter.trimmedCopy(args);
            this.recordEvent(level, msg, trimmedCopy, throwableCandidate);
        } else {
            this.recordEvent(level, msg, args, (Throwable) null);
        }
    }

    private void recordEvent(Level level, String msg, Object[] args, Throwable throwable) {
        SubstituteLoggingEvent loggingEvent = new SubstituteLoggingEvent();
        loggingEvent.setTimeStamp(System.currentTimeMillis());
        loggingEvent.setLevel(level);
        loggingEvent.setLoggerName(this.name);
        loggingEvent.setMessage(msg);
        loggingEvent.setThreadName(Thread.currentThread().getName());
        loggingEvent.setArgumentArray(args);
        loggingEvent.setThrowable(throwable);
        StringBuilder sb = new StringBuilder();
        sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(loggingEvent.getTimeStamp())));
        sb.append(" " + loggingEvent.getLevel());
        sb.append(" " + loggingEvent.getLoggerName());
        sb.append(": " + replacePlaceholders(loggingEvent.getMessage(), loggingEvent.getArgumentArray()));
        if (throwable != null) {
            sb.append(" " + getExceptionInfo(loggingEvent.getThrowable()));
        }
        System.out.println(sb);
    }

    public String replacePlaceholders(String msg, Object[] args) {
        Pattern pattern = Pattern.compile("\\{\\}");
        Matcher matcher = pattern.matcher(msg);
        StringBuilder result = new StringBuilder();
        int index = 0;
        while (matcher.find()) {
            if (index < args.length) {
                matcher.appendReplacement(result, args[index] != null ? args[index].toString() : "");
                index++;
            } else {
                break;
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public static String getExceptionInfo(Throwable e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        printWriter.close();
        return stringWriter.toString();
    }


}
