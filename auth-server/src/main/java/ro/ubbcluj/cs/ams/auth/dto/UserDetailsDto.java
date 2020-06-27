package ro.ubbcluj.cs.ams.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDetailsDto {

    private String lastName;
    private String firstName;
    private String email;
    private String username;
}
