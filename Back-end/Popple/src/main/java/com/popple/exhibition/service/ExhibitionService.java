package com.popple.exhibition.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.popple.auth.entity.User;
import com.popple.exhibition.domain.ExhibitionRequest;
import com.popple.exhibition.domain.ExhibitionResponse;
import com.popple.exhibition.entity.Exhibition;
import com.popple.exhibition.entity.Image;
import com.popple.exhibition.entity.Poster;
import com.popple.exhibition.repository.ExhibitionRepository;
import com.popple.exhibition.repository.ImageRepository;
import com.popple.exhibition.repository.PosterRepository;
import com.popple.type.ExhiType;
import com.popple.type.repository.ExhiTypeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExhibitionService {
	private final ExhibitionRepository exhibitionRepository;
	private final ExhiTypeRepository exhiTypeRepository;
	private final PosterRepository posterRepository;
	private final ImageRepository imageRepository;
	private final ImageService imageService;
	private final PosterService posterService;
	
	// 팝업/전시 생성
	public ExhibitionResponse createExhibition(ExhibitionRequest req, List<MultipartFile> images, List<MultipartFile> posters, User user) {
		// 타입 가져오기
		System.out.println(req.getTypeId());
		ExhiType type = exhiTypeRepository.findById(req.getTypeId()).orElseThrow(() -> new IllegalArgumentException("해당 분류가 없습니다."));
		
		// Exhibition 객체를 요청 데이터로 초기화
	    Exhibition exhibition = Exhibition.builder()
	            .user(user)  // 현재 사용자 설정
	            .type(type)
	            .exhibitionName(req.getExhibitionName())
	            .subTitle(req.getSubTitle())
	            .detailDescription(req.getDetailDescription())
	            .address(req.getAddress())
	            .notice(req.getNotice())
	            .terms(req.getTerms())
	            .grade(req.isGrade())
	            .fee(req.getFee())
	            .homepageLink(req.getHomepageLink())
	            .instagramLink(req.getInstagramLink())
	            .park(req.isPark())
	            .free(req.isFree())
	            .pet(req.isPet())
	            .food(req.isFood())
	            .wifi(req.isWifi())
	            .camera(req.isCamera())
	            .kids(req.isKids())
	            .sunday(req.getSunday())
	            .monday(req.getMonday())
	            .tuesday(req.getTuesday())
	            .wednesday(req.getWednesday())
	            .thursday(req.getThursday())
	            .friday(req.getFriday())
	            .saturday(req.getSaturday())
	            .startAt(req.getStartAt())
	            .endAt(req.getEndAt())
	            .reserve(req.isReserve())
	            .build();
	    
	    
	    // Exhibition 객체를 데이터베이스에 저장
	    exhibitionRepository.save(exhibition);
	    
	    // 이미지 저장하기
	    images.stream().map(image -> imageService.saveImage(image, exhibition)).collect(Collectors.toList());
	    // 포스터 저장하기
	    posters.stream().map(poster -> posterService.savePoster(poster, exhibition)).collect(Collectors.toList());

	    // ExhibitionResponse 생성
	    ExhibitionResponse response = convertToExhibitionResponse(exhibition);
	    
	    return response; // 응답 반환
	}
	
	// 팝업/전시 삭제
	public ExhibitionResponse deleteExhibition(Long id, User user) {
		Exhibition exhibition = exhibitionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 팝업/전시를 찾을 수 없습니다."));
		// 삭제 여부 바꿔주고
		exhibition.setDeleted(true);
		// 저장
		exhibitionRepository.save(exhibition);
		// id, 전시명, 삭제여부 만 담아서 response
		return ExhibitionResponse.builder()
	            .id(exhibition.getId())
	            .exhibitionName(exhibition.getExhibitionName())
	            .isDeleted(exhibition.isDeleted())
	            .build();
	}
	
	// 팝업/전시 전체 조회 - 모든 전시를 조회
	public List<ExhibitionResponse> getAllExhibition() {
		List<Exhibition> exhibitions = exhibitionRepository.findAll();
		
		return exhibitions.stream()
				.map(e -> {
					Optional<String> savedName = posterRepository.findFirstByExhibition(e.getId());
					Optional<String> descriptionImage = imageRepository.findFirstByExhibition(e.getId());
					return entityToResponse(e, savedName.get(), descriptionImage.get());
				})
//				.map(this::entityToResponse)
				.collect(Collectors.toList());
	}
	
	// 팝업/전시 선택 전체 조회
	public List<ExhibitionResponse> getAllExhibition(Long id) {
		log.info("타입으로 리스트 조회 : {}", id);
		ExhiType type = exhiTypeRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("잘못된 타입 요청입니다."));
		List<Exhibition> exhibitions = exhibitionRepository.findAllByType(type);

		// exhibitions의 각 요소를 response로 변환후 리스트화 하고 반환	
		return exhibitions.stream()
				.map(e -> {
					String savedName = posterRepository.findFirstByExhibition(e.getId()).orElse(null);
					String descriptionImage = imageRepository.findFirstByExhibition(e.getId()).orElse(null);
					return entityToResponse(e, savedName, descriptionImage);
				})
				.collect(Collectors.toList());
	}
	
	// 특정 팝업 or 전시 조회(디테일)
	public ExhibitionResponse getExhibitionById(Long id) {
		Exhibition exhibition = exhibitionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 팝업 혹은 전시가 존재하지 않습니다."));
		exhibition.setVisitCount(exhibition.getVisitCount()+1);
		Exhibition savedExhibition = exhibitionRepository.save(exhibition);
		ExhibitionResponse exhibitionResponse = convertToExhibitionResponse(savedExhibition);
		return exhibitionResponse;
	}

	// 자신이 만든 팝업/전시 찾기
	public List<ExhibitionResponse> getAllExhibitionByUser(User user) {
		// User로 모든 팝업/전시 찾고
		List<Exhibition> myExhibitions = exhibitionRepository.findAllByUser(user);
		// 엔티티를 리스폰스로 변환 후
		List<ExhibitionResponse> myExhibitionsResponse = myExhibitions.stream().map(this::convertToExhibitionResponse).collect(Collectors.toList());
		// 반환
		return myExhibitionsResponse;
	}
	
	// 키워드 검색
	public List<ExhibitionResponse> searchByKeyword(String keyword) {
		List<Exhibition> exList = exhibitionRepository.findByExhibitionNameContainsOrAddressContains(keyword, keyword);
		return exList.stream().map(this::convertToExhibitionResponse).collect(Collectors.toList());
	}
	
	// Exhibition 엔티티를 ExhibitionResponse로 변환하는 메서드
    public ExhibitionResponse convertToExhibitionResponse(Exhibition exhibition) {
    	String savedImage = posterRepository.findFirstByExhibition(exhibition.getId()).orElse(null);
    	String descriptionImage = imageRepository.findFirstByExhibition(exhibition.getId()).orElse(null);
        return ExhibitionResponse.builder()
                .id(exhibition.getId())
                .typeId(exhibition.getType().getId())
                .exhibitionName(exhibition.getExhibitionName())
                .subTitle(exhibition.getSubTitle())
                .detailDescription(exhibition.getDetailDescription())
                .address(exhibition.getAddress())
                .notice(exhibition.getNotice())
                .terms(exhibition.getTerms())
                .grade(exhibition.isGrade())
                .fee(exhibition.getFee())
                .homepageLink(exhibition.getHomepageLink())
                .instagramLink(exhibition.getInstagramLink())
                .park(exhibition.isPark())
                .free(exhibition.isFree())
                .pet(exhibition.isPet())
                .food(exhibition.isFood())
                .wifi(exhibition.isWifi())
                .camera(exhibition.isCamera())
                .kids(exhibition.isKids())
                .sunday(exhibition.getSunday())
                .monday(exhibition.getMonday())
                .tuesday(exhibition.getTuesday())
                .wednesday(exhibition.getWednesday())
                .thursday(exhibition.getThursday())
                .friday(exhibition.getFriday())
                .saturday(exhibition.getSaturday())
                .startAt(exhibition.getStartAt())
                .endAt(exhibition.getEndAt())
                .savedImage(savedImage)
                .reserve(exhibition.isReserve())
                .descriptionImage(descriptionImage)
                .build();
    }

	// [조회에서 사용] Exhibition -> Response로 변환 메서드
	// {{{{ 이미지 넣어야해 }}}}
	private ExhibitionResponse entityToResponse(Exhibition exhibition, String savedImage, String descriptionImage) {
		return ExhibitionResponse.builder()
				.id(exhibition.getId())
				.typeId(exhibition.getType().getId())
				.exhibitionName(exhibition.getExhibitionName())
				.address(exhibition.getAddress())
				.startAt(exhibition.getStartAt())
				.endAt(exhibition.getEndAt())
				.savedImage(savedImage)
				.visitCount(exhibition.getVisitCount())
				.reserve(exhibition.isReserve())
				.descriptionImage(descriptionImage)
				.build();
	}
	// 팝업/전시 수정
