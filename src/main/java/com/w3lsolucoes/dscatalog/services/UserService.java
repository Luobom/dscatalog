package com.w3lsolucoes.dscatalog.services;

import com.w3lsolucoes.dscatalog.dto.*;
import com.w3lsolucoes.dscatalog.entities.Role;
import com.w3lsolucoes.dscatalog.entities.User;
import com.w3lsolucoes.dscatalog.projections.UserDetailProjection;
import com.w3lsolucoes.dscatalog.repositories.UserRepository;
import com.w3lsolucoes.dscatalog.services.exceptions.DataBaseException;
import com.w3lsolucoes.dscatalog.services.exceptions.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Transactional(readOnly = true)
    public Page<UserDTO> findAllPaged(Pageable pageable) {
        Page<User> list = repository.findAll(pageable);
        return list.map(UserDTO::new);
    }

    @Transactional(readOnly = true)
    public UserDTO findById(Long id) {
        User entity = repository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found"));
        return new UserDTO(entity);
    }

    @Transactional
    public UserDTO save(UserInsertDTO dto) { // entrance with UserInsertDTO
        User user = new User();
        BeanUtils.copyProperties(dto, user);
        user.setPassword(passwordEncoder.encode(dto.password()));
        for (RoleDTO roleDTO : dto.roles()) {
            user.getRoles().add(new Role(roleDTO.id(), roleDTO.authority()));
        }
        return new UserDTO(repository.save(user)); // return UserDTO
    }

    @Transactional
    public UserDTO update(Long id, UserDTO dto) {
        try {
            User user = repository.getReferenceById(id);
            String[] ignoredProperties = {"id"};
            BeanUtils.copyProperties(dto, user, ignoredProperties);

            user.getRoles().clear();
            for (RoleDTO roleDTO : dto.roles()) {
                user.getRoles().add(new Role(roleDTO.id(), roleDTO.authority()));
            }

            return new UserDTO(repository.save(user));
        } catch (EntityNotFoundException | FatalBeanException e) {
            throw new ResourceNotFoundException("Id not found " + id);
        }
    }

    @Transactional
    public void delete(Long id) {
        try {
            if (!repository.existsById(id)) {
                throw new ResourceNotFoundException("User not found for the given ID: " + id);
            }
            repository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Integrity violation - This User has dependencies and cannot be deleted.");

        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("User not found for the given ID: " + id);
        }
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<UserDetailProjection> userDetailProjections = repository.searchUserAndRole(username);
        if (userDetailProjections.isEmpty()) {
            throw new UsernameNotFoundException("Email not found");
        }
        var user = new User();
        BeanUtils.copyProperties(userDetailProjections.getFirst(), user);
        user.setEmail(username);

        if (userDetailProjections.getFirst().getRoleId() != null) {
            userDetailProjections.forEach(x -> user.addRole(new Role(x.getRoleId(), x.getAuthority())));
        } else {
            System.out.println("User has no roles");
        }
        return user;
    }



}
