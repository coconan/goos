package auctionsniper;

import auctionsniper.ui.MainWindow;
import auctionsniper.ui.SnipersTableModel;
import auctionsniper.xmpp.XMPPAuctionHouse;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final int ARG_HOSTNAME = 0;
    private static final int ARG_USERNAME = 1;
    private static final int ARG_PASSWORD = 2;

    public static final String JOIN_COMMAND_FORMAT = "SOLVersion: 2.1; Command: JOIN;";
    public static final String BID_COMMAND_FORMAT = "SQLVersion: 2.1; Command: BID; Price: %d;";

    private final SnipersTableModel snipers = new SnipersTableModel();
    private MainWindow ui;
    private List<Auction> notToBeGCd = new ArrayList<>();

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
        AuctionHouse auctionHouse = XMPPAuctionHouse.connect(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]);
        main.disconnectWhenUICloses(auctionHouse);
        main.addUserRequestListenerFor(auctionHouse);
    }

    private void addUserRequestListenerFor(final AuctionHouse auctionHouse) {
        ui.addUserRequestListener(new SniperLauncher(auctionHouse, snipers));
    }

    private void disconnectWhenUICloses(final AuctionHouse auctionHouse) {
        ui.addWindowStateListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                auctionHouse.disconnect();
            }
        });
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(() -> ui = new MainWindow(snipers));
    }

}
