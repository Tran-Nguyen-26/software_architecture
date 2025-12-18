package backend.hobbiebackend.service.impl;

import backend.hobbiebackend.handler.NotFoundException;
import backend.hobbiebackend.model.dto.AppClientSignUpDto;
import backend.hobbiebackend.model.dto.BusinessRegisterDto;
import backend.hobbiebackend.model.dto.UpdateAppClientDto;
import backend.hobbiebackend.model.dto.UpdateBusinessDto;
import backend.hobbiebackend.model.entities.*;
import backend.hobbiebackend.model.entities.enums.GenderEnum;
import backend.hobbiebackend.model.entities.enums.UserRoleEnum;
import backend.hobbiebackend.model.repostiory.AppClientRepository;
import backend.hobbiebackend.model.repostiory.BusinessOwnerRepository;
import backend.hobbiebackend.model.repostiory.UserRepository;
import backend.hobbiebackend.service.UserRoleService;
import backend.hobbiebackend.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final AppClientRepository appClientRepository;
    private final BusinessOwnerRepository businessOwnerRepository;
    private final UserRoleService userRoleService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository,
                           AppClientRepository appClientRepository,
                           BusinessOwnerRepository businessOwnerRepository, UserRoleService userRoleService, PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.appClientRepository = appClientRepository;
        this.businessOwnerRepository = businessOwnerRepository;
        this.userRoleService = userRoleService;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public List<UserEntity> seedUsersAndUserRoles() {
        List<UserEntity> seededUsers = new ArrayList<>();
        //simple user
        if (appClientRepository.count() == 0) {
            UserRoleEntity userRoleEntity = new UserRoleEntity();
            userRoleEntity.setRole(UserRoleEnum.USER);
            UserRoleEntity userRole = this.userRoleService.saveRole(userRoleEntity);
            UserRoleEntity userRoleEntity2 = new UserRoleEntity();
            userRoleEntity2.setRole(UserRoleEnum.ADMIN);
            UserRoleEntity adminRole = this.userRoleService.saveRole(userRoleEntity2);
            AppClient user = new AppClient();
            user.setUsername("user");
            user.setEmail("n13@gmail.com");
            user.setPassword(this.passwordEncoder.encode("topsecret"));
            user.setRoles(List.of(userRole));
            user.setFullName("Nikoleta Doykova");
            user.setGender(GenderEnum.FEMALE);

            appClientRepository.save(user);
            seededUsers.add(user);


        }
        if (businessOwnerRepository.count() == 0) {
            UserRoleEntity userRoleEntity3 = new UserRoleEntity();
            userRoleEntity3.setRole(UserRoleEnum.BUSINESS_USER);
            UserRoleEntity businessRole = this.userRoleService.saveRole(userRoleEntity3);
            //business_user
            BusinessOwner business_user = new BusinessOwner();
            business_user.setUsername("business");
            business_user.setEmail("n10@gamil.com");
            business_user.setPassword(this.passwordEncoder.encode("topsecret"));
            business_user.setRoles(List.of(businessRole));
            business_user.setBusinessName("My Business name");
            business_user.setAddress("My business address");
            businessOwnerRepository.save(business_user);
            seededUsers.add(business_user);
        }
        return seededUsers;
    }

    @Override
    public AppClient register(AppClientSignUpDto user) {
        if (this.userExists(user.getUsername(), user.getEmail())) {
            throw new RuntimeException("Username or email address already in use.");
        }
        UserRoleEntity userRole = this.userRoleService.getUserRoleByEnumName(UserRoleEnum.USER);
        AppClient appClient = this.modelMapper.map(user, AppClient.class);
        appClient.setRoles(List.of(userRole));
        appClient.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return appClientRepository.save(appClient);
    }

    @Override
    public BusinessOwner registerBusiness(BusinessRegisterDto business) {
        if (this.businessExists(business.getBusinessName()) || this.userExists(business.getUsername(), business.getEmail())) {
            throw new RuntimeException("Username or email address already in use.");
        }
        UserRoleEntity businessUserRole = this.userRoleService.getUserRoleByEnumName(UserRoleEnum.BUSINESS_USER);
        BusinessOwner businessOwner = this.modelMapper.map(business, BusinessOwner.class);
        businessOwner.setRoles(List.of(businessUserRole));
        businessOwner.setPassword(this.passwordEncoder.encode(business.getPassword()));
        return businessOwnerRepository.save(businessOwner);
    }

    @Override
    public BusinessOwner saveUpdatedUser(UpdateBusinessDto business) {
        BusinessOwner businessOwner = this.findBusinessOwnerById(business.getId());
        if (this.businessExists(business.getBusinessName()) && (!businessOwner.getBusinessName().equals(business.getBusinessName()))) {
            throw new RuntimeException("Business name already in use.");
        }
        businessOwner.setBusinessName(business.getBusinessName());
        businessOwner.setPassword(this.passwordEncoder.encode(business.getPassword()));
        businessOwner.setAddress(business.getAddress());
        return this.businessOwnerRepository.save(businessOwner);
    }

    @Override
    public BusinessOwner updateBusiness(BusinessOwner businessOwner) {
        return businessOwnerRepository.save(businessOwner);
    }

    @Override
    @CacheEvict(value = "appClients", key = "'ID:' + #appClient.id")
    public AppClient saveUpdatedUserClient(AppClient appClient) {
        return this.appClientRepository.save(appClient);
    }

    @Override
    public AppClient updateUserClient(UpdateAppClientDto user) {
        AppClient client = this.findAppClientById(user.getId());
        client.setPassword(passwordEncoder.encode(user.getPassword()));
        client.setGender(user.getGender());
        client.setFullName(user.getFullName());
        return saveUpdatedUserClient(client);
    }

    @Override
    @Cacheable(value = "users", key = "'ID:' + #userId")
    public UserEntity findUserById(Long userId) {
        Optional<UserEntity> byId = this.userRepository.findById(userId);
        if (byId.isPresent()) {
            return byId.get();
        } else {
            throw new NotFoundException("User not found");
        }
    }

    @Override
    @Cacheable(value = "users", key = "'EMAIL' + #email")
    public UserEntity findUserByEmail(String email) {
        Optional<UserEntity> byEmail = this.userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            return byEmail.get();
        } else {
            return null;
        }
    }

    @Override
    public BusinessOwner findBusinessOwnerById(Long id) {
        Optional<BusinessOwner> businessOwner = this.businessOwnerRepository.findById(id);
        if (businessOwner.isPresent()) {
            return businessOwner.get();
        } else {
            throw new NotFoundException("Can not find business owner");
        }
    }

    @Override
    @Cacheable(value = "users", key = "'USERNAME:' + #username")
    public UserEntity findUserByUsername(String username) {
        Optional<UserEntity> byUsername = this.userRepository.findByUsername(username);
        if (byUsername.isPresent()) {
            return byUsername.get();
        } else {
            throw new NotFoundException("Can not find user with this username");
        }
    }

    @Override
    public boolean userExists(String username, String email) {
        Optional<UserEntity> byUsername = this.userRepository.findByUsername(username);
        Optional<UserEntity> byEmail = this.userRepository.findByEmail(email);
        return byUsername.isPresent() || byEmail.isPresent();
    }

    @Override
    public UserEntity saveUserWithUpdatedPassword(Long id, String password) {
        UserEntity userById = this.findUserById(id);
        userById.setPassword(passwordEncoder.encode(password));
        return this.userRepository.save(userById);
    }

    @Override
    public boolean deleteUser(Long id) {
        UserEntity user = findUserById(id);
        if (user == null) {
            return false;
        }
        Optional<BusinessOwner> byId = this.businessOwnerRepository.findById(user.getId());

        if (byId.isPresent()) {
            List<AppClient> all = appClientRepository.findAll();
            for (AppClient client : all) {
                for (Hobby hobby : byId.get().getHobby_offers()) {
                    client.getHobby_matches().remove(hobby);
                    client.getSaved_hobbies().remove(hobby);
                }
                this.userRepository.save(client);
            }
        }
        userRepository.delete(user);
        return true;
    }


    @Override
    @Cacheable(value = "appClients", key = "'ID:' + #clientId")
    public AppClient findAppClientById(Long clientId) {
        Optional<AppClient> user = this.appClientRepository.findById(clientId);
        if (user.isPresent()) {

            return user.get();
        } else {
            throw new NotFoundException("Can not find current user.");
        }
    }

    @Override
    public void findAndRemoveHobbyFromClientsRecords(Hobby hobby) {
        List<AppClient> all = this.appClientRepository.findAll();

        for (AppClient appClient : all) {
            appClient.getSaved_hobbies().remove(hobby);
            appClient.getHobby_matches().remove(hobby);
        }
    }


    @Override
    public boolean businessExists(String businessName) {
        Optional<BusinessOwner> byBusinessName = this.businessOwnerRepository.findByBusinessName(businessName);
        return byBusinessName.isPresent();
    }

    @Override
    @Cacheable(value = "appClients", key = "'USERNAME:' + #username")
    public AppClient findAppClientByUsername(String username) {
        return this.appClientRepository.findByUsername(username).orElseThrow();
    }

    @Override
    @Cacheable(value = "businessOwers", key = "'USERNAME:' + #username")
    public BusinessOwner findBusinessByUsername(String username) {
        return this.businessOwnerRepository.findByUsername(username).get();
    }
}
