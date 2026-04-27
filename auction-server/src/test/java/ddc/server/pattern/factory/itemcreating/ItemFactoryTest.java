package ddc.server.pattern.factory.ItemCreating;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import ddc.server.exception.ItemValidationException;
import ddc.server.model.item.Art;
import ddc.server.model.item.Electronics;
import ddc.server.model.item.ItemGeneric;
import ddc.server.model.item.Vehicle;
import ddc.server.pattern.factory.CreatorRegistry;
import ddc.server.pattern.factory.ItemCreator;
import ddc.server.pattern.factory.ItemRequest;

public class ItemFactoryTest {
   
    @Test
    void testFactory_ShouldArtCorrectly() throws ItemValidationException {
        ItemRequest req = new ItemRequest()
                .setItemName("Mona Lisa")
                .setCategory("ART")
                .setAuthor("Leonardo da Vinci");
        ItemCreator creator = CreatorRegistry.getCreator(req.getCategory());
        ItemGeneric result = creator.createItem(req);

        assertTrue(result instanceof Art);
        assertNotNull(result);
    }

    @Test
    void testFactory_ShouldCreateElectronicsCorrectly() throws ItemValidationException {
        ItemRequest req = new ItemRequest()
                .setItemName("iPhone 15")
                .setCategory("ELECTRONICS")
                .setBrand("Apple");

        ItemCreator creator = CreatorRegistry.getCreator(req.getCategory());
        ItemGeneric result = creator.createItem(req);

        assertTrue(result instanceof Electronics);
        assertNotNull(result);
    }

    @Test
    void testFactory_ShouldCreateVehicleCorrectly() throws ItemValidationException {
        ItemRequest req = new ItemRequest()
                .setItemName("Porche 911")
                .setCategory("VEHICLE")
                .setManufacturer("Porche AG");

        ItemCreator creator = CreatorRegistry.getCreator(req.getCategory());
        ItemGeneric result = creator.createItem(req);

        assertTrue(result instanceof Vehicle);
        assertNotNull(result);
    }

}

