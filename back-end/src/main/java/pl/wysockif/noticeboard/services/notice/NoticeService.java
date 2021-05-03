package pl.wysockif.noticeboard.services.notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.wysockif.noticeboard.dto.notice.requests.PostNoticeRequest;
import pl.wysockif.noticeboard.entities.notice.Notice;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.mappers.notice.NoticeMapper;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.services.file.StaticFileService;

import java.util.Date;
import java.util.logging.Logger;

@Service
public class NoticeService {

    private final Logger LOGGER = Logger.getLogger(NoticeService.class.getName());
    private final NoticeRepository noticeRepository;
    private final StaticFileService staticFileService;

    public NoticeService(NoticeRepository noticeRepository, StaticFileService staticFileService) {
        this.noticeRepository = noticeRepository;
        this.staticFileService = staticFileService;
    }

    public Long save(PostNoticeRequest postNoticeRequest, AppUser creator) {
        LOGGER.info("Creating notice (userId: " + creator.getId() + ")");
        Notice createdNotice = NoticeMapper.INSTANCE.postNoticeRequestToNotice(postNoticeRequest);
        createdNotice.setCreatedAt(new Date());
        createdNotice.setCreator(creator);
        createdNotice.setPrimaryImage(staticFileService.saveNoticeImage(creator.getId().toString(), postNoticeRequest.getPrimaryImage()));
        createdNotice.setSecondaryImage(staticFileService.saveNoticeImage(creator.getId().toString(), postNoticeRequest.getSecondaryImage()));
        createdNotice.setTertiaryImage(staticFileService.saveNoticeImage(creator.getId().toString(), postNoticeRequest.getTertiaryImage()));
        Long savedNoticeId = noticeRepository.save(createdNotice).getId();
        LOGGER.info("Created notice (noticeId: (" + savedNoticeId + ")");
        return savedNoticeId;
    }

    public Page<Notice> getNotices(Pageable pageable, Long userId) {
        LOGGER.info("Getting notices");
        Page<Notice> noticePage;
        if (userId != null) {
            noticePage = noticeRepository.findAllByCreatorId(pageable, userId);
        } else {
            noticePage = noticeRepository.findAll(pageable);
        }
        LOGGER.info("Got notices");
        return noticePage;
    }
}
