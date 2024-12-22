package uz.pdp.telegram_chat.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import uz.pdp.telegram_chat.entity.Attachment;
import uz.pdp.telegram_chat.entity.AttachmentContent;
import uz.pdp.telegram_chat.entity.Messages;
import uz.pdp.telegram_chat.entity.User;
import uz.pdp.telegram_chat.repo.AttachmentContentRepo;
import uz.pdp.telegram_chat.repo.AttachmentRepo;
import uz.pdp.telegram_chat.repo.MessageRepo;
import uz.pdp.telegram_chat.repo.UsersRepo;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@SessionAttributes({"currentUser", "currentChatUser"})
public class ChatController {

    @Autowired
    private UsersRepo userRepo;

    @Autowired
    private MessageRepo messageRepo;
    @Autowired
    private AttachmentContentRepo attachmentContentRepo;
    @Autowired
    private AttachmentRepo attachmentRepo;
    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") Integer password,
                        Model model) {
        User user = userRepo.findByUsernameAndPassword(username, password);
        if (user == null) {
            model.addAttribute("error", "Invalid username or password");
            return "login";
        }
        model.addAttribute("currentUser", user);
        return "redirect:/chat";
    }
    @GetMapping("/chat")
    public String chatPage(@SessionAttribute(name = "currentUser", required = false) User currentUser, Model model) {
        if (currentUser == null) return "redirect:/login";
        List<User> otherUsers = userRepo.findAll()
                .stream()
                .filter(user -> !user.getId().equals(currentUser.getId()))
                .toList();
        model.addAttribute("users", otherUsers);
        return "chat";
    }
    @GetMapping("/chat/{id}")
    public String selectChatUser(@PathVariable Integer id,
                                 @SessionAttribute("currentUser") User currentUser,
                                 Model model) {
        User currentChatUser = userRepo.findById(id).orElse(null);
        if (currentChatUser == null) return "redirect:/chat";

        model.addAttribute("currentChatUser", currentChatUser);

        List<Messages> messages = messageRepo.findChatHistory(currentUser, currentChatUser);
        model.addAttribute("messages", messages);
        return "chat";
    }
    @PostMapping("/sendMessage")
    public String sendMessage(@RequestParam("text") String text,
                              @RequestParam(value = "file", required = false) MultipartFile file,
                              @SessionAttribute("currentUser") User currentUser,
                              @SessionAttribute("currentChatUser") User currentChatUser) throws IOException {
        if (currentChatUser == null) {
            return "redirect:/chat";
        }

        Messages message = new Messages();
        message.setFrooom(currentUser);
        message.setTooo(currentChatUser);
        message.setText(text);
        message.setSentMessage(LocalDateTime.now());

        if (file != null && !file.isEmpty()) {
            Attachment attachment = new Attachment();
            attachment.setFilename(file.getOriginalFilename());
            attachment.setContentType(file.getContentType());
            attachmentRepo.save(attachment);

            AttachmentContent attachmentContent = new AttachmentContent();
            attachmentContent.setFileData(file.getBytes());
            attachmentContent.setAttachment(attachment);
            attachmentContentRepo.save(attachmentContent);

            message.setAttachment(attachment);
        }

        messageRepo.save(message);

        return "redirect:/chat/" + currentChatUser.getId();
    }

    @GetMapping("/download/{attachmentId}")
    @ResponseBody
    public ResponseEntity<byte[]> downloadFile(@PathVariable Integer attachmentId) {
        Optional<AttachmentContent> attachmentContentOpt = attachmentContentRepo.findByAttachmentId(attachmentId);

        if (attachmentContentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AttachmentContent attachmentContent = attachmentContentOpt.get();
        byte[] fileData = attachmentContent.getFileData();
        String filename = attachmentContent.getAttachment().getFilename(); // Get the filename

        String contentType = attachmentContent.getAttachment().getContentType();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.parseMediaType(contentType)) // Set content type dynamically
                .body(fileData);
    }

    @GetMapping("/logout")
    public String logout(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "redirect:/login";
    }
}
