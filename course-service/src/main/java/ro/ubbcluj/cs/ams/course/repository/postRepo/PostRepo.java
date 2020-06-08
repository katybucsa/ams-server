package ro.ubbcluj.cs.ams.course.repository.postRepo;

import ro.ubbcluj.cs.ams.course.model.tables.pojos.Post;
import ro.ubbcluj.cs.ams.course.model.tables.records.PostRecord;

import java.util.List;

public interface PostRepo {

    PostRecord addPost(Post post);

    List<PostRecord> findPostsByCourseId(String courseId);
}
