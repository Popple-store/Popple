package com.popple.reservation.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.popple.auth.entity.User;
import com.popple.auth.service.AuthService;
import com.popple.exhibition.entity.Exhibition;
import com.popple.exhibition.repository.ExhibitionRepository;
import com.popple.reservation.domain.ReservationRequest;
import com.popple.reservation.domain.ReservationResponse;
import com.popple.reservation.domain.ReserverResponse;
import com.popple.reservation.entity.Reservation;
import com.popple.reservation.repository.ReservationRepository;
import com.popple.visit.service.VisitService;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReservationService {
	private final ReservationRepository reservationRepository;
	private final ExhibitionRepository exhibitionRepository;
	private final VisitService visitService;
	private final AuthService authService;
	private final JavaMailSender javaMailSender;
	
	// 예약
	public ReservationResponse reserve(ReservationRequest request, User user) {
		Exhibition exhibition = exhibitionRepository.findById(request.getExhibitionId()).orElseThrow(() -> new IllegalArgumentException("해당 팝업이 유효하지 않습니다."));
		Reservation reservation = new Reservation();
		reservation.setUser(user);
		reservation.setExhibition(exhibition);
		reservation.setReservationDate(request.getReservationDate());
		
		reservationRepository.save(reservation);
		
		return ReservationResponse.builder()
				.id(reservation.getId())
				.reserver(user.getName())
				.exhibitionName(exhibition.getExhibitionName())
				.reservationDate(reservation.getReservationDate())
				.build();
	}

	// 자신이 예약한 리스트
	public List<ReservationResponse> getAllReserve(User user) {
		List<Reservation> reserveList = reservationRepository.findByUser(user);
		List<ReservationResponse> resList = reserveList.stream()
				.filter(reserve -> reserve.getDeletedAt() == null)		
				.map(reserve -> ReservationResponse.builder()
				.id(reserve.getId())
				.exhibitionId(reserve.getExhibition().getId())
				.exhiTypeId(reserve.getExhibition().getType().getId())
				.exhibitionName(reserve.getExhibition().getExhibitionName())
				.address(reserve.getExhibition().getAddress())
				.reserver(reserve.getUser().getName())
		    .reservationDate(reserve.getReservationDate())
				.build())
			.collect(Collectors.toList());
		return resList;
	}

	// 특정 팝업/전시에 대한 예약자 리스트
	public List<ReserverResponse> getReserveList(Long id) {
		Exhibition exhibition = exhibitionRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("유효하지 않은 팝업/전시 입니다."));
		List<Reservation> reserverList = reservationRepository.findByExhibition(exhibition);
		return reserverList.stream()
				.filter(reservation -> reservation.getDeletedAt()==null) // 예약취소 안된 것(deletedAt)
				.map(reservation -> ReserverResponse.builder()
				.id(reservation.getId())
				.reserverName(reservation.getUser().getName())
				.reserveTime(reservation.getReservationDate())
				.email(reservation.getUser().getEmail())
				.isAttend(reservation.isAttend())
				.build()).collect(Collectors.toList());
	}

	// 예약 취소
	public ReservationResponse cancelReserve(Long exId, User user) {
		// if (!authService.checkPassword(user, password)) {
		// 	throw new RuntimeException("비밀번호가 일치하지 않습니다.");
		// }
		// log.info("비밀번호는 일치 ");
		Reservation reservation = reservationRepository.findById(exId).orElseThrow(() -> new IllegalArgumentException("해당 팝업/전시가 존재하지 않습니다."));
		if (!user.getId().equals(reservation.getUser().getId())) {
			throw new IllegalArgumentException("예약자 본인만 취소 가능합니다.");
		}
		reservation.setDeletedAt(LocalDateTime.now());
		Reservation savedReservation = reservationRepository.save(reservation);
		return ReservationResponse.builder()
				.id(savedReservation.getId())
				.exhibitionName(savedReservation.getExhibition().getExhibitionName())
				.reserver(savedReservation.getUser().getName())
				.reservationDate(savedReservation.getReservationDate())
				.build();
	}

	// 방문 확인
	public ReserverResponse checkReserver(Long reservationId, User user) {
		Reservation reservation = reservationRepository.findByIdAndDeletedAtIsNull(reservationId).orElseThrow(() -> new IllegalArgumentException("잘못된 예약입니다."));
		reservation.setAttend(true);
		Reservation savedReservation = reservationRepository.save(reservation);
		visitService.insert(savedReservation.getExhibition().getId(), savedReservation.getUser());
		return ReserverResponse.builder()
				.id(savedReservation.getId())
				.reserverName(savedReservation.getUser().getName())
				.email(savedReservation.getUser().getEmail())
				.reserveTime(savedReservation.getReservationDate())
				.isAttend(savedReservation.isAttend())
				.build();
	}

	// 특정 팝업에 대한 나의 예약 가져오기
	public List<ReserverResponse> getMyReservationByExId(Long exId, User user) {
		Exhibition exhibition = exhibitionRepository.findById(exId).orElseThrow(() -> new IllegalArgumentException("찾는 팝업/전시가 없습니다."));
		List<Reservation> reservationList = reservationRepository.findByExhibitionAndUser(exhibition, user);
		
		return reservationList.stream().map(r -> {
			return ReserverResponse.builder()
						.id(r.getExhibition().getId())
						.reserverName(r.getUser().getName())
						.reserveTime(r.getReservationDate()) // 예약 날짜가 없음을 명시
						.isAttend(r.isAttend())
						.email(r.getUser().getEmail())
						.build();
		}).toList();
	}

    public boolean sendQRCode(String email, String title, String url) throws Exception {
		byte[] qrcode = generateQRCode(url);
		return sendEmail(email, title, qrcode);
    }

	private boolean sendEmail(String email, String title, byte[] qrcode) {
    MimeMessage message = javaMailSender.createMimeMessage();
    String text = "<h1>QR 코드를 확인해주세요.</h1><br><img src=\"cid:qrcode\"/>";

    try {
        message.setSubject(title, "UTF-8");
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email));

        // Create a multipart message for both text and QR code image
        MimeMultipart multipart = new MimeMultipart("related");

        // Part 1: Text part
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(text, "UTF-8", "html");
        multipart.addBodyPart(textPart);

        // Part 2: QR code image part
        MimeBodyPart imagePart = new MimeBodyPart();
        imagePart.setContent(qrcode, "image/png");
        imagePart.setHeader("Content-ID", "<qrcode>");
        imagePart.setDisposition(MimeBodyPart.INLINE);
        multipart.addBodyPart(imagePart);

        // Set the multipart message to the email message
        message.setContent(multipart);

        javaMailSender.send(message);
    } catch (MessagingException e) {
        log.warn("QR 코드 이메일 발송 중 Exception 발생 : {}", e.getMessage());
        return false;
    }

    return true;
}

	private byte[] generateQRCode(String url) throws Exception {
		// QR 정보
		int width = 500;
		int height = 500;
		
		// QR code - BitMatrix: qr code 정보 생성
		BitMatrix encode = new MultiFormatWriter()
				.encode(url, BarcodeFormat.QR_CODE, width, height);
		
		// QR code - Image 생성 : 1회성으로 생성
		// stream으로 Generate(1회성 아니면 File로)
		try {
			// output Stream
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			// Bitmatrix, file.format, outputStream
			MatrixToImageWriter.writeToStream(encode, "PNG", out);
			
			
			return out.toByteArray();
		} catch (Exception e) {
			log.warn("QR Code OutputStream 중 Exception 발생 : {}", e.getMessage());
		}
		return null;
	}
}

