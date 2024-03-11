package com.example.demo.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.common.FlashData;
import com.example.demo.entity.Chat;
import com.example.demo.entity.Event;
import com.example.demo.entity.User;
import com.example.demo.service.ChatService;
import com.example.demo.service.EventService;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/chats")
public class ChatsController {
	@Autowired
	ChatService chatService;

	@Autowired
	EventService eventService;

	@Autowired
	UserService userService;

	/*
	 * チャット画面表示
	 */
	@GetMapping(value = "/talk/{eventId}")
	public String talk(@PathVariable Integer eventId, Chat chat, Model model, RedirectAttributes ra) {
		try {
			// 存在確認
			Event event = eventService.findById(eventId);
			String email = SecurityContextHolder.getContext().getAuthentication().getName();
			User authUser = userService.findByEmail(email);
			if (!event.isParticipated(authUser)) {
				FlashData flash = new FlashData().danger("該当データがありません");
				ra.addFlashAttribute("flash", flash);
				return "redirect:/admin/events/view/{eventId}";
			}
			model.addAttribute("event", event);
			model.addAttribute("authUser", authUser);
			model.addAttribute("chat", chat);
		} catch (Exception e) {
			FlashData flash = new FlashData().danger("該当データがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/admin/events/view/{eventId}";
		}
		return "admin/chats/talk";
	}

	/*
	 * 新規登録
	 */
	@PostMapping(value = "/create")
	public String register(@Valid Chat chat, BindingResult result, Model model, RedirectAttributes ra) {
		try {
			Event event = eventService.findById(chat.getEvent().getId());
			String email = SecurityContextHolder.getContext().getAuthentication().getName();
			User authUser = userService.findByEmail(email);
			if (result.hasErrors()) {
				model.addAttribute("event", event);
				model.addAttribute("authUser", authUser);
				return "admin/chats/talk";
			}
			if (!event.isParticipated(authUser)) {
				FlashData flash = new FlashData().danger("処理中にエラーが発生しました");
				ra.addFlashAttribute("flash", flash);
				return "redirect:/admin/";
			}
			chat.setUser(authUser);
			chatService.save(chat);
		} catch (Exception e) {
			FlashData flash = new FlashData().danger("処理中にエラーが発生しました");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/admin/";
		}
		ra.addAttribute("eventId", chat.getEvent().getId());
		return "redirect:/admin/chats/talk/{eventId}";
	}
}
