package com.lifebook.Repositories.apis;

import com.lifebook.Model.News;
import org.springframework.data.repository.CrudRepository;

public interface NewsRepository extends CrudRepository<News, Long> {
}