package com.popple.event.utils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.popple.event.entity.EventPoster;

@Component
public class EventPosterUtils {
	@Value("${spring.upload.event_image_location}")
	private String uploadPath;

	public EventPoster eventPosterUpload(MultipartFile eventPoster) {
		try {
			
			//multipartfile 을 file 로 변환
			File file = new File(eventPoster.getOriginalFilename());
			eventPoster.transferTo(file);

			// 원본 이미지명 가져오기
			String originalImageName = eventPoster.getOriginalFilename();
			// 이미지 크기 가져오기
			Long mageSize = eventPoster.getSize();
			// 새로운 이미지명 만들어주기
			String savedImageName = UUID.randomUUID() + "_" + originalImageName;

			// 경로에 이미지 업로드
			InputStream inputStream = eventPoster.getInputStream();
			Path path = Paths.get(uploadPath).resolve(savedImageName);
			Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
			
			return EventPoster.builder()
					.posterName(savedImageName)
					.savedName(savedImageName)
					.fileSize(mageSize)
					.build();

			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
