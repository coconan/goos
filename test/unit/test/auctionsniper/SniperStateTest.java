package test.auctionsniper;

import auctionsniper.SniperState;
import auctionsniper.util.Defect;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SniperStateTest {
    @Test
    public void isLostWhenAuctionClosesWhileJoining() {
        assertEquals(SniperState.LOST, SniperState.JOINING.whenAuctionClosed());
    }

    @Test
    public void isLostWhenAuctionClosesWhileBidding() {
        assertEquals(SniperState.LOST, SniperState.BIDDING.whenAuctionClosed());
    }

    @Test
    public void isWonWhenAuctionClosesWhileWinning() {
        assertEquals(SniperState.WON, SniperState.WINNING.whenAuctionClosed());
    }

    @Test(expected = Defect.class)
    public void defectIfAuctionClosesWhenWon() {
        SniperState.WON.whenAuctionClosed();
    }

    @Test(expected = Defect.class)
    public void defectIfAuctionClosesWhenLost() {
        SniperState.LOST.whenAuctionClosed();
    }
}