package pl.wysockif.noticeboard.services.notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wysockif.noticeboard.params.GettingNoticesParams;
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
import java.util.LinkedList;
import java.util.List;
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

    @Transactional
    public Long postNotice(PostNoticeRequest postNoticeRequest, AppUser creator) {
        LOGGER.info("Creating notice (userId: " + creator.getId() + ")");
        Notice createdNotice = NoticeMapper.INSTANCE.postNoticeRequestToNotice(postNoticeRequest);
        createdNotice.setCreatedAt(new Date());
        createdNotice.setCreator(creator);
        String primaryImage = createdNotice.getPrimaryImage();
        createdNotice.setPrimaryImage(null);
        String secondaryImage = createdNotice.getSecondaryImage();
        createdNotice.setSecondaryImage(null);
        String tertiaryImage = createdNotice.getTertiaryImage();
        createdNotice.setTertiaryImage(null);
        Notice noticeToSave = noticeRepository.save(createdNotice);
        saveImages(noticeToSave, noticeToSave.getId().toString(), primaryImage, secondaryImage, tertiaryImage);
        Long savedNoticeId = noticeRepository.save(noticeToSave).getId();
        LOGGER.info("Created notice (noticeId: (" + savedNoticeId + ")");
        return savedNoticeId;
    }

    @Transactional
    public void deleteAllByUserId(Long userId) {
        LOGGER.info("Deleting all notices by user id (userId: " + userId + ")");
        List<Notice> notices = noticeRepository.findAllByCreatorId(userId);
        List<String> imagesToDelete = new LinkedList<>();
        notices.forEach(noticeToDelete -> {
            imagesToDelete.add(noticeToDelete.getPrimaryImage());
            imagesToDelete.add(noticeToDelete.getSecondaryImage());
            imagesToDelete.add(noticeToDelete.getTertiaryImage());
        });
        noticeRepository.deleteAllByCreatorId(userId);
        imagesToDelete.forEach(imageName -> staticFileService.deleteNoticeImage(userId.toString(), imageName));
        LOGGER.info("Deleted all notices by user id (userId: " + userId + ")");
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

    public Page<NoticeSnapshot> getNotices(Pageable pageable, GettingNoticesParams gettingNoticesParams) {
        LOGGER.info("Getting notices");
        Page<Notice> noticePage;
        noticePage = getNoticePage(pageable, gettingNoticesParams);
        Page<NoticeSnapshot> noticeSnapshotPage = noticePage.map(NoticeMapper.INSTANCE::noticeToNoticeSnapshot);
        LOGGER.info("Got notices");
        return noticeSnapshotPage;
    }


    private Page<Notice> getNoticePage(Pageable pageable, GettingNoticesParams gettingNoticesParams) {
        String username = gettingNoticesParams.getUsername();
        String minPriceParam = gettingNoticesParams.getMinPrice();
        String maxPriceParam = gettingNoticesParams.getMaxPrice();
        String searched = gettingNoticesParams.getSearched();
        String location = gettingNoticesParams.getLocation();
        Page<Notice> noticePage;
        if (username != null) {
            noticePage = noticeRepository.findAllByCreatorUsername(pageable, username);
        } else {
            BigDecimal minPriceValue = minPriceParam == null ? new BigDecimal("0") : new BigDecimal(minPriceParam);
            BigDecimal maxPriceValue = maxPriceParam == null ? new BigDecimal("100000000") : new BigDecimal(maxPriceParam); //todo: sprawdzić największą
            noticePage = getNoticeFilteredPage(pageable, searched, location, minPriceValue, maxPriceValue);
        }
        return noticePage;
    }

    private Page<Notice> getNoticeFilteredPage(Pageable pageable, String searched, String location, BigDecimal minPriceValue, BigDecimal maxPriceValue) {
        Page<Notice> noticePage;
        if (location != null) {
            noticePage = getNoticePageWhenLocationIsProvided(pageable, searched, location, minPriceValue, maxPriceValue);
        } else {
            noticePage = getNoticePageWhenLocationIsNotProvided(pageable, searched, minPriceValue, maxPriceValue);
        }
        return noticePage;
    }

    private Page<Notice> getNoticePageWhenLocationIsNotProvided(Pageable pageable, String searched,
                                                                BigDecimal minPriceValue, BigDecimal maxPriceValue) {
        Page<Notice> noticePage;
        if (searched != null) {
            noticePage = noticeRepository
                    .findAllByPriceBetweenAndTitleContainingIgnoreCaseOrPriceBetweenAndDescriptionContainingIgnoreCase(
                            minPriceValue, maxPriceValue, searched,
                            minPriceValue, maxPriceValue, searched,
                            pageable);
        } else {
            noticePage = noticeRepository.findAllByPriceBetween(minPriceValue, maxPriceValue, pageable);
        }
        return noticePage;
    }

    private Page<Notice> getNoticePageWhenLocationIsProvided(Pageable pageable, String searched, String location,
                                                             BigDecimal minPriceValue, BigDecimal maxPriceValue) {
        Page<Notice> noticePage;
        if (searched != null) {
            noticePage = getNoticePageWhenIsSearchedInLocation(pageable, searched, location, minPriceValue, maxPriceValue);
        } else {
            noticePage = noticeRepository
                    .findAllByLocationContainingIgnoreCaseAndPriceBetween(location, minPriceValue, maxPriceValue, pageable);
        }
        return noticePage;
    }


    private Page<Notice> getNoticePageWhenIsSearchedInLocation(
            Pageable pageable, String searched, String location, BigDecimal minPriceValue, BigDecimal maxPriceValue) {
        Page<Notice> noticePage;
        noticePage = noticeRepository
                .findAllByLocationContainingIgnoreCaseAndPriceBetweenAndTitleContainingIgnoreCaseOrLocationContainingIgnoreCaseAndPriceBetweenAndDescriptionContainingIgnoreCase(
                        location, minPriceValue, maxPriceValue, searched,
                        location, minPriceValue, maxPriceValue, searched,
                        pageable);
        return noticePage;
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

    private void updateTertiaryImage(Notice noticeBeforeUpdate, Notice noticeAfterUpdate, AppUser creator) {
        if (noticeAfterUpdate.getTertiaryImage() != null) {
            String oldImageName = noticeBeforeUpdate.getTertiaryImage();
            noticeAfterUpdate.setTertiaryImage(staticFileService.saveNoticeImage(creator.getId().toString(), noticeAfterUpdate.getTertiaryImage()));
            staticFileService.deleteNoticeImage(noticeBeforeUpdate.getId().toString(), oldImageName);
        } else {
            noticeAfterUpdate.setTertiaryImage(noticeBeforeUpdate.getTertiaryImage());
        }
    }

    private void saveImages(Notice notice, String noticeId, String primaryImage, String secondaryImage, String tertiaryImage) {
        notice.setPrimaryImage(staticFileService.saveNoticeImage(noticeId, primaryImage));
        notice.setSecondaryImage(staticFileService.saveNoticeImage(noticeId, secondaryImage));
        notice.setTertiaryImage(staticFileService.saveNoticeImage(noticeId, tertiaryImage));
    }
}
