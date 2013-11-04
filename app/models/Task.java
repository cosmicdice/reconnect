package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class Task extends Model {
	
	//Champs relatifs au compte
	public Long owner;
	public Long level;
	public boolean done;
	
	@Lob
	public String content;
	
	public Task(Long owner, Long level, String content) {
		this.owner = owner;
		this.level = level;
		this.done = false;
		this.content = content;
	}
}
