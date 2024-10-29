package com.popple.event.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.popple.event.entity.Event;
import com.popple.event.entity.EventPoster;

@Repository
public interface EventPosterRepository extends JpaRepository<EventPoster, Long> {

	EventPoster findOneByEvent(Event e);

}
