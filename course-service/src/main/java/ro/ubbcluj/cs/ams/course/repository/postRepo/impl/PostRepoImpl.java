package ro.ubbcluj.cs.ams.course.repository.postRepo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.course.model.Tables;
import ro.ubbcluj.cs.ams.course.model.tables.pojos.Post;
import ro.ubbcluj.cs.ams.course.model.tables.records.PostRecord;
import ro.ubbcluj.cs.ams.course.repository.postRepo.PostRepo;

import java.util.List;

@Repository
public class PostRepoImpl implements PostRepo {

    @Autowired
    private DSLContext dsl;

    private final Logger LOGGER = LogManager.getLogger(PostRepoImpl.class);

    @Override
    public PostRecord addPost(Post post) {

        LOGGER.info("========== LOGGING addPost ==========");

        PostRecord postRecord = dsl.insertInto(Tables.POST, Tables.POST.TITLE, Tables.POST.TEXT, Tables.POST.COURSE_ID)
                .values(post.getTitle(), post.getText(), post.getCourseId())
                .returning()
                .fetchOne();

        LOGGER.info("========== SUCCESSFUL LOGGING addPost ==========");
        return postRecord;
    }

    @Override
    public List<PostRecord> findPostsByCourseId(String courseId) {

        LOGGER.info("========== LOGGING findPostsByCourseId ==========");

        List<PostRecord> postRecords = dsl.selectFrom(Tables.POST)
                .where(Tables.POST.COURSE_ID.eq(courseId))
                .fetch();

        LOGGER.info("========== SUCCESSFUL LOGGING findPostsByCourseId ==========");
        return postRecords;
    }
}
