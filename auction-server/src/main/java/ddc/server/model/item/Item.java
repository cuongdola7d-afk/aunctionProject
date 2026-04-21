package ddc.server.model.item;

import java.sql.Connection;

public class Item extends ItemGeneric<Item> {
    public Item () {}

    @Override
    public void saveSpecificDetails (Connection con, String id) {}
}