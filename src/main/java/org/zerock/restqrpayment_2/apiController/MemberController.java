package org.zerock.restqrpayment_2.apiController;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.restqrpayment_2.dto.MemberDTO;
import org.zerock.restqrpayment_2.dto.PasswordChangeDTO;
import org.zerock.restqrpayment_2.service.MemberService;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@Log4j2
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원 등록
    @PostMapping
    public ResponseEntity<String> registerMember(@RequestBody MemberDTO memberDTO) {
        log.info("Registering member: {}", memberDTO);

        try {
            String mid = memberService.register(memberDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(mid); // 201 Created
        } catch (Exception e) {
            log.error("Error registering member: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Registration failed");
        }
    }

    // 모든 회원 조회
    @GetMapping
    public ResponseEntity<List<MemberDTO>> getAllMembers() {
        log.info("Fetching all members");

        try {
            List<MemberDTO> members = memberService.getMemberList();
            return ResponseEntity.ok(members); // 200 OK
        } catch (Exception e) {
            log.error("Error fetching members: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 특정 회원 조회
    @GetMapping("/{mid}")
    public ResponseEntity<MemberDTO> getMemberById(@PathVariable String mid) {
        log.info("Fetching member with ID: {}", mid);

        try {
            MemberDTO memberDTO = memberService.read(mid);
            return ResponseEntity.ok(memberDTO); // 200 OK
        } catch (Exception e) {
            log.error("Error fetching member: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
        }
    }

    // 회원 정보 수정
    @PutMapping("/{mid}")
    public ResponseEntity<String> updateMember(
            @RequestBody MemberDTO memberDTO) {

        try {
            memberService.modify(memberDTO);
            return ResponseEntity.ok("Member updated successfully"); // 200 OK
        } catch (Exception e) {
            log.error("Error updating member: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Update failed");
        }
    }

    // 회원 삭제
    @DeleteMapping("/{mid}")
    public ResponseEntity<String> deleteMember(@PathVariable String mid) {
        log.info("Deleting member with ID: {}", mid);

        try {
            memberService.remove(mid);
            return ResponseEntity.ok("Member deleted successfully"); // 200 OK
        } catch (Exception e) {
            log.error("Error deleting member: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Deletion failed");
        }
    }

    // 비밀번호 변경
    @PutMapping("/{mid}/password")
    public ResponseEntity<String> changePassword(
            @PathVariable String mid,
            @RequestBody PasswordChangeDTO passwordChangeDTO) {
        log.info("changePassword for user: " + mid);

        try {
            if (!passwordChangeDTO.getNewPassword().equals(passwordChangeDTO.getConfirmPassword())) {
                return ResponseEntity.badRequest().body("새 비밀번호와 확인 비밀번호가 일치하지 않습니다.");
            }

            memberService.changePassword(
                mid,
                passwordChangeDTO.getCurrentPassword(),
                passwordChangeDTO.getNewPassword()
            );
            
            return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
