package uz.pdp.telegram_chat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.telegram_chat.entity.AttachmentContent;

import java.util.Optional;

public interface AttachmentContentRepo extends JpaRepository<AttachmentContent, Integer> {
    Optional<AttachmentContent> findByAttachmentId(Integer id);
}
