package uz.pdp.telegram_chat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.pdp.telegram_chat.entity.Messages;
import uz.pdp.telegram_chat.entity.User;

import java.util.List;

public interface MessageRepo extends JpaRepository<Messages, Integer> {
    @Query("""
        select m from Messages m where\s
                    (m.frooom = :currentUser and m.tooo = :currentChatUser) or
                    (m.frooom = :currentChatUser and m.tooo= :currentUser)
                    order by  m.sentMessage asc\s
""")
    List<Messages> findChatHistory(@Param("currentUser") User currentUser,
                                   @Param("currentChatUser") User currentChatUser);

    List<Messages> findByFrooomAndToooOrToooAndFrooom(User currentUser, User currentChatUser, User currentChatUser1, User currentUser1);

}
