package com.example.autonomize_assignment;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final RestTemplate restTemplate;

    public User saveOrUpdateUser(String username) {
        return userRepository.findByUsername(username).orElseGet(() -> {
            String url = "https://api.github.com/users/" + username;
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            Map userData = response.getBody();

            User user = new User();
            user.setUsername(username);
            user.setName((String) userData.get("name"));
            user.setAvatarUrl((String) userData.get("avatar_url"));
            user.setLocation((String) userData.get("location"));
            user.setBlog((String) userData.get("blog"));
            user.setBio((String) userData.get("bio"));
            user.setPublicRepos((Integer) userData.get("public_repos"));
            user.setPublicGists((Integer) userData.get("public_gists"));
            user.setFollowers((Integer) userData.get("followers"));
            user.setFollowing((Integer) userData.get("following"));
            user.setCreatedAt(LocalDateTime.parse((String) userData.get("created_at")));
            user.setUpdatedAt(LocalDateTime.now());

            return userRepository.save(user);
        });
    }

    public List<User> getFriends(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));

        // Logic for finding mutual followers using GitHub API (simplified for demo)
        // Add mutual users to the friends set and save them

        return new ArrayList<>(user.getFriends());
    }

    public List<User> searchUsers(String keyword) {
        return userRepository.searchByKeyword(keyword);
    }

    public void softDeleteUser(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        user.setSoftDeleted(true);
        userRepository.save(user);
    }

    public void updateUser(String username, Map<String, Object> updates) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        updates.forEach((key, value) -> {
            switch (key) {
                case "location": user.setLocation((String) value); break;
                case "blog": user.setBlog((String) value); break;
                case "bio": user.setBio((String) value); break;
                default: throw new IllegalArgumentException("Invalid field");
            }
        });
        userRepository.save(user);
    }

    public List<User> getAllUsersSorted(String field) {
        return userRepository.findBySoftDeletedFalse().stream()
                .sorted(Comparator.comparing(user -> {
                    switch (field) {
                        case "public_repos": return user.getPublicRepos();
                        case "followers": return user.getFollowers();
                        case "following": return user.getFollowing();
                        default: throw new IllegalArgumentException("Invalid field");
                    }
                }))
                .collect(Collectors.toList());
    }
}
