package edu.ucam.server.protocol;

public class CommandParser {

    private String number;
    private String command;
    private String[] args;

    public CommandParser(String line) {
        String[] parts = line.split(" ");
        number = parts[0];
        command = parts[1];
        if (parts.length > 2) {
            args = new String[parts.length - 2];
            System.arraycopy(parts, 2, args, 0, args.length);
        } else {
            args = new String[0];
        }
    }

    public String getNumber() { return number; }
    public String getCommand() { return command; }
    public String[] getArgs() { return args; }
    
    
    
}
