package com.javarush;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javarush.dao.CityDAO;
import com.javarush.dao.CountryDAO;
import com.javarush.domain.City;
import com.javarush.domain.Country;
import com.javarush.redis.CityCountry;
import com.javarush.redis.Language;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisStringCommands;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {

    private final SessionFactory sessionFactory;
    private final CityDAO cityDAO;
    private final CountryDAO countryDAO;
    private final RedisClient redisClient;
    private final ObjectMapper mapper = new ObjectMapper();

    public Main() {
        sessionFactory = HibernateUtil.getSessionFactory();

        cityDAO = new CityDAO(sessionFactory);
        countryDAO = new CountryDAO(sessionFactory);
        redisClient = prepareRedisClient();
    }

    private RedisClient prepareRedisClient() {

        RedisClient redisClient =
                RedisClient.create(
                        RedisURI.create("localhost", 6379)
                );

        try (StatefulRedisConnection<String, String> connection =
                     redisClient.connect()) {

            System.out.println("Connected to Redis");
        }

        return redisClient;
    }

    private void pushToRedis(List<CityCountry> data) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {

            RedisStringCommands<String, String> sync = connection.sync();

            for (CityCountry cityCountry : data) {
                try {
                    String json = mapper.writeValueAsString(cityCountry);

                    sync.set(
                            String.valueOf(cityCountry.getId()),
                            json
                    );

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Data pushed to Redis: " + data.size());
    }

    private void testRedisData(List<Integer> ids) {
        try (StatefulRedisConnection<String, String> connection = redisClient.connect()) {

            RedisStringCommands<String, String> sync = connection.sync();

            for (Integer id : ids) {
                String value = sync.get(String.valueOf(id));

                try {
                    CityCountry cityCountry =
                            mapper.readValue(value, CityCountry.class);

                    System.out.println(
                            cityCountry.getId() + " - " + cityCountry.getName()
                    );

                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void testMysqlData(List<Integer> ids) {
        try (Session session = sessionFactory.getCurrentSession()) {

            session.beginTransaction();

            for (Integer id : ids) {
                City city = cityDAO.getById(id);

                city.getCountry().getLanguages().size();

                System.out.println(
                        city.getId() + " - " + city.getName()
                );
            }

            session.getTransaction().commit();
        }
    }

    private List<CityCountry> transformData(List<City> cities) {
        return cities.stream().map(city -> {

            CityCountry result = new CityCountry();

            result.setId(city.getId());
            result.setName(city.getName());
            result.setDistrict(city.getDistrict());
            result.setPopulation(city.getPopulation());

            Country country = city.getCountry();

            result.setCountryCode(country.getCode());
            result.setAlternativeCountryCode(country.getCode2());
            result.setCountryName(country.getName());
            result.setContinent(country.getContinent());
            result.setCountryRegion(country.getRegion());
            result.setCountrySurfaceArea(country.getSurfaceArea());
            result.setCountryPopulation(country.getPopulation());

            Set<Language> languages = country.getLanguages()
                    .stream()
                    .map(countryLanguage -> {
                        Language language = new Language();

                        language.setLanguage(countryLanguage.getLanguage());
                        language.setOfficial(countryLanguage.getOfficial());
                        language.setPercentage(countryLanguage.getPercentage());

                        return language;
                    })
                    .collect(Collectors.toSet());

            result.setLanguages(languages);

            return result;

        }).collect(Collectors.toList());
    }

    private List<City> fetchData() {

        try (Session session = sessionFactory.getCurrentSession()) {

            List<City> allCities = new ArrayList<>();

            session.beginTransaction();

            List<Country> countries = countryDAO.getAll();

            int totalCount = cityDAO.getTotalCount();
            int step = 500;

            for (int i = 0; i < totalCount; i += step) {
                allCities.addAll(cityDAO.getItems(i, step));
            }

            session.getTransaction().commit();

            System.out.println("Countries loaded: " + countries.size());
            System.out.println("Cities loaded: " + allCities.size());

            return allCities;
        }
    }

    private void shutdown() {
        redisClient.shutdown();
        sessionFactory.close();
    }

    public static void main(String[] args) {

        Main main = new Main();

        List<City> allCities = main.fetchData();

        List<CityCountry> preparedData =
                main.transformData(allCities);

        main.pushToRedis(preparedData);

        List<Integer> ids = List.of(
                3, 2545, 123, 4, 189,
                89, 3458, 1189, 10, 102
        );

        long startRedis = System.currentTimeMillis();

        main.testRedisData(ids);

        long stopRedis = System.currentTimeMillis();


        long startMysql = System.currentTimeMillis();

        main.testMysqlData(ids);

        long stopMysql = System.currentTimeMillis();


        System.out.printf(
                "Redis:\t%d ms%n",
                stopRedis - startRedis
        );

        System.out.printf(
                "MySQL:\t%d ms%n",
                stopMysql - startMysql
        );

        main.shutdown();
    }
}