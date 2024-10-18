package com.popple.exhibition.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.popple.auth.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exhibition {
	//id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(updatable = false)
	private Long id;
	
	//user id
	@JoinColumn(name = "user_id", nullable = false)
	@ManyToOne
	private User user;
	
	//입장료
	@Column(nullable = true)
	private String fee;
	
	//전시명
	@Column(nullable = false)
	private String exhibitionName;
	
	//부제
	@Column(nullable = false)
	private String subTitle;
	
	//상세설명
	@Column(nullable = true)
	private String detailDescription;
	
	//관람시간(이건 필요한가?)
	@Column(nullable = true)
	private String permittedTime;
	
	//전시주소
	@Column(nullable = false)
	private String address;
	
	//공지사항
	@Column(nullable = true)
	private String notice;
	
	//이용조건
	@Column(nullable = true)
	private String terms;
	
	//관람등급
	@Column(nullable = false)
	private String grade;
	
	//홈페이지 링크
	@Column(nullable = true)
	private String homepageLink;
	
	//인스타그램 링크
	@Column(nullable = true)
	private String instagramLink;
	
	//주차 여부
	@Column(nullable = true)
	private boolean park;
	
	//입장료 여부
	@Column(nullable = true)
	private boolean free;
	
	//동물 출입 여부
	@Column(nullable = true)
	private boolean pet;
	
	//음식 반입 여부
	@Column(nullable = true)
	private boolean food;
	
	//와이파이 여부
	@Column(nullable = true)
	private boolean wifi;
	
	//사진촬영 여부
	@Column(nullable = true)
	private boolean camera;
	
	//애들 출입 여부
	@Column(nullable = true)
	private boolean kids;
	
	//일요일
	@Column(nullable = true)
	private String sunday;
	
	//월요일
	@Column(nullable = true)
	private String monday;
	
	//화요일
	@Column(nullable = true)
	private String tuesday;
	
	//수요일
	@Column(nullable = true)
	private String wednesday;
	
	//목요일
	@Column(nullable = true)
	private String thursday;
	
	//금요일
	@Column(nullable = true)
	private String friday;
	
	//토요일
	@Column(nullable = true)
	private String saturday;
	
	//생성일자
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	
	//시작일자
	@Column(name = "start_at", nullable = false)
	private LocalDate startAt;
	
	//종료일자
	@Column(name = "end_at", nullable = false)
	private LocalDate endAt;
	
	//삭제여부
	@Column(name = "is_deleted", nullable = true)
	private boolean isDeleted;
	
	
}
