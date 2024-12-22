package uz.pdp.telegram_chat.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.pdp.telegram_chat.entity.Attachment;

public interface AttachmentRepo extends JpaRepository<Attachment, Integer> {

}
