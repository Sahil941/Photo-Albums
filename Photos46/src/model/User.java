package model;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.stream.IntStream;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

/**
 * Represents a user object.
 * 
 * @author Mohamed Ameer
 * @author Sahil Kumbhani
 */
public class User implements Serializable, Comparable<User> {
    /**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1536886839674618319L;

	/**
     * Username
     */
    private String username;
    
    /**
     * Admin user
     */
    public static final String ADMIN = "admin";

    /**
     * List of albums
     */
    private ObservableList<Album> albumList;

    /**
     * List of albums for serialization
     */
    private ArrayList<Album> keepAlbumList;

    /**
     * Current album
     */
    private Album currAlbum;

    /**
     * Tags for searching
     */
    private TagRestriction tags;

    /**
     * Range of dates for searching
     */
    private TimeRestriction dates;

    /**
     * @param stored Clean up before store to file or after retrieve from file
     */
    public void cleanUp(boolean stored) {
		if (!stored) {
			this.albumList = FXCollections.observableList(this.keepAlbumList);
			this.keepAlbumList = null;

			for (Album a : this.albumList) {
				a.cleanUp(false);
			}
		}
		else {
			this.keepAlbumList = new ArrayList<>(this.albumList);
			this.albumList = null;
			
			for (Album a : this.keepAlbumList) {
				a.cleanUp(true);
			}
		}

		this.tags.cleanUp(stored);
	}

    /**
     * @param username Username
     */
    public User(String username) {
		this.username = username;
		this.albumList = FXCollections.observableArrayList();
		this.keepAlbumList = null;
		this.currAlbum = null;
		this.tags = new TagRestriction();
		this.dates = new TimeRestriction();
	}

    /**
     * @param username Username
     * @return New album added
     */
    public Album addAlbum(String username) {
		Album album = new Album(username);

        boolean isFound = IntStream.range(0, this.albumList.size()).anyMatch(i -> 
        	this.albumList.get(i).getAlbumName().equals(album.getAlbumName()));
        
		if (!isFound) {
			this.albumList.add(album);
			return album;
		}
		else {
			return null;
		}
	}

    /**
     * @param album Input album. Overwrite used for search results
     */
    public void addAlbum(Album album) {
        IntStream.range(0, this.albumList.size()).filter(i -> this.albumList.get(i).getAlbumName().equals(album.getAlbumName()))
                .findFirst().ifPresent(i -> this.albumList.remove(i));
		
        this.albumList.add(album);
	}

    /**
     * @param index Index of album to update
     * @param name New album name
     */
    public void updateAlbumName(int index, String name) {
		if (index >= 0 && index < this.albumList.size()) {
            boolean isFound = IntStream.range(0, this.albumList.size())
                    .filter(i -> i != index).anyMatch(i -> this.albumList.get(i).getAlbumName().equalsIgnoreCase(name));
            
			if (!isFound) {
				this.albumList.get(index).setAlbumName(name);
			}
		}
	}

    /**
     * @return Current album
     */
    public Album getCurrAlbum() {
		return this.currAlbum;
	}

    /**
     * @param currAlbum Current album
     */
    public void setCurrAlbum(Album currAlbum) {
        this.currAlbum = currAlbum;
    }

    /**
     * @return Tags for searching
     */
    public TagRestriction getTags() {
        return this.tags;
    }

    /**
     * @param index Index of tag to delete
     */
    public void deleteTagRestrictionTag(int index) {
		ObservableList<Tag> tagList = this.tags.getTags();
		
		if (tagList.size() > 0 && index >= 0 && index < tagList.size()) {
			tagList.remove(index);
		}
	}

    /**
     * @return Range of dates
     */
    public TimeRestriction getDates() {
        return this.dates;
    }

    /**
     * @return List of albums
     */
    public ObservableList<Album> getAlbumList() {
		return this.albumList;
	}

    /**
     * @return Username
     */
    public String getUsername() {
		return this.username;
	}

	public Album deleteAlbum(String name) {
		return Helper.delete(this.albumList, name, (t,k) -> t.getAlbumName().equalsIgnoreCase(k));
	}

    /**
     * @param name Album name to search for
     * @return Album requested
     */
    public Album getAlbum(String name) {
		return Helper.get(this.albumList, name, (t,k) -> t.getAlbumName().equalsIgnoreCase(k));
	}

    /**
     * @param p Photo to copy
     * @param target Album to copy to
     */
    public void copyPhoto(Photo p, String target) {
		Photo newP = new Photo(p);
		Album targetAlbum = getAlbum(target);
		targetAlbum.addToAlbum(newP);
	}

    /**
     * @param p Photo to move
     * @param target Album to move to
     */
    public void movePhoto(Photo p, String target) {
		Album targetAlbum = getAlbum(target);
		this.currAlbum.deleteFromAlbum(p);
		targetAlbum.addToAlbum(p);
	}

    /**
     * @return Username
     */
    @Override
	public String toString() {
		return this.username;
	}

    /**
     * @param other Input username
     * @return Checks whether username is equal or not
     */
    @Override
	public int compareTo(User other) {
		return this.username.compareToIgnoreCase(other.username);
	}

    /**
     * @return List of photos with specified tags
     */
    public List<Photo> tagSearch() {
		List<Photo> list = new ArrayList<>();
		
		for (Album a : this.albumList) {
            if (!a.getAlbumName().equalsIgnoreCase(Album.ALBUM_NAME_SEARCH_BY_DATE) && !a.getAlbumName()
                    .equalsIgnoreCase(Album.ALBUM_NAME_SEARCH_BY_TAG)) {
                list.addAll(a.tagSearch(this.tags));
            }
        }
		
		return list;
	}

    /**
     * @return List of photos within range of dates
     */
    public List<Photo> dateSearch() {
		List<Photo> list = new ArrayList<>();
		
		for (Album a : this.albumList) {
            if (!a.getAlbumName().equalsIgnoreCase(Album.ALBUM_NAME_SEARCH_BY_DATE) && !a.getAlbumName()
                    .equalsIgnoreCase(Album.ALBUM_NAME_SEARCH_BY_TAG)) {
                list.addAll(a.searchDate(this.dates));
            }
        }
		
		return list;
	}
}