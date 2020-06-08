package ro.ubbcluj.cs.ams.course.dto.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Post;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostRequestDto {

    @NotNull
    private String title;

    @NotNull
    private String text;

    private String courseId;
}
