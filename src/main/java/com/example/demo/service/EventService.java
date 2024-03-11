package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.common.DataNotFoundException;
import com.example.demo.dao.EventDao;
import com.example.demo.entity.Event;

@Service
public class EventService implements BaseService<Event> {
	@Autowired
	private EventDao dao;

	@Override
	public List<Event> findAll() {
		return dao.findAll();
	}

	@Override
	public Event findById(Integer id) throws DataNotFoundException {
		return dao.findById(id);
	}

	@Override
	public void save(Event orderDetail) {
		dao.save(orderDetail);
	}

	@Override
	public void deleteById(Integer id) {
		dao.deleteById(id);
	}

	public List<Event> findByUserId(Integer userId) throws DataNotFoundException {
		return dao.findByUserId(userId);
	}

	public List<Event> findByCategoryId(Integer categoryId) throws DataNotFoundException {
		return dao.findByCategoryId(categoryId);
	}
}
