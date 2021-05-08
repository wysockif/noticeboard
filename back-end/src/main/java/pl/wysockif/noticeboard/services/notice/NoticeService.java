package pl.wysockif.noticeboard.services.notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.wysockif.noticeboard.controllers.notice.GetNoticesRequestParams;
import pl.wysockif.noticeboard.dto.notice.requests.PostNoticeRequest;
import pl.wysockif.noticeboard.dto.notice.requests.PutNoticeRequest;
import pl.wysockif.noticeboard.dto.notice.snapshots.NoticeSnapshot;
import pl.wysockif.noticeboard.dto.notice.snapshots.NoticeWithDetailsSnapshot;
import pl.wysockif.noticeboard.entities.notice.Notice;
import pl.wysockif.noticeboard.entities.user.AppUser;
import pl.wysockif.noticeboard.errors.notice.NoPermissionException;
import pl.wysockif.noticeboard.errors.notice.NoticeNotFoundException;
import pl.wysockif.noticeboard.mappers.notice.NoticeMapper;
import pl.wysockif.noticeboard.repositories.notice.NoticeRepository;
import pl.wysockif.noticeboard.services.file.StaticFileService;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
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

    public Long postNotice(PostNoticeRequest postNoticeRequest, AppUser creator) {
        LOGGER.info("Creating notice (userId: " + creator.getId() + ")");
        Notice createdNotice = NoticeMapper.INSTANCE.postNoticeRequestToNotice(postNoticeRequest);
        createdNotice.setCreatedAt(new Date());
        createdNotice.setCreator(creator);
        saveImages(createdNotice, creator, postNoticeRequest.getPrimaryImage(),
                postNoticeRequest.getSecondaryImage(), postNoticeRequest.getTertiaryImage());
        Long savedNoticeId = noticeRepository.save(createdNotice).getId();
        LOGGER.info("Created notice (noticeId: (" + savedNoticeId + ")");
        return savedNoticeId;
    }

    public Page<NoticeSnapshot> getNotices(Pageable pageable, GetNoticesRequestParams getNoticesRequestParams) {
        LOGGER.info("Getting notices");
        Page<Notice> noticePage;
        if (getNoticesRequestParams.getUsername() != null) {
            noticePage = noticeRepository.findAllByCreatorUsername(pageable, getNoticesRequestParams.getUsername());
        } else {
            BigDecimal minPrice = new BigDecimal("0");
            BigDecimal maxPrice = new BigDecimal("100000000"); //todo: sprawdzić największą
            if (getNoticesRequestParams.getMinPrice() != null) {
                minPrice = new BigDecimal(getNoticesRequestParams.getMinPrice());
            }
            if (getNoticesRequestParams.getMaxPrice() != null) {
                maxPrice = new BigDecimal(getNoticesRequestParams.getMaxPrice());
            }
            if (getNoticesRequestParams.getLocation() != null) {
                if(getNoticesRequestParams.getSearched() != null){
                    noticePage = noticeRepository
                            .findAllByLocationIgnoreCaseAndPriceBetweenAndTitleContainingIgnoreCaseOrLocationIgnoreCaseAndPriceBetweenAndDescriptionContainingIgnoreCase(
                                    getNoticesRequestParams.getLocation(), minPrice, maxPrice,getNoticesRequestParams.getSearched(),
                                    getNoticesRequestParams.getLocation(), minPrice, maxPrice,getNoticesRequestParams.getSearched(),
                                    pageable);
                } else {
                    noticePage = noticeRepository.findAllByLocationIgnoreCaseAndPriceBetween(getNoticesRequestParams.getLocation(), minPrice, maxPrice, pageable);
                }
            } else {
                if(getNoticesRequestParams.getSearched() != null){
                    noticePage = noticeRepository
                            .findAllByPriceBetweenAndTitleContainingIgnoreCaseOrPriceBetweenAndDescriptionContainingIgnoreCase(
                                    minPrice,maxPrice,getNoticesRequestParams.getSearched(),
                                    minPrice,maxPrice,getNoticesRequestParams.getSearched(),
                                    pageable);

                } else {
                    noticePage = noticeRepository.findAllByPriceBetween(minPrice, maxPrice, pageable);
                }
            }
        }
        Page<NoticeSnapshot> noticeSnapshotPage = noticePage.map(NoticeMapper.INSTANCE::noticeToNoticeSnapshot);
        LOGGER.info("Got notices");
        return noticeSnapshotPage;
    }

    public NoticeWithDetailsSnapshot getNotice(Long noticeId) {
        LOGGER.info("Getting notice (noticeId: " + noticeId + ")");
        Optional<Notice> foundNotice = noticeRepository.findById(noticeId);
        if (foundNotice.isEmpty()) {
            LOGGER.info("Notice not found (noticeId: " + noticeId + ")");
            throw new NoticeNotFoundException("Nie znaleziono ogłoszenia o id: " + noticeId);
        }
        NoticeWithDetailsSnapshot noticeWithDetailsSnapshot = NoticeMapper.INSTANCE
                .noticeToNoticeWithDetailsSnapshot(foundNotice.get());
        LOGGER.info("Got notice (noticeId: " + noticeId + ")");
        return noticeWithDetailsSnapshot;
    }

    public void deleteNotice(Long noticeId, AppUser appUser) {
        LOGGER.info("Deleting notice (noticeId: " + noticeId + ")");
        checkIfNoticeExists(noticeId);
        Notice noticeToDelete = noticeRepository.getOne(noticeId);
        if (!noticeToDelete.getCreator().getId().equals(appUser.getId())) {
            LOGGER.info("Deleting notice (not by owner) not allowed (noticeId: " + noticeId + ")");
            throw new NoPermissionException("Brak uprawnień do usunięcia ogłoszenia");
        }
        staticFileService.deleteNoticeImage(noticeId.toString(), noticeToDelete.getPrimaryImage());
        staticFileService.deleteNoticeImage(noticeId.toString(), noticeToDelete.getSecondaryImage());
        staticFileService.deleteNoticeImage(noticeId.toString(), noticeToDelete.getTertiaryImage());
        noticeRepository.deleteById(noticeId);
        LOGGER.info("Deleted notice (noticeId: " + noticeId + ")");
    }

    public void putNotice(Long noticeId, PutNoticeRequest putNoticeRequest, AppUser creator) {
        LOGGER.info("Updating notice (noticeId: " + noticeId + ")");
        checkIfNoticeExists(noticeId);
        Notice noticeBeforeUpdate = noticeRepository.getOne(noticeId);
        if (!noticeBeforeUpdate.getCreator().getId().equals(creator.getId())) {
            LOGGER.info("Updating notice (not by owner) not allowed (noticeId: " + noticeId + ")");
            throw new NoPermissionException("Brak uprawnień do edycji ogłoszenia");
        }
        Notice noticeAfterUpdate = NoticeMapper.INSTANCE.putNoticeRequestToNotice(putNoticeRequest);
        updateImages(noticeBeforeUpdate, noticeAfterUpdate, creator);
        noticeAfterUpdate.setId(noticeId);
        noticeAfterUpdate.setCreator(creator);
        noticeAfterUpdate.setCreatedAt(noticeBeforeUpdate.getCreatedAt());
        noticeRepository.save(noticeAfterUpdate);
        LOGGER.info("Updated (noticeId: " + noticeId + ")");
    }

    private void updateImages(Notice noticeBeforeUpdate, Notice noticeAfterUpdate, AppUser creator) {
        updatePrimaryImage(noticeBeforeUpdate, noticeAfterUpdate, creator);
        updateSecondaryImage(noticeBeforeUpdate, noticeAfterUpdate, creator);
        updateTertiaryImage(noticeBeforeUpdate, noticeAfterUpdate, creator);
    }


    private void updatePrimaryImage(Notice noticeBeforeUpdate, Notice noticeAfterUpdate, AppUser creator) {
        if (noticeAfterUpdate.getPrimaryImage() != null) {
            String oldImageName = noticeBeforeUpdate.getPrimaryImage();
            noticeAfterUpdate.setPrimaryImage(staticFileService.saveNoticeImage(creator.getId().toString(), noticeAfterUpdate.getPrimaryImage()));
            staticFileService.deleteNoticeImage(noticeBeforeUpdate.getId().toString(), oldImageName);
        } else {
            noticeAfterUpdate.setPrimaryImage(noticeBeforeUpdate.getPrimaryImage());
        }
    }

    private void checkIfNoticeExists(Long noticeId) {
        if (!noticeRepository.existsById(noticeId)) {
            LOGGER.info("Notice not found (noticeId: " + noticeId + ")");
            throw new NoticeNotFoundException("Nie znaleziono ogłoszenia");
        }
    }

    private void updateSecondaryImage(Notice noticeBeforeUpdate, Notice noticeAfterUpdate, AppUser creator) {
        if (noticeAfterUpdate.getSecondaryImage() != null) {
            String oldImageName = noticeBeforeUpdate.getSecondaryImage();
            noticeAfterUpdate.setSecondaryImage(staticFileService.saveNoticeImage(creator.getId().toString(), noticeAfterUpdate.getSecondaryImage()));
            staticFileService.deleteNoticeImage(noticeBeforeUpdate.getId().toString(), oldImageName);
        } else {
            noticeAfterUpdate.setSecondaryImage(noticeBeforeUpdate.getSecondaryImage());
        }
    }

    private void prepareDetailsInNoticeBeforeSaving(Notice notice, AppUser creator) {
        notice.setCreatedAt(new Date());
        notice.setCreator(creator);
    }

    private void updateTertiaryImage(Notice noticeBeforeUpdate, Notice noticeAfterUpdate, AppUser creator) {
        if (noticeAfterUpdate.getTertiaryImage() != null) {
            String oldImageName = noticeBeforeUpdate.getTertiaryImage();
            noticeAfterUpdate.setTertiaryImage(staticFileService.saveNoticeImage(creator.getId().toString(), noticeAfterUpdate.getTertiaryImage()));
            staticFileService.deleteNoticeImage(noticeBeforeUpdate.getId().toString(), oldImageName);
        } else {
            noticeAfterUpdate.setTertiaryImage(noticeBeforeUpdate.getTertiaryImage());
        }
    }

    private void saveImages(Notice notice, AppUser creator, String primaryImage, String secondaryImage, String tertiaryImage) {
        notice.setPrimaryImage(staticFileService.saveNoticeImage(creator.getId().toString(), primaryImage));
        notice.setSecondaryImage(staticFileService.saveNoticeImage(creator.getId().toString(), secondaryImage));
        notice.setTertiaryImage(staticFileService.saveNoticeImage(creator.getId().toString(), tertiaryImage));
    }
}
