package ro.ubbcluj.cs.ams.course.repository.cpLinkRepo.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.course.model.Tables;
import ro.ubbcluj.cs.ams.course.model.tables.records.CpLinkRecord;
import ro.ubbcluj.cs.ams.course.repository.cpLinkRepo.CpLinkRepo;

import java.util.List;

@Repository
public class CpLinkRepoImpl implements CpLinkRepo {

    @Autowired
    private DSLContext dsl;

    private static final Logger LOGGER = LogManager.getLogger(CpLinkRepoImpl.class);

    @Override
    public CpLinkRecord findCpLink(String courseId, int typeId, String professor) {

        LOGGER.info("========== LOGGING findCpLink ==========");

        CpLinkRecord cpLinkRecord = dsl.selectFrom(Tables.CP_LINK)
                .where(Tables.CP_LINK.COURSE_ID.eq(courseId))
                .and(Tables.CP_LINK.TYPE_ID.eq(typeId))
                .and(Tables.CP_LINK.USER_ID.eq(professor))
                .fetchOne();

        LOGGER.info("========== SUCCESSFUL LOGGING findCpLink ==========");
        return cpLinkRecord;
    }

    @Override
    public List<CpLinkRecord> findAllLinksByProfessorUsername(String professorUsername) {

        LOGGER.info("========== LOGGING findAllLinksByProfessorUsername ==========");

        List<CpLinkRecord> cpLinks = dsl.selectFrom(Tables.CP_LINK)
                .where(Tables.CP_LINK.USER_ID.eq(professorUsername))
                .fetch();

        LOGGER.info("========== SUCCESSFUL LOGGING findAllLinksByProfessorUsername ==========");
        return cpLinks;
    }
}
