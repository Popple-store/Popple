package com.popple.exhibition.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.popple.exhibition.entity.Poster;

@Repository
public interface PosterRepository extends JpaRepository<Poster, Long> {

}
