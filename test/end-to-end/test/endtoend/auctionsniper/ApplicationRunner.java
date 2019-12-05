package test.endtoend.auctionsniper;

import auctionsniper.Main;
import auctionsniper.SniperState;

import static test.endtoend.auctionsniper.FakeAuctionServer.XMPP_HOSTNAME;

public class ApplicationRunner {
    public static final String SNIPER_ID = "sniper";
    public static final String SNIPER_PASSWORD = "sniper";
    public static final String SNIPER_XMPP_ID = SNIPER_ID + "@" + XMPP_HOSTNAME + "/Auction";
    private AuctionSniperDriver driver;

    public void startBiddingIn(final FakeAuctionServer auction) {
        Thread thread = new Thread("Test Application") {
            @Override public void run() {
                try {
                    Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
        driver = new AuctionSniperDriver(1000);
        driver.showsSniperStatus(SniperState.JOINING);
    }

    public void showsSniperHasLostAuction() {
        driver.showsSniperStatus(SniperState.LOST);
    }

    public void showsSniperHasWonAuction() {
        driver.showsSniperStatus(SniperState.WON);
    }

    public void hasShownSniperIsBidding() {
        driver.showsSniperStatus(SniperState.BIDDING);
    }

    public void hasShownSniperIsWinning() {
        driver.showsSniperStatus(SniperState.WINNING);
    }

    public void stop() {
        if (driver != null) {
            driver.dispose();
        }
    }
}