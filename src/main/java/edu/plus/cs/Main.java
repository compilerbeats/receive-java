package edu.plus.cs;

import edu.plus.cs.util.OperatingMode;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) throws UnknownHostException {
        if (args.length > 0) {
            int operatingModeCode = Integer.parseInt(args[0]);
            OperatingMode operatingMode = parseOperatingMode(operatingModeCode);

            if (operatingMode == OperatingMode.NO_ACK && args.length != 4) {
                System.err.println("Usage: <operatingMode> <transmissionId> <port> <targetFolder>");
                return;
            } else if (operatingMode == OperatingMode.STOP_WAIT && args.length != 6) {
                System.err.println("Usage: <operatingMode> <transmissionId> <port> <targetFolder> <ackIp> <ackPort>");
                return;
            } else if (operatingMode == OperatingMode.SLIDING_WINDOW && args.length != 9) {
                System.err.println("Usage: <operatingMode> <transmissionId> <port> <targetFolder> <ackIp> " +
                        "<ackPort> <windowSize> <windowTimeout> <dupAckDelay>");
                return;
            }

            short transmissionId = Short.parseShort(args[1]);
            int port = Integer.parseInt(args[2]);

            File targetFolder = new File(args[3]);
            if (targetFolder.exists() && !targetFolder.isDirectory()) {
                System.err.println("Target folder can't be a file");
                return;
            }
            targetFolder.mkdirs();

            String ackIp = (operatingMode != OperatingMode.NO_ACK) ? args[4] : null;
            int ackPort = (operatingMode != OperatingMode.NO_ACK) ? Integer.parseInt(args[5]) : -1;
            int windowSize = (operatingMode == OperatingMode.SLIDING_WINDOW) ? Integer.parseInt(args[6]) : -1;
            int windowTimeout = (operatingMode == OperatingMode.SLIDING_WINDOW) ? Integer.parseInt(args[7]) : -1;
            int duplicateAckDelay = (operatingMode == OperatingMode.SLIDING_WINDOW) ? Integer.parseInt(args[8]) : -1;

            new Receiver(transmissionId, port, targetFolder, InetAddress.getByName(ackIp),
                    ackPort, operatingMode, windowSize, windowTimeout, duplicateAckDelay).start();
        }
    }

    private static OperatingMode parseOperatingMode(int operatingModeCode) {
        switch (operatingModeCode) {
            case 0:
                return OperatingMode.NO_ACK;
            case 1:
                return OperatingMode.STOP_WAIT;
            case 2:
                return OperatingMode.SLIDING_WINDOW;
            default:
                System.err.println("No valid operating mode provided, set to NO_ACK as default!");
                return OperatingMode.NO_ACK;
        }
    }
}