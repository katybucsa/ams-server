package ro.ubbcluj.cs.ams.course.repository.cpLinkRepo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.course.model.tables.records.CpLinkRecord;
import ro.ubbcluj.cs.ams.course.repository.cpLinkRepo.CpLinkRepo;

import java.util.List;

import static ro.ubbcluj.cs.ams.course.model.tables.CpLink.CP_LINK;

@Repository
public class CpLinkRepoImpl implements CpLinkRepo {

    @Autowired
    private DSLContext dsl;

    private static final Logger LOGGER = LogManager.getLogger(CpLinkRepoImpl.class);

    @Override
    public CpLinkRecord findCpLink(String courseId, int typeId, String professor) {

        LOGGER.info("========== LOGGING findCpLink ==========");

        CpLinkRecord cpLinkRecord = dsl.selectFrom(CP_LINK)
                .where(CP_LINK.COURSE_ID.eq(courseId))
                .and(CP_LINK.TYPE_ID.eq(typeId))
                .and(CP_LINK.USER_ID.eq(professor))
                .fetchOne();

        LOGGER.info("========== SUCCESSFULLY LOGGING findCpLink ==========");
        return cpLinkRecord;
    }

    @Override
    public List<CpLinkRecord> findAllLinksByProfessorUsername(String professorUsername) {

        LOGGER.info("========== LOGGING findAllLinksByProfessorUsername ==========");

        List<CpLinkRecord> cpLinks = dsl.selectFrom(CP_LINK)
                .where(CP_LINK.USER_ID.eq(professorUsername))
                .fetch();

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllLinksByProfessorUsername ==========");
        return cpLinks;
    }
}
