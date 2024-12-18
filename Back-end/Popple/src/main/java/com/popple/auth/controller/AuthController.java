package com.popple.auth.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.popple.auth.domain.request.SignUpRequest;
import com.popple.auth.domain.request.UserDeleteRequest;
import com.popple.auth.domain.request.UserEditRequest;
import com.popple.auth.domain.request.UserPasswordCheckRequest;
import com.popple.auth.domain.response.LoginResponse;
import com.popple.auth.domain.response.SignUpResponse;
import com.popple.auth.entity.User;
import com.popple.auth.service.AuthService;
import com.popple.common.utils.TokenUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestController
@Tag(name = "Auth", description = "사용자 로그인 관련 API")
@RequestMapping("/api/auth")

public class AuthController {
	private final TokenUtils tokenUtils;
	private final AuthService authService;
	
	//회원가입
	@Operation(summary = "회원가입", description = "회원가입을 진행합니다.")
	@PostMapping("/create")
	public ResponseEntity<SignUpResponse> signUp(@RequestBody SignUpRequest req){
		log.info("[SignUp] 회원가입 정보 : {}", req);
		SignUpResponse signUpResponse =  authService.signUp(req);
		return ResponseEntity.status(HttpStatus.CREATED).body(signUpResponse);
	}
	
	//이메일 중복 확인
	@Operation(summary = "이메일 중복 확인", description = "중복이 아니면 true 중복이면 false 반환.")
	@GetMapping("/email")
	public ResponseEntity<Boolean> emailCheck(@RequestParam("email") String email){
		boolean isNotDuplicate = authService.duplicateEmailCheck(email);
		return ResponseEntity.ok(isNotDuplicate);
		
	}
	//특정 회원 조회
	@Operation(summary = "특정 회원 조회", description = "특정 회원 정보를 반환합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<SignUpResponse> getUser(@PathVariable("id") Long id){
		SignUpResponse res = authService.getUser(id);
		return ResponseEntity.ok(res);
	}
	

	//닉네임 중복 확인
	@Operation(summary = "닉네임 중복 확인", description = "중복이 아니면 true 중복이면 false 반환.")
	@GetMapping("/nickname")
	public ResponseEntity<Boolean> nicknameCheck(@RequestParam("nickname") String nickname){
		boolean isNotDuplicate = authService.duplicateNicknameCheck(nickname);
		return ResponseEntity.ok(isNotDuplicate);
	}

	//회원 탈퇴
	@Operation(summary = "회원탈퇴", description = "회원탈퇴를 진행합니다.")
	@PatchMapping("/delete")
	public ResponseEntity<?> deleteUser(@AuthenticationPrincipal User user){
		if(user != null) {
			authService.deleteUser(user);
			return ResponseEntity.ok(null);
		}else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없음");
		}
	}
	
	//회원 정보 수정
	@Operation(summary = "회원수정", description = "회원수정을 진행합니다.")
	@PatchMapping("/update")
	public ResponseEntity<LoginResponse> updateUser(HttpServletResponse res, @AuthenticationPrincipal User user, @RequestBody UserEditRequest userEditRequest){
		Map<String, String> tokenMap = authService.updateUser(user, userEditRequest);
		// 회원 수정 시 토큰 재발급 진행
		if(tokenMap == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		} else {
			// HEADER에 추가(refresh)
			tokenUtils.setRefreshTokenCookie(res, tokenMap.get("refreshToken"));
			return ResponseEntity.ok(LoginResponse.builder().accessToken(tokenMap.get("accessToken")).build());
		}
	}
	
	//토큰 재발급
	@Operation(summary = "토큰 재발급", description = "토큰을 재발급합니다.")
	@PostMapping("/refresh-token")
	public ResponseEntity<LoginResponse> refreshToken(HttpServletRequest req, HttpServletResponse res){
		Map<String, String> tokenMap = authService.refreshToken(req);
		
		if(tokenMap == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		tokenUtils.setRefreshTokenCookie(res, tokenMap.get("refreshToken"));
		
		return ResponseEntity.ok(LoginResponse.builder().accessToken(tokenMap.get("accessToken")).build());
	}
	// 사용자의 비밀번호 확인
	@Operation(summary = "비밀번호 확인", description = "사용자가 입력한 비밀번호를 확인합니다.")
	@PostMapping("/check-password")
	public ResponseEntity<String> passwordCheck(@AuthenticationPrincipal User user, @RequestBody UserPasswordCheckRequest userPasswordCheckRequest) {
			log.info("userPassword : {}", userPasswordCheckRequest);
		try {
					boolean isMatched = authService.checkPassword(user, userPasswordCheckRequest.getPassword());
					if (isMatched) {
						return ResponseEntity.ok("비밀번호가 일치합니다.");
					}
			} catch (RuntimeException e) {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
			}
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("비밀번호가 일치하지 않습니다.");
	}
}
