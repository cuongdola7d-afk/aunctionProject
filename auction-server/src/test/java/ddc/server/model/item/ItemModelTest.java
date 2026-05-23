package ddc.server.model.item;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;

import ddc.server.exception.ItemValidationException;

class ItemModelTest {

    @Test
    void itemGenericValidate_shouldRejectMissingRequiredFields() {
        TestItem missingName = validBaseItem().setItemName(null);
        assertThrows(ItemValidationException.class, missingName::validate);

        TestItem blankName = validBaseItem().setItemName("   ");
        assertThrows(ItemValidationException.class, blankName::validate);

        TestItem missingCategory = validBaseItem().setCategory(null);
        assertThrows(ItemValidationException.class, missingCategory::validate);

        TestItem missingSeller = validBaseItem().setSellerName("");
        assertThrows(ItemValidationException.class, missingSeller::validate);
    }

    @Test
    void itemGenericFluentSetters_shouldSetValuesAndReturnSelf() {
        TestItem item = new TestItem();

        TestItem result = item
                .setId("I001")
                .setItemName("Camera")
                .setCategory("GENERAL")
                .setDescription("Used camera")
                .setSellerName("seller1")
                .setImageUrl("https://example.test/image.jpg");

        assertSame(item, result);
        assertEquals("I001", item.getId());
        assertEquals("Camera", item.getItemName());
        assertEquals("GENERAL", item.getCategory());
        assertEquals("Used camera", item.getDescription());
        assertEquals("seller1", item.getSellerName());
        assertEquals("https://example.test/image.jpg", item.getImageUrl());
        assertDoesNotThrow(item::validate);
    }

    @Test
    void vehicle_shouldValidateFieldsAndExposeFactoryDefaults() {
        Vehicle vehicle = Vehicle.create()
                .setItemName("Civic")
                .setSellerName("seller1")
                .setManufacturer("Honda")
                .setYear(2020);

        assertEquals("VEHICLE", vehicle.getCategory());
        assertEquals("Honda", vehicle.getManufacturer());
        assertEquals(2020, vehicle.getYear());
        assertSame(vehicle, vehicle.setManufacturer("Toyota"));
        assertSame(vehicle, vehicle.setYear(2021));
        assertDoesNotThrow(vehicle::validate);

        assertThrows(ItemValidationException.MissingFieldException.class,
                () -> validVehicle().setManufacturer("").validate());
        assertThrows(ItemValidationException.InvalidValueException.class,
                () -> validVehicle().setYear(1885).validate());
    }

    @Test
    void art_shouldValidateFieldsAndExposeFactoryDefaults() {
        Art art = Art.create()
                .setItemName("Portrait")
                .setSellerName("seller1")
                .setAuthor("Painter")
                .setyearCreated(1999);

        assertEquals("ART", art.getCategory());
        assertEquals("Painter", art.getAuthor());
        assertEquals(1999, art.getyearCreated());
        assertSame(art, art.setAuthor("Other Painter"));
        assertSame(art, art.setyearCreated(2000));
        assertDoesNotThrow(art::validate);

        assertThrows(ItemValidationException.MissingFieldException.class,
                () -> validArt().setAuthor("").validate());
        assertThrows(ItemValidationException.MissingFieldException.class,
                () -> validArt().setyearCreated(-1).validate());
    }

    @Test
    void electronics_shouldValidateFieldsAndExposeFactoryDefaults() {
        Electronics electronics = Electronics.create()
                .setItemName("Laptop")
                .setSellerName("seller1")
                .setBrand("Dell")
                .setWarrantyMonths(12);

        assertEquals("ELECTRONICS", electronics.getCategory());
        assertEquals("Dell", electronics.getBrand());
        assertEquals(12, electronics.getWarrantyMonths());
        assertSame(electronics, electronics.setBrand("HP"));
        assertSame(electronics, electronics.setWarrantyMonths(24));
        assertDoesNotThrow(electronics::validate);

        assertThrows(ItemValidationException.MissingFieldException.class,
                () -> validElectronics().setBrand("").validate());
    }

    @Test
    void general_shouldUseBaseValidation() {
        General general = validGeneral();

        assertDoesNotThrow(general::validate);
        assertThrows(ItemValidationException.class, () -> validGeneral().setItemName("").validate());
        assertThrows(ItemValidationException.class, () -> validGeneral().setCategory("").validate());
        assertThrows(ItemValidationException.class, () -> validGeneral().setSellerName("").validate());
    }

