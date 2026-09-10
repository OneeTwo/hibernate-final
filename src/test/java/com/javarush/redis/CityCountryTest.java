package com.javarush.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.domain.Continent;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CityCountryTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldSerializeAndDeserializeCityCountry() throws JsonProcessingException {
        Language language = new Language();
        language.setLanguage("Ukrainian");
        language.setOfficial(true);
        language.setPercentage(new BigDecimal("67.5"));

        Set<Language> languages = new HashSet<>();
        languages.add(language);

        CityCountry city = new CityCountry();
        city.setId(1);
        city.setName("Lviv");
        city.setDistrict("Lviv");
        city.setPopulation(700000);
        city.setCountryCode("UKR");
        city.setAlternativeCountryCode("UA");
        city.setCountryName("Ukraine");
        city.setContinent(Continent.EUROPE);
        city.setCountryRegion("Eastern Europe");
        city.setCountrySurfaceArea(new BigDecimal("603700.00"));
        city.setCountryPopulation(41000000);
        city.setLanguages(languages);

        String json = mapper.writeValueAsString(city);

        CityCountry result =
                mapper.readValue(json, CityCountry.class);

        assertEquals(1, result.getId());
        assertEquals("Lviv", result.getName());
        assertEquals("UKR", result.getCountryCode());
        assertEquals("Ukraine", result.getCountryName());
        assertEquals(Continent.EUROPE, result.getContinent());

        assertNotNull(result.getLanguages());
        assertEquals(1, result.getLanguages().size());

        Language resultLanguage =
                result.getLanguages().iterator().next();

        assertEquals("Ukrainian", resultLanguage.getLanguage());
        assertTrue(resultLanguage.getOfficial());
        assertEquals(
                0,
                new BigDecimal("67.5")
                        .compareTo(resultLanguage.getPercentage())
        );
    }

    @Test
    void shouldCreateEmptyCityCountry() {
        CityCountry city = new CityCountry();

        assertNull(city.getId());
        assertNull(city.getName());
        assertNull(city.getCountryName());
        assertNull(city.getLanguages());
    }
}