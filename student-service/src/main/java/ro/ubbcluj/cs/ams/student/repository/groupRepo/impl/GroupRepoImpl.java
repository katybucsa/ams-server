package ro.ubbcluj.cs.ams.student.repository.groupRepo.impl;

import org.jooq.DSLContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ro.ubbcluj.cs.ams.student.model.tables.records.SGroupRecord;
import ro.ubbcluj.cs.ams.student.repository.groupRepo.GroupRepo;

import java.util.List;

import static ro.ubbcluj.cs.ams.student.model.tables.SGroup.S_GROUP;

@Repository
public class GroupRepoImpl implements GroupRepo {

    @Autowired
    private DSLContext dsl;

    private static final Logger LOGGER = LoggerFactory.getLogger(GroupRepoImpl.class);

    @Override
    public List<SGroupRecord> findAllGroupsBySpecId(int specId) {

        LOGGER.info("========== LOGGING findAllGroupsBySpecId ==========");

        List<SGroupRecord> sGroupRecords = dsl.selectFrom(S_GROUP)
                .where(S_GROUP.SPEC_ID.eq(specId))
                .fetch();

        LOGGER.info("========== SUCCESSFULLY LOGGING findAllGroupsBySpecId ==========");
        return sGroupRecords;
    }

    @Override
    public SGroupRecord findById(Integer groupId) {

        LOGGER.info("========== LOGGING findById ==========");

        SGroupRecord sGroupRecord = dsl.selectFrom(S_GROUP)
                .where(S_GROUP.GROUP_ID.eq(groupId))
                .fetchAny();

        LOGGER.info("========== SUCCESSFULLY LOGGING findById ==========");
        return sGroupRecord;
    }
}
