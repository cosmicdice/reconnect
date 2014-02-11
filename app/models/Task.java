package models;

import play.*;
import play.db.jpa.*;

import javax.persistence.*;
import java.util.*;

@Entity
public class Task extends Model {

	//Champs relatifs au compte
	public Long owner;
	public int level;
    public boolean closed;
    public boolean owner_done;
    public boolean task_finished;
    public int participants_max;
	public boolean done;
    public ArrayList<Long> participants_done;
    public ArrayList<Long> participants;
    public String picture;
    public ArrayList<String> tags;
    public boolean credited;

    public ArrayList<Long> getParticipants_done(){
        return participants_done;
    }
    public void setParticipants_done(ArrayList<Long> participants_done){
        this.participants_done = participants_done;
    }
    public void addParticipant_done(Long id){
        this.participants_done.add(id);
    }
    public void deleteParticipant_done(Long participants_done){
        this.participants_done.remove(this.participants_done.indexOf(id));
    }

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

    public void isFinished(){
        if (participants_done.size() == participants.size())
            this.task_finished = true;
    }

    public int isMember(Long id){
        for (int i=0; i < this.participants.size(); i++){
            Logger.info("" + i);
            if (id == this.participants.get(i)){
                return 1;
            }
        }
        return 0;
        }

    public int isMemberDone(Long id){
        for (int i=0; i < this.participants_done.size(); i++){
            if (id == this.participants_done.get(i)){
                return 1;
            }
        }
        return 0;
        }

	@Lob
	public String content;
    public String title;

	public Task(Long owner, int level, String content, String title, ArrayList<String> tags, int participants_max) {
		this.owner = owner;
		this.level = level;
		this.done = false;
        this.closed=false;
        this.owner_done = false;
        this.task_finished = false;
        this.credited = false;
        this.participants_max = participants_max;
		this.content = content;
        this.title = title;
        this.participants = new ArrayList<Long>(600);
        this.participants_done = new ArrayList<Long>(600);
        this.tags = tags;
	}
}
