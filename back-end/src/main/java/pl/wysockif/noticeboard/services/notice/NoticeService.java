package pl.wysockif.noticeboard.services.notice;

import org.springframework.stereotype.Service;
import pl.wysockif.noticeboard.dto.notice.requests.PostNoticeRequest;
import pl.wysockif.noticeboard.entities.notice.Notice;
import pl.wysockif.noticeboard.mappers.notice.NoticeMapper;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;

import java.util.Date;
import java.util.logging.Logger;

@Service
public class NoticeService {

    private final Logger LOGGER = Logger.getLogger(NoticeService.class.getName());
    private final NoticeRepository noticeRepository;

    public NoticeService(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    public Long save(PostNoticeRequest postNoticeRequest) {
        LOGGER.info("Creating notice");
        Notice createdNotice = NoticeMapper.INSTANCE.postNoticeRequestToNotice(postNoticeRequest);
        createdNotice.setCreatedAt(new Date());
        Notice savedNotice = noticeRepository.save(createdNotice);
        LOGGER.info("Created notice");
        return savedNotice.getId();
    }
}
