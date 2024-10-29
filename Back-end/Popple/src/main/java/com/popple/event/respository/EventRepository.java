package com.popple.event.respository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.popple.event.domain.EventResponse;
import com.popple.event.entity.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

}
