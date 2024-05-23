package com.haminhtrung.backend.service.impl;

import lombok.AllArgsConstructor;
import lombok.experimental.NonFinal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.haminhtrung.backend.repository.UserRepository;
import com.haminhtrung.backend.dto.UserDto;
import com.haminhtrung.backend.entity.User;
import com.haminhtrung.backend.service.UserService;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;
import java.util.logging.Level;
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private static final Logger log = Logger.getLogger(UserServiceImpl.class.getName());
    @NonFinal
    protected static final String SiGNER_KEY = 
        "IQ8SMYaokz+WF9kVhs+AYr1MxM6YliKLvFR0nYV57221Gs9x+LuFzyicJfFvj76A"; 
                        
    @Override
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.get();
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId()).get();
        existingUser.setFullname(user.getFullname());
        existingUser.setEmail(user.getEmail());
        existingUser.setPhone_number(user.getPhone_number());
        existingUser.setAddress(user.getAddress());
        existingUser.setPassword(user.getPassword());
        existingUser.setCreated_at(user.getCreated_at());
        existingUser.setUpdated_at(user.getUpdated_at());
        // existingUser.setTokens(user.getTokens());
        // existingUser.setRole(user.getRole());

        User updateUser = userRepository.save(existingUser);
        return updateUser;
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User registerUser(UserDto userDto) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        String encodedPassword = passwordEncoder.encode(userDto.getPassword());
        
        User newUser = new User(userDto.getFullname(), userDto.getEmail(), userDto.getPhone_number(),
                userDto.getAddress(), encodedPassword);
        
        return userRepository.save(newUser);
    }
    

    @Override
public UserDto loginUser(UserDto userDto) {
    User userInDb = userRepository.findByFullname(userDto.getFullname());
    if (userInDb != null) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (passwordEncoder.matches(userDto.getPassword(), userInDb.getPassword())) {
            // Tạo mã JWT token
            String token = generateToken(userDto.getFullname());

            // Tạo một đối tượng UserDto mới với mã token đã được thêm vào
            UserDto loggedInUserDto = new UserDto(
                userInDb.getId(), 
                userInDb.getFullname(), 
                userInDb.getEmail(),
                userInDb.getPhone_number(), 
                userInDb.getAddress(), 
                userInDb.getPassword(),
                token
            );

            // Trả về đối tượng UserDto đã được cập nhật với mã token
            return loggedInUserDto;
        }
    }
    // Trả về null hoặc một giá trị khác để biểu thị không có người dùng nào được đăng nhập
    return null;
}


    // hàm handle token
     private String generateToken(String fullname) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(fullname)
                .issuer("haminhtrung.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("customClaim", "Custom")
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SiGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.log(Level.SEVERE, "Cannot create token", e);
            throw new RuntimeException(e);
        }
    }
    

}