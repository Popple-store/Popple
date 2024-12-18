package com.popple.reservation.domain;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationResponse {
	private Long id,exhibitionId, exhiTypeId;
	private String exhibitionName, reserver, address;
	private LocalDate reservationDate;
	private boolean isDeleted, isAttend;

}	
