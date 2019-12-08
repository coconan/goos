package auctionsniper.xmpp;

import auctionsniper.Auction;
import auctionsniper.AuctionEventListener;
import auctionsniper.util.Announcer;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import static auctionsniper.Main.BID_COMMAND_FORMAT;
import static auctionsniper.Main.JOIN_COMMAND_FORMAT;

public class XMPPAuction implements Auction {
    private final Announcer<AuctionEventListener> auctionEventListeners = Announcer.to(AuctionEventListener.class);
    private final Chat chat;
    private final XMPPFailureReporter failureReporter;

    public XMPPAuction(XMPPConnection connection, String auctionJID, XMPPFailureReporter failureReporter) {
        this.failureReporter = failureReporter;
        AuctionMessageTranslator translator = translatorFor(connection);
        chat = connection.getChatManager().createChat(auctionJID, translator);
        addAuctionEventListener(chatDisconnectorFor(translator));
    }

    @Override
    public void bid(int amount) {
        sendMessage(String.format(BID_COMMAND_FORMAT, amount));
    }

    @Override
    public void addAuctionEventListener(AuctionEventListener auctionEventListener) {
        auctionEventListeners.addListener(auctionEventListener);
    }

    public void join() {
        sendMessage(JOIN_COMMAND_FORMAT);
    }

    private AuctionMessageTranslator translatorFor(XMPPConnection connection) {
        return new AuctionMessageTranslator(connection.getUser(), auctionEventListeners.announce(), failureReporter);
    }

    private AuctionEventListener chatDisconnectorFor(final AuctionMessageTranslator translator) {
        return new AuctionEventListener() {
            @Override
            public void auctionClosed() {
                // empty method
            }

            @Override
            public void currentPrice(int price, int increment, PriceSource from) {
                // empty method
            }

            @Override
            public void auctionFailed() {
                chat.removeMessageListener(translator);
            }
        };
    }

    private void sendMessage(final String message) {
        try {
            chat.sendMessage(message);
        } catch (XMPPException e) {
            e.printStackTrace();
        }
    }
}
