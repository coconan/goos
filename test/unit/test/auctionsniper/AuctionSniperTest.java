package test.auctionsniper;

import auctionsniper.*;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.Sequence;
import org.jmock.States;
import org.jmock.integration.junit4.JMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static auctionsniper.SniperState.*;
import static auctionsniper.UserRequestListener.Item;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(JMock.class)
public class AuctionSniperTest {
    protected static final String ITEM_ID = "item-id";
    public static final Item ITEM = new Item(ITEM_ID, 1234);
    private final Mockery context = new Mockery();
    private final States sniperState = context.states("sniper");
    private final SniperListener sniperListener = context.mock(SniperListener.class);
    private final Auction auction = context.mock(Auction.class);
    private final AuctionSniper sniper = new AuctionSniper(ITEM, auction);

    @Before
    public void attachListener() {
        sniper.addSniperListener(sniperListener);
    }

    @Test
    public void reportsLostWhenAuctionCloses() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(LOST)));
        }});

        sniper.auctionClosed();
    }

    @Test
    public void bidsHigherAndReportsBiddingWhenNewPriceArrives() {
        final int price = 1001;
        final int increment = 25;
        final int bid = price + increment;

        context.checking(new Expectations() {{
            exactly(1).of(auction).bid(bid);
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, BIDDING));
        }});

        sniper.currentPrice(price, increment, AuctionEventListener.PriceSource.FromOtherBidder);
    }

    @Test
    public void reportsIsWinningWhenCurrentPriceComesFromSniper() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
            then(sniperState.is("bidding"));
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 135, 135, WINNING)
            );
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 12, AuctionEventListener.PriceSource.FromOtherBidder);
        sniper.currentPrice(135, 45, AuctionEventListener.PriceSource.FromSniper);
    }

    @Test
    public void reportsLostIfAuctionClosesImmediately() {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(LOST)));
        }});

        sniper.auctionClosed();
    }

    @Test
    public void reportsLostIfAuctionClosesWhenBidding() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(BIDDING)));
            then(sniperState.is("bidding"));
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(LOST)));
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    public void reportsWonIfAuctionClosesWhenWinning() {
        context.checking(new Expectations() {{
            ignoring(auction);
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(WINNING)));
            then(sniperState.is("winning"));
            atLeast(1).of(sniperListener).sniperStateChanged(with(aSniperThatIs(WON)));
            when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FromSniper);
        sniper.auctionClosed();
    }

    @Test
    public void doesNotBidAndReportsLosingIfFirstPriceIsAboveStopPrice() {
        final int price = 1233;
        final int increment = 25;

        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, 0, LOSING));
        }});

        sniper.currentPrice(price, increment, AuctionEventListener.PriceSource.FromOtherBidder);
    }

    @Test
    public void doseNotBidAndReportsLosingIfSubsequentPriceIsAboveStopPrice() {
        allowingSniperBidding();
        context.checking(new Expectations() {{
            int bid = 123 + 45;
            allowing(auction).bid(bid);
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 2345, bid, LOSING));
            when(sniperState.is("bidding"));
        }});

        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FromOtherBidder);
        sniper.currentPrice(2345, 25, AuctionEventListener.PriceSource.FromOtherBidder);
    }


    @Test
    public void continuesToBeLosingOnceStopPriceHasBeenReached() {
        final Sequence states = context.sequence("sniper states");
        final int price1 = 1233;
        final int price2 = 1258;

        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price1, 0, LOSING));
            inSequence(states);
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price2, 0, LOSING));
            inSequence(states);
        }});

        sniper.currentPrice(price1, 25, AuctionEventListener.PriceSource.FromOtherBidder);
        sniper.currentPrice(price2, 25, AuctionEventListener.PriceSource.FromOtherBidder);
    }

    @Test
    public void doesNotBidAndReportsLosingIfPriceAfterWinningIsAboveStopPrice() {
        final int price = 1233;
        final int increment = 25;

        allowingSniperBidding();
        allowingSniperWinning();
        context.checking(new Expectations() {{
            int bid = 123 + 45;
            allowing(auction).bid(bid);

            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, price, bid, LOSING));
            when(sniperState.is("winning"));
        }});

        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FromOtherBidder);
        sniper.currentPrice(168, 45, AuctionEventListener.PriceSource.FromSniper);
        sniper.currentPrice(price, increment, AuctionEventListener.PriceSource.FromOtherBidder);
    }

    @Test
    public void reportsLostIfAuctionClosesWhenLosing() {
        allowingSniperLosing();
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(new SniperSnapshot(ITEM_ID, 1230, 0, LOST));
            when(sniperState.is("losing"));
        }});

        sniper.currentPrice(1230, 456, AuctionEventListener.PriceSource.FromOtherBidder);
        sniper.auctionClosed();
    }

    @Test
    public void reportsFailedIfAuctionFailsWhenBidding() {
        ignoringAuction();
        allowingSniperBidding();

        expectSniperToFailWhenItIs("bidding");

        sniper.currentPrice(123, 45, AuctionEventListener.PriceSource.FromOtherBidder);
        sniper.auctionFailed();
    }

    private void expectSniperToFailWhenItIs(final String state) {
        context.checking(new Expectations() {{
            atLeast(1).of(sniperListener).sniperStateChanged(
                    new SniperSnapshot(ITEM_ID, 0, 0, FAILED));
            when(sniperState.is(state));
        }});
    }

    private void ignoringAuction() {
        context.checking(new Expectations() {{
            ignoring(auction);
        }});
    }

    private void allowingSniperBidding() {
        allowSniperStateChange(BIDDING, "bidding");
    }

    private void allowingSniperLosing() {
        allowSniperStateChange(LOSING, "losing");
    }

    private void allowingSniperWinning() {
        allowSniperStateChange(WINNING, "winning");
    }

    private void allowSniperStateChange(final SniperState newState, final String oldState) {
        context.checking(new Expectations() {{
            allowing(sniperListener).sniperStateChanged(with(aSniperThatIs(newState)));
            then(sniperState.is(oldState));
        }});
    }

    private Matcher<SniperSnapshot> aSniperThatIs(final SniperState state) {
        return new FeatureMatcher<SniperSnapshot, SniperState>
                (equalTo(state), "sniper that is ", "was") {
            @Override
            protected SniperState featureValueOf(SniperSnapshot actual) {
                return actual.state;
            }
        };
    }
}
