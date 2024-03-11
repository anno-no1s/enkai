package com.example.demo.web.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.common.DataNotFoundException;
import com.example.demo.common.FlashData;
import com.example.demo.entity.Event;
import com.example.demo.entity.EventUser;
import com.example.demo.entity.User;
import com.example.demo.service.EventService;
import com.example.demo.service.EventUserService;
import com.example.demo.service.UserService;

@Controller
@RequestMapping("/admin/eventusers")
public class EventUsersController {
	@Autowired
	EventUserService eventUserService;

	@Autowired
	EventService eventService;

	@Autowired
	UserService userService;

	/*
	 * 新規登録
	 */
	@GetMapping(value = "/create/{eventId}")
	public String register(EventUser eventUser, @PathVariable Integer eventId, RedirectAttributes ra) {
		FlashData flash;
		try {
			Event event = eventService.findById(eventId);
			String email = SecurityContextHolder.getContext().getAuthentication().getName();
			User authUser = userService.findByEmail(email);
			if (event.isLimited()) {
				flash = new FlashData().danger("最大参加者数に到達しているため参加できません");
			} else if (event.isParticipated(authUser)) {
				flash = new FlashData().danger("既にイベントに参加済みです");
			} else {
				// 新規登録
				eventUser.setEvent(event);
				eventUser.setUser(authUser);
				eventUserService.save(eventUser);
				flash = new FlashData().success("参加登録しました");
			}
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
		}
		ra.addFlashAttribute("flash", flash);
		return "redirect:/admin/events/view/{eventId}";
	}

	/*
	 * 削除
	 */
	@GetMapping(value = "/delete/{eventId}")
	public String delete(@PathVariable Integer eventId, RedirectAttributes ra) {
		FlashData flash;
		try {
			Event event = eventService.findById(eventId);
			String email = SecurityContextHolder.getContext().getAuthentication().getName();
			User authUser = userService.findByEmail(email);
			if (event.isParticipated(authUser)) {
				EventUser eventUser = event.findEventUser(authUser);
			    eventUserService.deleteById(eventUser.getId());
			    flash = new FlashData().success("辞退しました");
			} else {
			    flash = new FlashData().danger("既にイベントを辞退しています");
			}
		} catch (DataNotFoundException e) {
			flash = new FlashData().danger("該当データがありません");
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
		}
		ra.addFlashAttribute("flash", flash);
		return "redirect:/admin/events/view/{eventId}";
	}
}
