package pl.wysockif.noticeboard.repositories.notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wysockif.noticeboard.entities.notice.Notice;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

    Page<Notice> findAllByCreatorUsername(Pageable pageable, String username);

    Page<Notice> findAllByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Notice> findAllByLocationContainingIgnoreCaseAndPriceBetween(String location, BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    Page<Notice> findAllByPriceBetweenAndTitleContainingIgnoreCaseOrPriceBetweenAndDescriptionContainingIgnoreCase(
            BigDecimal minPrice1, BigDecimal maxPrice1, String searched1,
            BigDecimal minPrice2, BigDecimal maxPrice2, String searched2,
            Pageable pageable);

    Page<Notice> findAllByLocationContainingIgnoreCaseAndPriceBetweenAndTitleContainingIgnoreCaseOrLocationContainingIgnoreCaseAndPriceBetweenAndDescriptionContainingIgnoreCase(
            String location1, BigDecimal minPrice1, BigDecimal maxPrice1, String searched1,
            String location2, BigDecimal minPrice2, BigDecimal maxPrice2, String searched2,
            Pageable pageable);

    List<Notice> findAllByCreatorId(Long creatorId);

    void deleteAllByCreatorId(Long creatorId);
}
