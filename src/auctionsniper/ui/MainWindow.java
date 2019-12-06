package auctionsniper.ui;

import auctionsniper.SniperSnapshot;
import auctionsniper.SniperState;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame {
    public static final String STATUS_JOINING = "joining";
    public static final String STATUS_BIDDING = "bidding";
    public static final String STATUS_WINNING = "winning";
    public static final String STATUS_LOST = "lost";
    public static final String STATUS_WON = "won";
    public static final String MAIN_WINDOW_NAME = "Auction Sniper";
    private static final String SNIPERS_TABLE_NAME = "Snipers table";
    private final SnipersTableModel snipers = new SnipersTableModel();

    public MainWindow(String itemId) {
        super("Auction Sniper");
        setName(MAIN_WINDOW_NAME);
        fillContentPane(makeSnipersTable());
        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        sniperStatusChanged(new SniperSnapshot(itemId, 0, 0, SniperState.JOINING));
    }

    private void fillContentPane(JTable snipersTable) {
        final Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        contentPane.add(new JScrollPane(snipersTable), BorderLayout.CENTER);
    }

    private JTable makeSnipersTable() {
        final JTable snipersTable = new JTable(snipers);
        snipersTable.setName(SNIPERS_TABLE_NAME);
        return snipersTable;
    }

    public void showStatusText(String statusText) {
        snipers.setStatusText(statusText);
    }

    public void sniperStatusChanged(SniperSnapshot sniperSnapshot) {
        snipers.sniperStatusChanged(sniperSnapshot);
    }
}
