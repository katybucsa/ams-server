package ro.ubbcluj.cs.ams.course.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PostsResponseDto {

    private List<PostResponseDto> data;
}
