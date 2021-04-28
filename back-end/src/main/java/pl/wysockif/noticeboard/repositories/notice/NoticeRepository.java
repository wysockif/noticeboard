package pl.wysockif.noticeboard.repositories.notice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wysockif.noticeboard.entities.notice.Notice;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
