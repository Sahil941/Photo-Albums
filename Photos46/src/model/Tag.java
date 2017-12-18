package model;

import java.io.Serializable;

/**
 * Represents a tag object.
 * 
 * @author Mohamed Ameer
 * @author Sahil Kumbhani
 */
public class Tag implements Serializable, Comparable<Tag> {
    /**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 7764033782119311253L;

	/**
     * Tag name
     */
    private String tagName;

    /**
     * Tag value
     */
    private String tagVal;

    /**
     * @param tagName Tag name
     * @param tagVal Tag value
     */
    public Tag(String tagName, String tagVal) {
        this.tagName = tagName;
        this.tagVal = tagVal;
    }

    /**
     * @param t Input tag
     */
    public Tag(Tag t) {
        this.tagName = t.tagName;
        this.tagVal = t.tagVal;
    }

    /**
     * @return Output for tags
     */
    public String toString() {
    	return this.tagName + ", " + this.tagVal;
    }

    /**
     * @param t Input tag
     * @return Checks whether tags are equal to each other
     */
    @Override
    public int compareTo(Tag t) {
    	int result = this.tagName.compareToIgnoreCase(t.tagName);
    	
    	if (result != 0){
    		return result;
    	}
    	else{
    		return this.tagVal.compareToIgnoreCase(t.tagVal);
    	}
    }
}