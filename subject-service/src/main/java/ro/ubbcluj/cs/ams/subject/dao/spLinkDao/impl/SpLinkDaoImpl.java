package ro.ubbcluj.cs.ams.subject.dao.spLinkDao.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.subject.dao.spLinkDao.SpLinkDao;
import ro.ubbcluj.cs.ams.subject.model.Tables;
import ro.ubbcluj.cs.ams.subject.model.tables.records.SpLinkRecord;

@Repository
public class SpLinkDaoImpl implements SpLinkDao {

    @Autowired
    private DSLContext dsl;

    private final Logger logger = LogManager.getLogger(SpLinkDaoImpl.class);

    @Override
    public SpLinkRecord findSpLink(String subjectId, int typeId, String professor) {

        logger.info("========== LOGGING findSpLink ==========");

        SpLinkRecord spLinkRecord = dsl.selectFrom(Tables.SP_LINK)
                .where(Tables.SP_LINK.SUBJECT_ID.eq(subjectId))
                .and(Tables.SP_LINK.TYPE_ID.eq(typeId))
                .and(Tables.SP_LINK.USER_ID.eq(professor))
                .fetchOne();

        logger.info("========== SUCCESSFUL LOGGING findSpLink ==========");
        return spLinkRecord;
    }
}
