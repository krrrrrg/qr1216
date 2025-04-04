package org.zerock.restqrpayment_2.service;

import org.zerock.restqrpayment_2.dto.MemberDTO;

import java.util.List;

public interface MemberService {
    String register(MemberDTO memberDTO); // Create
    MemberDTO read(String userId);           // Read
    void modify(MemberDTO memberDTO);     // Update
    void remove(String userId);              // Delete

    List<MemberDTO> getMemberList();
    
    void changePassword(String userId, String currentPassword, String newPassword);
    
    void deleteAccount(String userId);
    
    void verifyPassword(String userId, String password);
    
    boolean authenticate(String userId, String password);
    
    List<String> findIdsByNameAndPhone(String name, String phone);
    
    void resetPasswordAndSendToPhone(String userId, String phone);
    
    boolean isOwner(String userId);
    boolean isUser(String userId);
    String resetPassword(String userId, String phone);
}
