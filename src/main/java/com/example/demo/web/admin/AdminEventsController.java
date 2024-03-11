package com.example.demo.web.admin;

import java.util.List;

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

import com.example.demo.common.DataNotFoundException;
import com.example.demo.common.FlashData;
import com.example.demo.entity.Event;
import com.example.demo.entity.User;
import com.example.demo.service.EventService;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminEventsController {
	@Autowired
	EventService eventService;

	@Autowired
	UserService userService;

	/*
	 * 一覧画面表示
	 */
	@GetMapping(path = {"", "/"})
	public String list(Model model) {
		// 全件取得
		List<Event> events = eventService.findAll();
		model.addAttribute("events", events);
		return "admin/events/list";
	}

	/*
	 * マイリスト一覧画面表示
	 */
	@GetMapping(value = "/events/mylist")
	public String mylist(Model model, RedirectAttributes ra) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			User authUser = userService.findByEmail(email);
			// 全件取得
			List<Event> events = eventService.findByUserId(authUser.getId());
			model.addAttribute("events", events);
		} catch (Exception e) {
			FlashData flash = new FlashData().danger("エラーが発生しました");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/admin";
		}
		return "admin/events/mylist";
	}

	/*
	 * 新規作成画面表示
	 */
	@GetMapping(value = "/events/create")
	public String form(Event event, Model model, RedirectAttributes ra) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
		    User authUser = userService.findByEmail(email);
		    event.setUser(authUser);
		} catch (Exception e) {
			FlashData flash = new FlashData().danger("エラーが発生しました");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/admin";
		}
		model.addAttribute("event", event);
		return "admin/events/create";
	}

	/*
	 * 新規登録
	 */
	@PostMapping(value = "/events/create")
	public String register(@Valid Event event, BindingResult result, Model model, RedirectAttributes ra) {
		FlashData flash;
		try {
			if (result.hasErrors()) {
				return "admin/events/create";
			}
			// 新規登録
			eventService.save(event);
			flash = new FlashData().success("新規作成しました");
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
		}
		ra.addFlashAttribute("flash", flash);
		return "redirect:/admin";
	}

	/*
	 * 編集画面表示
	 */
	@GetMapping(value = "/events/edit/{id}")
	public String edit(@PathVariable Integer id, Model model, RedirectAttributes ra) {
		try {
			// 存在確認
			Event event = eventService.findById(id);
			model.addAttribute("event", event);
		} catch (Exception e) {
			FlashData flash = new FlashData().danger("該当データがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/admin/events/mylist";
		}
		return "admin/events/edit";
	}

	/*
	 * 更新
	 */
	@PostMapping(value = "/events/edit/{id}")
	public String update(@PathVariable Integer id, @Valid Event event, BindingResult result, Model model, RedirectAttributes ra) {
		FlashData flash;
		try {
			if (result.hasErrors()) {
				return "admin/events/edit";
			}
			eventService.findById(id);
			// 更新
			eventService.save(event);
			flash = new FlashData().success("更新しました");
		} catch (Exception e) {
			flash = new FlashData().danger("該当データがありません");
		}
		ra.addFlashAttribute("flash", flash);
		return "redirect:/admin/events/mylist";
	}

	/*
	 * 
	 * 削除
	 */
	@GetMapping(value = "/events/delete/{id}")
	public String delete(@PathVariable Integer id, RedirectAttributes ra) {
		FlashData flash;
		try {
			Event event = eventService.findById(id);
			if (event.getEventUsers().isEmpty()) {
				eventService.deleteById(id);
				flash = new FlashData().success("削除しました");
			} else {
				flash = new FlashData().danger("イベント参加者がいるため削除できません");
			}
		} catch (DataNotFoundException e) {
			flash = new FlashData().danger("該当データがありません");
		} catch (Exception e) {
			flash = new FlashData().danger("処理中にエラーが発生しました");
		}
		ra.addFlashAttribute("flash", flash);
		return "redirect:/admin/events/mylist";
	}

	/*
	 * 表示画面表示
	 */
	@GetMapping(value = "/events/view/{id}")
	public String view(@PathVariable Integer id, Model model, RedirectAttributes ra) {
		try {
			// 存在確認
			Event event = eventService.findById(id);
			model.addAttribute("event", event);
			String email = SecurityContextHolder.getContext().getAuthentication().getName();
			User authUser = userService.findByEmail(email);
			Boolean isParticipated = event.isParticipated(authUser);
			model.addAttribute("isParticipated", isParticipated);
		} catch (Exception e) {
			FlashData flash = new FlashData().danger("該当データがありません");
			ra.addFlashAttribute("flash", flash);
			return "redirect:/admin";
		}
		return "admin/events/view";
	}
}
