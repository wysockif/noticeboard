package pl.wysockif.noticeboard.controllers.notice;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.wysockif.noticeboard.dto.notice.requests.PostNoticeRequest;
import pl.wysockif.noticeboard.services.notice.NoticeService;

import javax.validation.Valid;
import java.util.logging.Logger;

@RestController
@RequestMapping(("/api/1.0"))
public class NoticeController {

    private static final Logger LOGGER = Logger.getLogger(NoticeController.class.getName());

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @PostMapping("/notices")
    public Long createNotice(@Valid @RequestBody PostNoticeRequest postNoticeRequest) {
        LOGGER.info("Request postNotice started");
        Long savedNoticeId = noticeService.save(postNoticeRequest);
        LOGGER.info("Request postNotice finished");
        return savedNoticeId;
    }
}
