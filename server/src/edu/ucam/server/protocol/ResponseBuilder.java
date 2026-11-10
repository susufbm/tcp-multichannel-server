package edu.ucam.server.protocol;

public class ResponseBuilder {

    public static String ok(String num, int code, String msg) {
        return "OK " + num + " " + code + " " + msg;
    }

    public static String preok(String num, int code, String ip, int port) {
        return "PREOK " + num + " " + code + " " + ip + " " + port;
    }

    public static String failed(String num, int code, String msg) {
        return "FAILED " + num + " " + code + " " + msg;
    }
}
