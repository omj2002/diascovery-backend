package discovery.com.di.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String email;
    private String password;
}