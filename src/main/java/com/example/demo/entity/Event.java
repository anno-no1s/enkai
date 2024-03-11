package com.example.demo.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "events")
public class Event extends BaseEntity {
	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 255, nullable = false)
	@NotEmpty(message = "イベント名は必須入力です")
	private String name;

	@Column(length = 255, nullable = false)
	@NotEmpty(message = "詳細は必須入力です")
	private String detail;

	@Column(nullable = false)
	@NotNull(message = "最大参加者数が未入力です")
	@Min(value = 1, message = "最大参加者数は1以上の整数を入力してください")
	private Integer maxParticipant;

	@ManyToOne
	@JoinColumn(name = "category_id")
	@NotNull(message = "カテゴリは必須入力です")
	private Category category;

	@ManyToOne
	@JoinColumn(name = "user_id")
	@NotNull(message = "管理ユーザは必須入力です")
	private User user;

	@OneToMany(mappedBy = "event", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<EventUser> eventUsers;

	@OneToMany(mappedBy = "event", fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
	private List<Chat> chats;

	public EventUser findEventUser(User user) {
		for (EventUser eventUser: this.eventUsers) {
			if (eventUser.getUser().getId() == user.getId()) {
			    return eventUser;
			}
		}
		return null;
	}

	// ユーザーが参加済みか判定するメソッド
	public boolean isParticipated(User user) {
		for (EventUser eventUser: this.eventUsers) {
			if (eventUser.getUser().getId() == user.getId()) {
			    return true;
			}
		}
		return false;
	}

	// 参加者人数が上限に達しているか判定するメソッド
	public boolean isLimited() {
		if (this.maxParticipant <= this.getEventUsers().size()) {
			return true;
		}
		return false;
	}
}
