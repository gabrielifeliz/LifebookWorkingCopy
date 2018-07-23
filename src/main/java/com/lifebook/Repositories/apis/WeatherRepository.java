package com.lifebook.Repositories.apis;

import com.lifebook.Model.Weather;
import org.springframework.data.repository.CrudRepository;

public interface WeatherRepository extends CrudRepository<Weather, Long> {
}