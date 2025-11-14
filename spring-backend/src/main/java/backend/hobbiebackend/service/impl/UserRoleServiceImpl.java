package backend.hobbiebackend.service.impl;

import backend.hobbiebackend.handler.NotFoundException;
import backend.hobbiebackend.model.entities.UserRoleEntity;
import backend.hobbiebackend.model.entities.enums.UserRoleEnum;
import backend.hobbiebackend.model.repostiory.UserRoleRepository;
import backend.hobbiebackend.service.UserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserRoleServiceImpl implements UserRoleService {
    private final UserRoleRepository userRoleRepository;

    @Autowired
    public UserRoleServiceImpl(UserRoleRepository userRoleRepository) {
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    @Cacheable(
        value = "userRoleByEnum",
        key = "#userRoleEnum.name()",
        unless = "#result == null"
    )
    public UserRoleEntity getUserRoleByEnumName(UserRoleEnum userRoleEnum) {
        Optional<UserRoleEntity> byRole = this.userRoleRepository.findByRole(userRoleEnum);
        if (byRole.isPresent()) {
            return byRole.get();
        } else {
            throw new NotFoundException("User role not found. Please seed the roles.");
        }
    }

    @Override
    @CacheEvict(
        value = "userRoleByEnum",
        key = "#userRoleEntity.role.name()"
    )
    public UserRoleEntity saveRole(UserRoleEntity userRoleEntity) {
        return this.userRoleRepository.save(userRoleEntity);
    }
}
