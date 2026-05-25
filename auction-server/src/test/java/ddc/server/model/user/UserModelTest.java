package ddc.server.model.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ddc.server.model.item.General;
import ddc.server.model.transaction.Bid;

class UserModelTest {

    @Test
    void user_shouldRoundTripFieldsAndDefaultBlankRoleStatus() {
        User user = new User()
                .setUsername("buyer")
                .setName("Buyer One")
                .setEmail("buyer@example.test")
                .setPassword("secret")
                .setRole("ADMIN")
                .setStatus("LOCKED");

        assertEquals("buyer", user.getUsername());
        assertEquals("Buyer One", user.getName());
        assertEquals("buyer@example.test", user.getEmail());
        assertEquals("secret", user.getPassword());
        assertEquals("ADMIN", user.getRole());
        assertEquals("LOCKED", user.getStatus());

        assertSame(user, user.setRole(null));
        assertSame(user, user.setStatus(" "));
        assertEquals("USER", user.getRole());
        assertEquals("ACTIVE", user.getStatus());
    }

    @Test
    void specializedUsers_shouldManageRoleItemsAndBids() {
        Admin admin = new Admin();
        assertEquals("ADMIN", admin.getRole());

        Seller seller = new Seller();
        General item = new General().setId("I001");
        seller.addItem(item);
        assertEquals(1, seller.getItemForSale().size());
        assertSame(item, seller.getItemForSale().get(0));

        Bidder bidder = new Bidder();
        Bid bid = new Bid().setBidAmount(100);
        bidder.addBid(bid);
        assertEquals(1, bidder.getBidHistory().size());
        assertSame(bid, bidder.getBidHistory().get(0));
        assertTrue(new User().getRole().equals("USER"));
    }
}
