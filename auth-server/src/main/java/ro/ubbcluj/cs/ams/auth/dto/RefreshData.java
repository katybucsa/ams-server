package ro.ubbcluj.cs.ams.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RefreshData {

    @NotBlank
    private String grant_type;

    @NotBlank
    private String refresh_token;

    @NotBlank
    private String username;

    @NotBlank
    private String clientId;

    @NotBlank
    private String clientSecret;
}
