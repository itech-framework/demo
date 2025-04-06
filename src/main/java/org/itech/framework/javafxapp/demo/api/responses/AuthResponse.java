package org.itech.framework.javafxapp.demo.api.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AuthResponse {
    private String email;
    private String accessToken;
}