//	public ExhibitionResponse updateExhibition(Long id, ExhibitionRequest exhibitionRequest, List<MultipartFile> images, List<MultipartFile> posters, User user) throws IOException {
//		Exhibition exhibition = exhibitionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 팝업/전시를 찾을 수 없습니다."));
//		
//		// 현재 사용자가 작성자가 맞는지 확인
//		if (!exhibition.getUser().getId().equals(user.getId())) {
//			throw new AccessDeniedException("본인이 만든 팝업/전시만 수정할 수 있습니다.");
//		}
//		
//		// 이미지들을 삭제
//		List<Image> prevImages = exhibition.getImages();
//		prevImages.forEach(image -> imageService.deleteImage(image.getId()));
//		
//		// 새로운 이미지들을 저장
//		List<Image> savedImages = images.stream().map(image -> imageService.saveImage(image)).collect(Collectors.toList());
//		
//		// 포스터들을 삭제
//		List<Poster> prevPosters = exhibition.getPosters();
//		prevPosters.forEach(poster -> posterService.deletePoster(poster.getId()));
//		
//		// 새로운 포스터들을 저장
//		List<Poster> savedPosters = posters.stream().map(poster -> posterService.savePoster(poster)).collect(Collectors.toList());
//		
//		// 바뀐 타입 불러오기
//		ExhiType changeType = exhiTypeRepository.findById(exhibitionRequest.getTypeId()).orElseThrow(() -> new IllegalArgumentException("해당 분류를 찾을 수 없습니다."));
//		
//		// Exhibition 객체를 요청 데이터로 업데이트
//	    exhibition.setType(changeType);
//	    exhibition.setExhibitionName(exhibitionRequest.getExhibitionName());
//	    exhibition.setSubTitle(exhibitionRequest.getSubTitle());
//	    exhibition.setDetailDescription(exhibitionRequest.getDetailDescription());
//	    exhibition.setAddress(exhibitionRequest.getAddress());
//	    exhibition.setNotice(exhibitionRequest.getNotice());
//	    exhibition.setTerms(exhibitionRequest.getTerms());
//	    exhibition.setGrade(exhibitionRequest.isGrade());
//	    exhibition.setFee(exhibitionRequest.getFee());
//	    exhibition.setHomepageLink(exhibitionRequest.getHomepageLink());
//	    exhibition.setInstagramLink(exhibitionRequest.getInstagramLink());
//	    exhibition.setPark(exhibitionRequest.isPark());
//	    exhibition.setFree(exhibitionRequest.isFree());
//	    exhibition.setPet(exhibitionRequest.isPet());
//	    exhibition.setFood(exhibitionRequest.isFood());
//	    exhibition.setWifi(exhibitionRequest.isWifi());
//	    exhibition.setCamera(exhibitionRequest.isCamera());
//	    exhibition.setKids(exhibitionRequest.isKids());
//	    exhibition.setSunday(exhibitionRequest.getSunday());
//	    exhibition.setMonday(exhibitionRequest.getMonday());
//	    exhibition.setTuesday(exhibitionRequest.getTuesday());
//	    exhibition.setWednesday(exhibitionRequest.getWednesday());
//	    exhibition.setThursday(exhibitionRequest.getThursday());
//	    exhibition.setFriday(exhibitionRequest.getFriday());
//	    exhibition.setSaturday(exhibitionRequest.getSaturday());
//	    exhibition.setStartAt(exhibitionRequest.getStartAt());
//	    exhibition.setEndAt(exhibitionRequest.getEndAt());
////	    exhibition.setImages(savedImages);
////	    exhibition.setPosters(savedPosters);
//		
//		exhibitionRepository.save(exhibition);
//	    
//		return convertToExhibitionResponse(exhibition);
//	}
	

	public ExhibitionResponse updateExhi(User user, ExhibitionRequest req, List<MultipartFile> images,
			MultipartFile poster) {
		Exhibition ex = exhibitionRepository.findById(req.getTypeId()).orElseThrow(() -> new IllegalArgumentException("해당 팝업/전시를 찾을 수 없습니다."));
		List<Image> prevImage = imageService.findAll();
		Poster prevPoster = posterService.findPoster(req.getExhiId());
		if(ex.getUser().getId() == user.getId()) {
			ex.setDetailDescription(req.getDetailDescription());
			ex.setAddress(req.getAddress());
			ex.setStartAt(req.getStartAt());
			ex.setEndAt(req.getEndAt());
			ex.setExhibitionName(req.getExhibitionName());
			ex.setInstagramLink(req.getInstagramLink());
			ex.setHomepageLink(req.getHomepageLink());
			ex.setSubTitle(req.getSubTitle());
			ex.setReserve(req.isReserve());
			ex.setNotice(req.getNotice());
			
			if(images != null) {
				prevImage.forEach(i -> imageService.deleteImage(i.getId()));
				images.stream().map(image -> imageService.saveImage(image, ex)).collect(Collectors.toList());
			}
			if(poster != null) {
				posterService.deletePoster(prevPoster.getId());
				posterService.savePoster(poster, ex);
			}
		}
		exhibitionRepository.save(ex);
		
		return convertToExhibitionResponse(ex);
	}

	

    
}
