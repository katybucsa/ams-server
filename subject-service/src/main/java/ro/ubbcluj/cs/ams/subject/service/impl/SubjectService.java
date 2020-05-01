package ro.ubbcluj.cs.ams.subject.service.impl;

import com.netflix.discovery.converters.Auto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import ro.ubbcluj.cs.ams.subject.controller.SubjectController;
import ro.ubbcluj.cs.ams.subject.dao.spLinkDao.SpLinkDao;
import ro.ubbcluj.cs.ams.subject.dao.specializationDao.SpecializationDao;
import ro.ubbcluj.cs.ams.subject.dao.subjectCodeDao.SubjectCodeDao;
import ro.ubbcluj.cs.ams.subject.dao.subjectDao.SubjectDao;
import ro.ubbcluj.cs.ams.subject.dto.SpLinkMapper;
import ro.ubbcluj.cs.ams.subject.dto.SpLinkResponseDto;
import ro.ubbcluj.cs.ams.subject.dto.SubjectDtoRequest;
import ro.ubbcluj.cs.ams.subject.dto.SubjectDtoResponse;
import ro.ubbcluj.cs.ams.subject.model.tables.pojos.Subject;
import ro.ubbcluj.cs.ams.subject.model.tables.pojos.SubjectCode;
import ro.ubbcluj.cs.ams.subject.model.tables.records.SpLinkRecord;
import ro.ubbcluj.cs.ams.subject.model.tables.records.SpecializationRecord;
import ro.ubbcluj.cs.ams.subject.model.tables.records.SubjectCodeRecord;
import ro.ubbcluj.cs.ams.subject.model.tables.records.SubjectRecord;
import ro.ubbcluj.cs.ams.subject.service.Service;
import ro.ubbcluj.cs.ams.subject.service.exception.SubjectExceptionType;
import ro.ubbcluj.cs.ams.subject.service.exception.SubjectServiceException;

import java.text.DecimalFormat;
import java.util.Objects;

@org.springframework.stereotype.Service
public class SubjectService implements Service {

    private final Logger logger = LogManager.getLogger(SubjectController.class);

    @Autowired
    private SpecializationDao specializationDao;

    @Autowired
    private SubjectDao subjectDao;

    @Autowired
    private SubjectCodeDao subjectCodeDao;

    @Autowired
    private SpLinkDao spLinkDao;

    @Autowired
    private SpLinkMapper spLinkMapper;

    private String prefixCode = "ML";

    private DecimalFormat myFormat = new DecimalFormat("0000");


    @Transactional
    @Override
    public SubjectDtoResponse addSubject(SubjectDtoRequest subjectDtoRequest) {

        logger.info("+++++++ LOGGING addSubject +++++++");

//        SpecializationRecord specialization = specializationDao.findById(subjectDtoRequest.getSpecId());
        Subject subject = new Subject("", subjectDtoRequest.getName(), subjectDtoRequest.getCredits(), subjectDtoRequest.getSpecId(), subjectDtoRequest.getYear());
        String subjectId = generateSubjectCode(subject);
        subject.setId(subjectId);

        SubjectRecord subjectSave = subjectDao.addSubject(subject);

        if (subjectSave == null)
            throw new SubjectServiceException("Subject \"" + subject.getName() + "\" already exists!!", SubjectExceptionType.DUPLICATE_SUBJECT, HttpStatus.BAD_REQUEST);

        logger.info("+++++++ SUCCESSFUL LOGGING addSubject +++++++");
        return SubjectDtoResponse.builder()
                .subjectId(subjectSave.getId())
                .name(subjectSave.getName())
                .credits(subjectSave.getCredits())
                .specId(subjectSave.getSpecId())
                .year(subjectSave.getYear())
                .build();
    }

    @Override
    public SpLinkResponseDto findSpLink(String subjectId, int type, String professor) {

        logger.info("========== LOGGING findSpLink ==========");

        SpLinkRecord spLinkRecord = spLinkDao.findSpLink(subjectId, type, professor);

        logger.info("========== SUCCESSFUL LOGGING findSpLink ==========");
        return spLinkMapper.spLinkRecordToSpLinkResponseDto(spLinkRecord);
    }

    private String generateSubjectCode(Subject subject) {

        SpecializationRecord specialization = specializationDao.findById(subject.getSpecId());
        if (Objects.isNull(specialization)) {
            logger.info("Specialization not found!");
            throw new SubjectServiceException("Specialization not found!", SubjectExceptionType.SPECIALIZATION_NOT_FOUND, HttpStatus.BAD_REQUEST);
        }
        String code = prefixCode + specialization.getLanguage().charAt(0);

        SubjectCodeRecord scr = subjectCodeDao.findBySubjectName(subject.getName());

        if (scr == null) { // this subject does not exist

            Integer intCode = subjectCodeDao.addSubjectCode(new SubjectCode(null, subject.getName()));
            String stringCode = myFormat.format(intCode);
            code += stringCode;
        } else {
            code += scr.getCode();
        }
        return code;
    }

}
