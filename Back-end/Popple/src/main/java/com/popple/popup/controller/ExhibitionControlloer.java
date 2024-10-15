package com.popple.popup.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.popple.auth.entity.User;
import com.popple.popup.domain.ExhibitionRequest;
import com.popple.popup.domain.ExhibitionResponse;
import com.popple.popup.service.ExhibitonService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "팝업/전시	", description = "팝업/전시 관련 API")
@RequestMapping("")
public class ExhibitionControlloer {
	private final ExhibitonService popUpService;
	
	@Operation(summary = "팝업/전시 추가", description = "팝업/전시를 생성합니다.")
	@PostMapping("")
	public ResponseEntity<ExhibitionResponse> createPopUp(ExhibitionRequest popUpRequest, @AuthenticationPrincipal User user){
		ExhibitionResponse popUP = popUpService.createPopUp(popUpRequest, user);
		return ResponseEntity.status(HttpStatus.CREATED).body(popUP);
	}
	@Operation(summary = "팝업/전시 수정", description = "특정 팝업/전시를 수정합니다.")
	@PatchMapping("")
	public ResponseEntity<ExhibitionResponse> updatePopUp(ExhibitionRequest popUpRequest, @AuthenticationPrincipal User user){
		ExhibitionResponse popUp = popUpService.updatePopUp(popUpRequest, user);
		return ResponseEntity.ok(popUp);
	}
	
	@Operation(summary = "팝업/전시 삭제", description = "특정 팝업/전시를 삭제합니다.")
	@PatchMapping("")
	public ResponseEntity<ExhibitionResponse> deletePopUp(Long id, @AuthenticationPrincipal User user){
		ExhibitionResponse popUp = popUpService.deletePopUp(id, user);
		return ResponseEntity.ok(popUp);
	}
	
	
	@Operation(summary = "팝업/전시 목록", description = "팝업/전시 목록을 반환합니다.")
	@GetMapping("")
	public ResponseEntity<List<ExhibitionResponse>> getAllPopUp(){
		List<ExhibitionResponse> popUpList = popUpService.getAllPopUp();
		return ResponseEntity.ok(popUpList);
	}
	
	
	
}
