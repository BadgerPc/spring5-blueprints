package com.packt.dal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.packt.config.TestDBConfiguration;
import com.packt.model.Country;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringJUnitConfig( classes = {TestDBConfiguration.class, CountryDAO.class})
public class CountryDAOTest {

	@Autowired CountryDAO countryDao;
	
	@Autowired @Qualifier("testTemplate")
	NamedParameterJdbcTemplate namedParamJdbcTemplate;
	
	@Before
	public void setup() {
		countryDao.setNamedParamJdbcTemplate(namedParamJdbcTemplate);
	}
	
	@Test
	public void testGetCountries() {
		List<Country> countries = countryDao.getCountries(new HashMap<>());
		assertThat(countries).hasSize(20);
		
		Country c = countries.get(0);
		assertThat(c.toString()).isEqualTo("Country(code=ABW, name=Aruba, "
				+ "continent=North America, region=Caribbean, surfaceArea=193.0, "
				+ "indepYear=0, population=103000, lifeExpectancy=78.4000015258789, "
				+ "gnp=828.0, localName=Aruba, governmentForm=Nonmetropolitan Territory of The Netherlands, "
				+ "headOfState=Beatrix, capital=City(id=129, name=Oranjestad, countryCode=null, "
				+ "country=null, district=null, population=null), code2=AW)");
	}
	
	@Test
	public void testGetCountries_searchByName() {
		Map<String, Object> params = new HashMap<>();
		params.put("search", "Aruba");
		List<Country> countries = countryDao.getCountries(params);
		assertThat(countries).hasSize(1);
	}
	
	@Test
	public void testGetCountries_searchByLocalName() {
		Map<String, Object> params = new HashMap<>();
		params.put("search", "Bharat/India");
		List<Country> countries = countryDao.getCountries(params);
		assertThat(countries).hasSize(1);
	}
	
	@Test
	public void testGetCountries_searchByContinent() {
		Map<String, Object> params = new HashMap<>();
		params.put("continent", "Asia");
		List<Country> countries = countryDao.getCountries(params);
		
		assertThat(countries).hasSize(20);
	}
	
	@Test
	public void testGetCountryDetail() {
		Country c = countryDao.getCountryDetail("IND");
		assertThat(c).isNotNull();
		assertThat(c.toString()).isEqualTo("Country(code=IND, name=India, "
				+ "continent=Asia, region=Southern and Central Asia, "
				+ "surfaceArea=3287263.0, indepYear=1947, population=1013662000, "
				+ "lifeExpectancy=62.5, gnp=447114.0, localName=Bharat/India, "
				+ "governmentForm=Federal Republic, headOfState=Kocheril Raman Narayanan, "
				+ "capital=City(id=1109, name=New Delhi, countryCode=null, "
				+ "country=null, district=null, population=null), code2=IN)");
	}
	
	@Test public void testEditCountryDetail() {
		Country c = countryDao.getCountryDetail("IND");
		c.setHeadOfState("Ram Nath Kovind");
		c.setPopulation(1324171354l);
		countryDao.editCountryDetail("IND", c);
		
		c = countryDao.getCountryDetail("IND");
		assertThat(c.getHeadOfState()).isEqualTo("Ram Nath Kovind");
		assertThat(c.getPopulation()).isEqualTo(1324171354l);
		assertThat(c.getName()).isEqualTo("India");
	}
	
	@Test public void testGetCountriesCount() {
		Integer count = countryDao.getCountriesCount(Collections.EMPTY_MAP);
		assertThat(count).isEqualTo(239);
	}
}
