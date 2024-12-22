package uz.pdp.telegram_chat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.telegram_chat.entity.User;

import java.util.List;

public interface UsersRepo extends JpaRepository<User, Integer> {
    User findByUsernameAndPassword(String username, Integer password);
    List<User> findAll();
}
