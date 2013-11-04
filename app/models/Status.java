package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class Status extends Model {
	
	//Champs relatifs au compte
	public Long owner;
	public Date date;
	public Long type;
	
	@Lob
	public String content;
	
	public Status(Long owner, Date date, Long type, String content) {
		this.owner = owner;
		this.date = date;
		this.type = type;
		this.content = content;
	}
}
