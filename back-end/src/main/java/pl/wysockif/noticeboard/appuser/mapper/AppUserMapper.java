package pl.wysockif.noticeboard.appuser.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import pl.wysockif.noticeboard.appuser.dto.requests.PostUserRequest;
import pl.wysockif.noticeboard.appuser.entity.AppUser;

@Mapper
public interface AppUserMapper {
    AppUserMapper INSTANCE = Mappers.getMapper( AppUserMapper.class );

    @Mapping(target = "id", ignore = true)
    AppUser postUserRequestToAppUser(PostUserRequest postUserRequest);
}
