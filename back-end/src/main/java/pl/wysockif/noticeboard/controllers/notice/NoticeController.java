package pl.wysockif.noticeboard.controllers.notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pl.wysockif.noticeboard.dto.notice.requests.PostNoticeRequest;
import pl.wysockif.noticeboard.dto.notice.snapshots.NoticeSnapshot;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.mappers.notice.NoticeMapper;
import pl.wysockif.noticeboard.services.notice.NoticeService;

import javax.validation.Valid;
import java.util.logging.Logger;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(("/api/1.0"))
public class NoticeController {

    private static final Logger LOGGER = Logger.getLogger(NoticeController.class.getName());

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping("/notices")
    @ResponseStatus(CREATED)
    public Long createNotice(@Valid @RequestBody PostNoticeRequest postNoticeRequest) {
        LOGGER.info("Request postNotice started");
        AppUser loggedInUser = (AppUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Long savedNoticeId = noticeService.save(postNoticeRequest, loggedInUser);
        LOGGER.info("Request postNotice finished");
        return savedNoticeId;
    }

    @GetMapping("/notices")
    public Page<NoticeSnapshot> getNotices(Pageable pageable) {
        LOGGER.info("Request getNotices started");
        Page<NoticeSnapshot> page = noticeService.getNotices(pageable)
                .map(NoticeMapper.INSTANCE::noticeToNoticeSnapshot);
        LOGGER.info("Request getNotices finished");
        return page;
    }
}
