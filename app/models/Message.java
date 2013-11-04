package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class Message extends Model {
	
	//Champs relatifs au compte
	public Long sender;
	public Long receiver;
	public String subject;
	public Date date;
	public Long type;
	public Long idOfConversation;
	
	@Lob
	public String content;
	
	public Message(Long sender, Long receiver, String subject, Date date, String content, Long type, Long idOfConversation) {
		this.sender = sender;
		this.receiver = receiver;
		this.subject = subject;
		this.date = date;
		this.type = type;
		this.idOfConversation = idOfConversation;
		this.content = content;
	}
}
