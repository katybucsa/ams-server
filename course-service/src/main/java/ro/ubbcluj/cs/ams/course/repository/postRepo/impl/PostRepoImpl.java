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

import static ro.ubbcluj.cs.ams.course.model.tables.Post.POST;

@Repository
public class PostRepoImpl implements PostRepo {

    @Autowired
    private DSLContext dsl;

    private final Logger LOGGER = LogManager.getLogger(PostRepoImpl.class);

    @Override
    public PostRecord addPost(Post post) {

        LOGGER.info("========== LOGGING addPost ==========");

        PostRecord postRecord = dsl.insertInto(POST, POST.TITLE, POST.TEXT, POST.COURSE_ID, POST.PROFESSOR_ID, POST.TYPE, POST.DATE)
                .values(post.getTitle(), post.getText(), post.getCourseId(), post.getProfessorId(), post.getType(), post.getDate())
                .returning()
                .fetchOne();

        LOGGER.info("========== SUCCESSFUL LOGGING addPost ==========");
        return postRecord;
    }

    @Override
    public List<PostRecord> findPostsByCourseId(String courseId) {

        LOGGER.info("========== LOGGING findPostsByCourseId ==========");

        List<PostRecord> postRecords = dsl.selectFrom(POST)
                .where(POST.COURSE_ID.eq(courseId))
                .orderBy(POST.DATE.desc())
                .fetch();

        LOGGER.info("========== SUCCESSFUL LOGGING findPostsByCourseId ==========");
        return postRecords;
    }

    @Override
    public PostRecord findById(int id) {

        LOGGER.info("========== LOGGING findById ==========");

        PostRecord postRecord = dsl.selectFrom(POST)
                .where(POST.ID.eq(id))
                .fetchAny();

        LOGGER.info("========== SUCCESSFUL LOGGING findById ==========");
        return postRecord;
    }
}
