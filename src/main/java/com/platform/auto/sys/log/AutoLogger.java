package com.platform.auto.sys.log;


import com.platform.auto.config.Config;
import com.platform.auto.util.AutoUtil;
import com.platform.auto.util.FileUtil;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
        if (StringUtils.isEmpty(format)) return;
        this.recordEventArgArray(Level.INFO, format, arguments);
    }

    @Override
    public void info(Throwable e) {
        this.recordEvent(Level.INFO, null, null, (Throwable) e);
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
        sb.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(loggingEvent.getTimeStamp())));
        sb.append(" " + loggingEvent.getLevel());
        sb.append(" " + formatLoggerName(loggingEvent.getLoggerName()));
        if (loggingEvent.getArgumentArray() != null) {
            sb.append(": " + replacePlaceholders(loggingEvent.getMessage(), loggingEvent.getArgumentArray()));
        }
        if (throwable != null) {
            sb.append(" " + getExceptionInfo(loggingEvent.getThrowable()));
        }
        if (!FileUtil.exists(Config.project_auto_path + "/" + Config.log_path_file_name)) {
//            System.out.println(sb);
            return;
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(Config.project_auto_path + "/" + Config.log_path_file_name, true))) {
            writer.newLine();  // 换行
            writer.write(sb.toString());
        } catch (IOException e) {
            File file = new File("D:\\ksm\\code\\playlet\\a.txt");
            if (!file.exists()) {
                return;
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
                writer.newLine();  // 换行
                writer.write(sb.toString());
            } catch (IOException ex) {
            }
        }
    }

    public String replacePlaceholders(String msg, Object[] args) {
        if (args == null) {
            return msg;
        }
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


    public static String formatLoggerName(String loggerName) {
        if (loggerName.length() > 40) {
            return loggerName.substring(loggerName.length() - 40);
        } else {
            StringBuilder sb = new StringBuilder();
            int spacesToAdd = 40 - loggerName.length();
            sb.append(loggerName);
            for (int i = 0; i < spacesToAdd; i++) {
                sb.append(' ');
            }
            return sb.toString();
        }
    }


}
