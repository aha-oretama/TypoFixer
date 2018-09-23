package jp.aha.oretama.typoFixer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.aha.oretama.typoFixer.service.filter.ExtensionFilter;
import jp.aha.oretama.typoFixer.service.filter.Filter;
import jp.aha.oretama.typoFixer.service.filter.ParseFilter;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author aha-oretama
 */
public class FilterServiceTest {

    private FilterService filterService = new FilterService(new ObjectMapper());

    private String empty = "";
    private String emptyJson = "{}";
    private String emptyArrayJson = "[]";
    private String extensionJson
            = "{\n" +
            "   \"extensions\": [\".md\"] \n" +
            "}";

    @Test
    public void setParse() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        filterService.setParse();

        // Act
        List<Filter> filters = getPrivateField(filterService, "filters");

        // Assert
        assertEquals(1, filters.size());
        assertTrue(filters.get(0) instanceof ParseFilter);
    }

    @Test
    public void setConfigByEmpty() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        filterService.setConfig(empty);

        // Act
        List<Filter> filters = getPrivateField(filterService, "filters");

        // Assert
        assertTrue(filters.isEmpty());
    }

    @Test
    public void setConfigByEmptyJson() throws NoSuchFieldException, IllegalAccessException, IOException {
        // Arrange
        filterService.setConfig(emptyJson);

        // Act
        List<Filter> filters = getPrivateField(filterService, "filters");

        // Assert
        assertTrue(filters.isEmpty());
    }

    @Test
    public void setConfigByEmptyArrayJson() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        filterService.setConfig(emptyArrayJson);

        // Act
        List<Filter> filters = getPrivateField(filterService, "filters");

        // Assert
        assertTrue(filters.isEmpty());
    }


    @Test
    public void setConfigByExtensionJson() throws NoSuchFieldException, IllegalAccessException {
        // Arrange
        filterService.setConfig(extensionJson);

        // Act
        List<Filter> filters = getPrivateField(filterService, "filters");

        // Assert
        assertEquals(1, filters.size());
        assertTrue(filters.get(0) instanceof ExtensionFilter);
    }

    private List<Filter> getPrivateField(Object targetObject, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Class c = targetObject.getClass();
        Field field = c.getDeclaredField(fieldName);
        field.setAccessible(true);
        return (List<Filter>) field.get(targetObject);
    }
}