package com.dragonzone.jsf;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

@ManagedBean
@RequestScoped
public class TerminalControlBean extends BaseControlBean {

    public enum CommandEnum {

        date("Get current system date."),
        help("List all available commands."),
        list("List all available commands."),
        ping("Determines the latency (communication delay) between this computer and another computer on a network"),
        tail("Display last specified number of lines.");
        private final String description;

        private CommandEnum(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
    @ManagedProperty("#{terminalBean}")
    private TerminalBean terminalBean;

    @Override
    public void preLoadPage() {
        try {
            terminalBean.setHostAddress(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            terminalBean.setHostAddress("");
        }
    }

    public String handleCommand(String command, String[] params) {
        String result = "";

        command = StringUtils.stripToNull(command);
        if (command != null) {
            CommandEnum cmd = null;
            try {
                cmd = CommandEnum.valueOf(command);
            } catch (IllegalArgumentException e) {
                result = command + " not found. Type 'list' to view all available commands.";
            }
            if (cmd != null) {
                switch (cmd) {
                    case date:
                        result = new Date().toString();
                        break;
                    case tail:
                        int lines = 0;
                        if (params.length == 1) {
                            lines = 10;
                        } else if (params.length == 2) {
                            lines = Integer.parseInt(params[1]);
                        } else {
                            result = "Usage: tail [FILENAME] [NUMBER OF LINES], e.g tail //servername/d$/temp/mylog.log 10";
                        }
                        if (lines > 0) {
                            String tmpStr = tail(new File(params[0]), lines);
                            result = tmpStr == null
                                    ? ""
                                    : StringUtils.replace(StringEscapeUtils.escapeHtml4(tmpStr), "\n", "<br/>");
                        }

                        break;
                    case ping:
                        result = ping(params[0])
                                ? "Host is reachable"
                                : "Host is NOT reachable";
                        break;
                    case help:
                    case list:
                        StringBuilder sb = new StringBuilder();
                        CommandEnum[] commands = CommandEnum.values();
                        for (CommandEnum commandEnum : commands) {
                            sb.append(commandEnum.name())
                                    .append(" - ")
                                    .append(commandEnum.getDescription())
                                    .append("<br/>");
                        }
                        result = sb.toString();
                        break;
                    default:
                        result = command + " not found. Type 'list' to view all available commands.";
                        break;

                }
            }
        }
        return result;
    }

    public static boolean ping(String host) {
        boolean isReachable = false;
        try {
            Process proc = new ProcessBuilder("ping", host).start();

            int exitValue = proc.waitFor();
            if (exitValue == 0) {
                isReachable = true;
            }
        } catch (IOException e1) {
            System.out.println(e1.getMessage());
            Logger.getLogger(TerminalControlBean.class.toString()).log(Level.SEVERE,
                    "Error pinging " + host, e1);
        } catch (InterruptedException e) {
            Logger.getLogger(TerminalControlBean.class.toString()).log(Level.SEVERE,
                    "Error pinging " + host, e);
        }
        return isReachable;
    }

    public String tail(File file, int lines) {
        StringBuilder sb = new StringBuilder();
        ReversedLinesFileReader reader;
        try {
            reader = new ReversedLinesFileReader(file);
            int ct = 0;
            String strLine;
            while ((strLine = reader.readLine()) != null) {
                if (++ct <= lines) {
                    sb.insert(0, strLine + "\n");
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(TerminalControlBean.class.getName()).log(Level.SEVERE,
                    "Error tail file: " + file.getAbsolutePath(), ex);
        }
        return sb.toString();
    }

    /**
     * @param terminalBean the terminalBean to set
     */
    public void setTerminalBean(TerminalBean terminalBean) {
        this.terminalBean = terminalBean;
    }

}
