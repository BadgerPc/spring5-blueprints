package com.packt.controller.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.packt.dal.CountryDAO;
import com.packt.external.WorldBankApiClient;
import com.packt.model.Country;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/countries")
@Slf4j
public class CountryAPIController {
	
	@Autowired CountryDAO countryDao;
	@Autowired WorldBankApiClient worldBankApiClient;
	
	@GetMapping
	public ResponseEntity<?> getCountries(
		@RequestParam(name="search", required = false) String searchTerm,
		@RequestParam(name="continent", required = false) String continent,
		@RequestParam(name="region", required = false) String region,
		@RequestParam(name="pageNo", required = false) Integer pageNo
	){
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("search", searchTerm);
			params.put("continent", continent);
			params.put("region", region);
			if ( pageNo != null ) {
				params.put("pageNo", pageNo.toString());
			}
			
			List<Country> countries = countryDao.getCountries(params);
			Map<String, Object> response = new HashMap<>();
			response.put("list", countries);
			response.put("count", countryDao.getCountriesCount(params));
			return new ResponseEntity<>(response, HttpStatus.OK);
		}catch(Exception ex) {
			log.error("Error while getting countries", ex);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/{countryCode}")
	public ResponseEntity<?> editCountry(
		@PathVariable String countryCode, @Valid @RequestBody Country country ){
		try {
			//System.out.println(country);
			countryDao.editCountryDetail(countryCode, country);
			Country countryFromDb = countryDao.getCountryDetail(countryCode);
			//System.out.println(countryFromDb);
			return new ResponseEntity<>(countryFromDb, HttpStatus.OK);
		}catch(Exception ex) {
			log.error("Error while editing the country: {} with data: {}", countryCode, country, ex);
			return new ResponseEntity<>("Error while ediiting the country", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/{countryCode}/gdp")
	public ResponseEntity<?> getGDP(@PathVariable String countryCode){
		try {
			return ResponseEntity.ok(worldBankApiClient.getGDP(countryCode));
		}catch(Exception ex) {
			log.error("Error while getting GDP for country: {}", countryCode, ex);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body("Error while getting the GDP");
		}
	}
	
}
