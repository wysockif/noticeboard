package pl.wysockif.noticeboard.mappers.notice;

import org.apache.tomcat.util.buf.StringUtils;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;
import pl.wysockif.noticeboard.dto.notice.requests.PostNoticeRequest;
import pl.wysockif.noticeboard.dto.notice.snapshots.NoticeSnapshot;
import pl.wysockif.noticeboard.entities.notice.Notice;

import java.util.List;

@Mapper
public interface NoticeMapper {
    NoticeMapper INSTANCE = Mappers.getMapper(NoticeMapper.class);

    NoticeSnapshot noticeToNoticeSnapshot(Notice notice);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(source = "keywords", target = "keywords", qualifiedByName = "keywordsListToKeywordsString")
    Notice postNoticeRequestToNotice(PostNoticeRequest postNoticeRequest);

    @Named("keywordsListToKeywordsString")
    default String keywordsListToKeywordsString(List<String> keywordsList) {
        return StringUtils.join(keywordsList, ' ');
    }

}
