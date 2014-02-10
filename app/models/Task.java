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
    public ArrayList<Long> participants;
    public String picture;
    public ArrayList<String> tags;

     public ArrayList<Long> getParticipants(){
        return participants;
    }
    public void setParticipants(ArrayList<Long> participants){
        this.participants = participants;
    }
    public void addParticipant(Long id){
        this.participants.add(id);
    }
    public void deleteParticipant(Long participants){
        this.participants.remove(this.participants.indexOf(id));
    }

   public ArrayList<String> getTags(){
        return tags;
    }
    public void setTags(ArrayList<String> tags){
        this.tags = tags;
    }
    public void addTag(String tag){
        this.tags.add(tag);
    }
    public void deleteTag(String tag){
        this.tags.remove(this.tags.indexOf(tag));
    }

	@Lob
	public String content;
    public String title;

	public Task(Long owner, Long level, String content, String title, ArrayList<String> tags) {
		this.owner = owner;
		this.level = level;
		this.done = false;
		this.content = content;
        this.title = title;
        this.participants = new ArrayList<Long>(600);
        this.tags = tags;
	}
}
