package ro.ubbcluj.cs.ams.subject.dto;

import org.mapstruct.Mapper;
import ro.ubbcluj.cs.ams.subject.model.tables.records.SpLinkRecord;

@Mapper(componentModel = "spring")
public interface SpLinkMapper {

    SpLinkResponseDto spLinkRecordToSpLinkResponseDto(SpLinkRecord spLinkRecord);
}
