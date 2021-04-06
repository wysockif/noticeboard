package pl.wysockif.noticeboard.appuser.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wysockif.noticeboard.appuser.entity.AppUser;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long> {
}
