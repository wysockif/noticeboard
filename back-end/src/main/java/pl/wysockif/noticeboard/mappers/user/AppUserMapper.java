package pl.wysockif.noticeboard.mappers.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.wysockif.noticeboard.dto.user.requests.PostUserRequest;
import pl.wysockif.noticeboard.dto.user.snapshots.AppUserSnapshot;
import pl.wysockif.noticeboard.entities.user.AppUser;

@Mapper
public interface AppUserMapper {
    AppUserMapper INSTANCE = Mappers.getMapper(AppUserMapper.class);

    @Mapping(target = "id", ignore = true)
    AppUser postUserRequestToAppUser(PostUserRequest postUserRequest);

    AppUserSnapshot appUserToSnapshot(AppUser appUser);
}
