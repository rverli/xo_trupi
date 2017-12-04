package com.crossover.trial.journals.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.crossover.trial.journals.controller.PublisherController;
import com.crossover.trial.journals.model.Category;
import com.crossover.trial.journals.model.Journal;
import com.crossover.trial.journals.model.Publisher;
import com.crossover.trial.journals.model.Subscription;
import com.crossover.trial.journals.model.User;
import com.crossover.trial.journals.repository.CategoryRepository;
import com.crossover.trial.journals.repository.JournalRepository;
import com.crossover.trial.journals.repository.UserRepository;
import com.crossover.trial.journals.utils.Util;

@Service
public class JournalServiceImpl implements JournalService {

	private final static Logger log = Logger.getLogger(JournalServiceImpl.class);

	@Autowired
	private JournalRepository journalRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private JavaMailSender javaMailService;	

	@Override
	public List<Journal> listAll(User user) {
		User persistentUser = userRepository.findOne(user.getId());
		List<Subscription> subscriptions = persistentUser.getSubscriptions();
		if (subscriptions != null) {
			List<Long> ids = new ArrayList<>(subscriptions.size());
			subscriptions.stream().forEach(s -> ids.add(s.getCategory().getId()));
			return journalRepository.findByCategoryIdIn(ids);
		} else {
			return Collections.emptyList();
		}
	}

	@Override
	public List<Journal> publisherList(Publisher publisher) {
		Iterable<Journal> journals = journalRepository.findByPublisher(publisher);
		return StreamSupport.stream(journals.spliterator(), false).collect(Collectors.toList());
	}

	@Override
	public Journal publish(Publisher publisher, Journal journal, Long categoryId) throws ServiceException {
		Category category = categoryRepository.findOne(categoryId);
		if(category == null) {
			throw new ServiceException("Category not found");
		}
		journal.setPublisher(publisher);
		journal.setCategory(category);
		try {
			Journal newjournal = journalRepository.save(journal);
			sendEmailToSubscribedUser(newjournal);

			return newjournal;
		} catch (DataIntegrityViolationException e) {
			throw new ServiceException(e.getMessage(), e);
		}
	}
	
	public void sendEmailToSubscribedUser(Journal journal) {
		log.info("Sending email");
		List<User> users = userRepository.findBySubscriptionCategory(journal.getCategory());
		
		if (users != null && users.size() > 0) {
			ArrayList<String> to = new ArrayList<>();
			for (User user : users) {
				to.add(user.getLoginName());
			}
			
			SimpleMailMessage mailMessage = 
					Util.createEmailMessage(
							"crossover-journal@crossover.com", 
							to.toArray(new String[0]), 
							"New Journal Notification",
							"New Journal has been added. " + journal);
			
			javaMailService.send(mailMessage);
		}
	}

    @Scheduled(cron="59 23 0 * * *")
	public void sendEmailNewJournalsDay() {
		log.info("Sending email scheduler");
		List<User> users = userRepository.findAll();
		
		Date startDate = Util.getBeginDate(new Date());		
		Date endDate = Util.getEndDate(startDate);
		List<Journal> journals = journalRepository.findByPublishDateBetween(startDate, endDate);
		
		if (users != null && users.size() > 0 && journals != null && journals.size() > 0) {
			ArrayList<String> to = new ArrayList<>();
			for (User user : users) {
				to.add(user.getLoginName());
			}
			
			SimpleMailMessage mailMessage = 
					Util.createEmailMessage(
							"crossover-journal@crossover.com", 
							to.toArray(new String[0]),
							"Daily Journal Notification",
							"All journals of the day: " + StringUtils.join(journals,","));
			
			javaMailService.send(mailMessage);		
		}
    }
	

	@Override
	public void unPublish(Publisher publisher, Long id) throws ServiceException {
		Journal journal = journalRepository.findOne(id);
		if (journal == null) {
			throw new ServiceException("Journal doesn't exist");
		}
		String filePath = PublisherController.getFileName(publisher.getId(), journal.getUuid());
		File file = new File(filePath);
		if (file.exists()) {
			boolean deleted = file.delete();
			if (!deleted) {
				log.error("File " + filePath + " cannot be deleted");
			}
		}
		if (!journal.getPublisher().getId().equals(publisher.getId())) {
			throw new ServiceException("Journal cannot be removed");
		}
		journalRepository.delete(journal);
	}
}
