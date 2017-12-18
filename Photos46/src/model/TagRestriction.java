package model;

import java.util.ArrayList;
import java.io.Serializable;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

/**
 * Helps search through tags.
 * 
 * @author Mohamed Ameer
 * @author Sahil Kumbhani
 */
public class TagRestriction implements Serializable {
    /**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = -2912661487375888338L;

	/**
     * List of tags
     */
    private ObservableList<Tag> tagList;
    
    /**
     * List of tags (For serialization)
     */
    private ArrayList<Tag> keepTagList;

    /**
     * @param stored Clean up before store to file or after retrieve from file
     */
    public void cleanUp(boolean stored) {
		if (!stored) {
			this.tagList = FXCollections.observableList(this.keepTagList);
			this.keepTagList = null;
		}
		else {
			this.keepTagList = new ArrayList<>(this.tagList);
			this.tagList = null;
		}
	}

    /**
     * Initialize fields
     */
    public TagRestriction() {
	    this.tagList = FXCollections.observableArrayList();
	}

    /**
     * @return List of tags
     */
    public ObservableList<Tag> getTags() {
		return this.tagList;
	}

    /**
     * @param tagName Tag name
     * @param tagVal Tag value
	 * @return True if tag is added
     */
    public boolean addTag(String tagName, String tagVal) {
    	Tag t = new Tag(tagName, tagVal);

        return Helper.addOrGetList(this.tagList, t);
    }
}