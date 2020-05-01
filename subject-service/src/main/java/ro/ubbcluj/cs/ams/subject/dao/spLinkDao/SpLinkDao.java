package ro.ubbcluj.cs.ams.subject.dao.spLinkDao;

import ro.ubbcluj.cs.ams.subject.model.tables.records.SpLinkRecord;

public interface SpLinkDao {

    SpLinkRecord findSpLink(String subjectId, int typeId, String professor);
}
