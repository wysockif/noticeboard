package pl.wysockif.noticeboard.services.notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.wysockif.noticeboard.dto.notice.requests.PostNoticeRequest;
import pl.wysockif.noticeboard.entities.notice.Notice;
import pl.wysockif.noticeboard.entities.user.AppUser;
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

    public Long save(PostNoticeRequest postNoticeRequest, AppUser creator) {
        LOGGER.info("Creating notice");
        Notice createdNotice = NoticeMapper.INSTANCE.postNoticeRequestToNotice(postNoticeRequest);
        createdNotice.setCreatedAt(new Date());
        createdNotice.setCreator(creator);
        Notice savedNotice = noticeRepository.save(createdNotice);
        LOGGER.info("Created notice");
        return savedNotice.getId();
    }

    public Page<Notice> getNotices(Pageable pageable) {
        LOGGER.info("Getting notices");
//        Pageable pageable = PageRequest.of(0, 18);
        return noticeRepository.findAll(pageable);
    }
}