    @Test
    void vehicleLoad_shouldPopulateSpecificFieldsFromResultSet() throws SQLException {
        Connection con = mockLoad("manufacturer", "Honda", "year", 2020);
        Vehicle vehicle = new Vehicle().setId("I001");

        vehicle.load(con);

        assertEquals("Honda", vehicle.getManufacturer());
        assertEquals(2020, vehicle.getYear());
        verify(con.prepareStatement("SELECT manufacturer, year FROM item_vehicle WHERE id = ?")).setString(1, "I001");
    }

    @Test
    void artLoad_shouldPopulateSpecificFieldsFromResultSet() throws SQLException {
        Connection con = mockLoad("author", "Da Vinci", "year_created", 1503);
        Art art = new Art().setId("I002");

        art.load(con);

        assertEquals("Da Vinci", art.getAuthor());
        assertEquals(1503, art.getyearCreated());
        verify(con.prepareStatement("SELECT author, year_created FROM item_art WHERE id = ?")).setString(1, "I002");
    }

    @Test
    void electronicsLoad_shouldPopulateSpecificFieldsFromResultSet() throws SQLException {
        Connection con = mockLoad("brand", "Apple", "warranty_months", 18);
        Electronics electronics = new Electronics().setId("I003");

        electronics.load(con);

        assertEquals("Apple", electronics.getBrand());
        assertEquals(18, electronics.getWarrantyMonths());
        verify(con.prepareStatement("SELECT brand, warranty_months FROM item_electronics WHERE id = ?")).setString(1, "I003");
    }

    @Test
    void save_shouldReturnGeneratedIdForEachItemType() throws SQLException {
        assertEquals("I101", validVehicle().save(mockSaveSuccess("I101")));
        assertEquals("I102", validArt().save(mockSaveSuccess("I102")));
        assertEquals("I103", validElectronics().save(mockSaveSuccess("I103")));
        assertEquals("I104", validGeneral().save(mockSaveSuccess("I104")));
    }

    @Test
    void save_shouldReturnNullWhenSqlFails() throws SQLException {
        Connection con = org.mockito.Mockito.mock(Connection.class);
        when(con.prepareStatement(anyString())).thenThrow(new SQLException("db down"));

        assertNull(validVehicle().save(con));
        assertNull(validArt().save(con));
        assertNull(validElectronics().save(con));
        assertNull(validGeneral().save(con));
    }

    private TestItem validBaseItem() {
        return new TestItem()
                .setItemName("Item")
                .setCategory("GENERAL")
                .setSellerName("seller1");
    }

    private Vehicle validVehicle() {
        return new Vehicle()
                .setItemName("Civic")
                .setSellerName("seller1")
                .setDescription("Car")
                .setManufacturer("Honda")
                .setYear(2020);
    }

    private Art validArt() {
        return new Art()
                .setItemName("Portrait")
                .setSellerName("seller1")
                .setDescription("Painting")
                .setAuthor("Painter")
                .setyearCreated(1999);
    }

    private Electronics validElectronics() {
        return new Electronics()
                .setItemName("Laptop")
                .setSellerName("seller1")
                .setDescription("Computer")
                .setBrand("Dell")
                .setWarrantyMonths(12);
    }

    private General validGeneral() {
        return new General()
                .setItemName("Book")
                .setCategory("GENERAL")
                .setSellerName("seller1")
                .setDescription("Novel");
    }

    private Connection mockLoad(String stringColumn, String stringValue, String intColumn, int intValue) throws SQLException {
        Connection con = org.mockito.Mockito.mock(Connection.class);
        PreparedStatement pst = org.mockito.Mockito.mock(PreparedStatement.class);
        ResultSet rs = org.mockito.Mockito.mock(ResultSet.class);

        when(con.prepareStatement(anyString())).thenReturn(pst);
        when(pst.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString(stringColumn)).thenReturn(stringValue);
        when(rs.getInt(intColumn)).thenReturn(intValue);

        return con;
    }

    private Connection mockSaveSuccess(String generatedId) throws SQLException {
        Connection con = org.mockito.Mockito.mock(Connection.class);
        PreparedStatement insert = org.mockito.Mockito.mock(PreparedStatement.class);
        PreparedStatement select = org.mockito.Mockito.mock(PreparedStatement.class);
        ResultSet rs = org.mockito.Mockito.mock(ResultSet.class);

        when(con.prepareStatement(anyString())).thenReturn(insert, select);
        when(select.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("generated_id")).thenReturn(generatedId);

        return con;
    }

    private static class TestItem extends ItemGeneric<TestItem> {
        @Override
        public String save(Connection con) {
            return null;
        }

        @Override
        public void load(Connection con) {
        }
    }
}
