package com.beyond.qiin.infra.mail;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from}")
    private String from;

    @Value("${LOGIN_URL}")
    private String loginUrl;

    /**
     * 임시 비밀번호 발송
     * @param to 발송 대상 이메일 주소
     * @param tempPassword 생성된 임시 비밀번호 (평문)
     */
    public void sendTempPassword(final String to, final String tempPassword) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(from);
            message.setSubject("[QueueIn] 임시 비밀번호가 발급되었습니다.");
            message.setText("안녕하세요.\n\n" + "요청하신 계정의 임시 비밀번호는 아래와 같습니다.\n\n"
                    + "임시 비밀번호: "
                    + tempPassword + "\n\n" + "보안을 위해 로그인 후 반드시 새 비밀번호로 변경해주세요.\n\n"
                    + "로그인 하러 가기: " + loginUrl + "\n\n"
                    + "감사합니다.\nQueueIn 운영팀 드림");

            mailSender.send(message);

            log.info("임시 비밀번호 이메일 발송 완료 -> {}", to);
        } catch (Exception e) {
            log.error("임시 비밀번호 이메일 발송 실패 -> {}", to, e);
            // 메일 전송 실패 시에도 사용자 생성 트랜잭션은 커밋되도록 런타임 예외로 처리
            throw new RuntimeException("임시 비밀번호 이메일 발송 중 오류가 발생했습니다.", e);
        }
    }
}
