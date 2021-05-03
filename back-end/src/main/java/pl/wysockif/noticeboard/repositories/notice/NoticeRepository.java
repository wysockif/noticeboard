package pl.wysockif.noticeboard.repositories.notice;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wysockif.noticeboard.entities.notice.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findAllByCreatorId(Pageable pageable, Long userId);
}
