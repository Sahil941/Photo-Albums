package model;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;
import java.util.stream.IntStream;
import java.util.function.BiPredicate;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Represents an album object.
 * 
 * @author Mohamed Ameer
 * @author Sahil Kumbhani
 */
public class Album implements Serializable, Comparable<Album> {
    /**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 5910620234698813615L;

	/**
     * Album name for result of searching tags
     */
    public static String ALBUM_NAME_SEARCH_BY_TAG	= " Tag Search Result";
    
    /**
     * Album name for result of searching dates
     */
    public static String ALBUM_NAME_SEARCH_BY_DATE	= " Date Search Result";
    
    /**
     * Album name when store model to file (since SimpleStringProperty could not be stored in file)
     */
    private String keepAlbumName;
    
    /**
     * Name of the album
     */
    private SimpleStringProperty albumName;
    
    /**
     * Number of photos
     */
    private SimpleIntegerProperty photoCount;
    
    /**
     * earliest date time of picture taken
     */
    private SimpleStringProperty startTime;
    
    /**
     * latest time for  picture taken
     */
    private SimpleStringProperty endTime;
    
    /**
     * List of all the photos in the album
     */
    private List<Photo> photoList;
    
    /**
     * Index of the current photo
     */
    private int currPhotoIndex;
    
    /**
     * @return Name of album
     */
    public String getAlbumName() {
        return this.albumName.get();
    }
    
    /**
     * @param name Name of the album
     */
    public void setAlbumName(String name) {
        this.albumName.set(name);
    }
    
    /**
     * @return Number of photos
     */
    public int getPhotoCount() {
        return this.photoCount.get();
    }
    
    /**
     * @param photoCount Number of photos
     */
    public void setPhotoCount(int photoCount) {
        this.photoCount.set(photoCount);
    }

    /**
     * @return Earliest date time of picture taken
     */
    public String getStartTime() {
        return this.startTime.get();
    }

    /**
     * @param startTime Earliest date time of picture taken
     */
    public void setStartTime(String startTime) {
        this.startTime.set(startTime);
    }

    /**
     * @return Latest date time of picture taken
     */
    public String getEndTime() {
        return this.endTime.get();
    }

    /**
     * @param endTime Latest date time of picture taken
     */
    public void setEndTime(String endTime) {
        this.endTime.set(endTime);
    }

    /**
     * @param stored Clean up before store to file or after retrieved from file
     */
    public void cleanUp(boolean stored) {
		if (!stored) {
			this.albumName = new SimpleStringProperty(this.keepAlbumName);
		    this.photoCount = new SimpleIntegerProperty();
		    this.startTime = new SimpleStringProperty();
		    this.endTime = new SimpleStringProperty();
		    this.keepAlbumName = null;
		}
		else {
			this.keepAlbumName = getAlbumName();
			this.albumName = null;
			this.photoCount = null;
			this.startTime = null;
			this.endTime = null;
		}
		
		for (Photo p: this.photoList) {
			p.cleanUp(stored);
		}
	}

    /**
     * @param name Name of the album
     */
    public Album(String name) {
		this.albumName = new SimpleStringProperty(name);
	    this.photoCount = new SimpleIntegerProperty(0);
	    this.startTime = new SimpleStringProperty("N/A");
	    this.endTime = new SimpleStringProperty("N/A");
	    this.photoList = new ArrayList<>();
	    this.currPhotoIndex = -1;
	}

    /**
     * @param name Name of the album
     * @param photoList List of photos
     */
    public Album(String name, List<Photo> photoList) {
		this(name);
		
	    for (Photo p : photoList) {
		    Photo newP = new Photo(p);
		    this.photoList.add(newP);
	    }
	    
		if (this.photoList.size() > 0) {
			this.currPhotoIndex = 0;
		}
	}

    /**
     * @param input Input index of current photo
     * @return index of current photo
     */
    public int setCurrPhotoIndex(int input) {
		this.currPhotoIndex = input;
		return resetCurrPhotoIndex();
	}

    /**
     * @return Index of current photo
     */
    private int resetCurrPhotoIndex() {
		if (this.photoList.size() == 0) {
		    this.currPhotoIndex = -1;
		}
		else {
			if (this.currPhotoIndex > this.photoList.size() - 1) {
				this.currPhotoIndex  = this.photoList.size() - 1;
			}
			else if (this.currPhotoIndex < 0) {
				this.currPhotoIndex = 0;
			}
		}

		return this.currPhotoIndex;
	}

    /**
     * @return Index of current photo
     */
    public int getCurrPhotoIndex() {
		return resetCurrPhotoIndex();
	}

    /**
     * @param p Input photo to be added
     */
    public void addToAlbum(Photo p) {
    	this.photoList.add(p);
    	this.currPhotoIndex = this.photoList.size() - 1;
	}

    /**
     * @param index Index of photo
     * @return Photo from index
     */
    public Photo getFromAlbum(int index) {
    	if (index < this.photoList.size() && index >= 0) {
    		return this.photoList.get(index);
    	}
    	
    	return null;
    }

    /**
     * @param p Input photo
     * @return Index of the photo
     */
    public int getPhotoIndex(Photo p) {
        return IntStream.range(0, this.photoList.size()).filter(i -> p == this.photoList.get(i)).findFirst().orElse(-1);
    }

    /**
     * @param p Input photo
     * @return the index of photo deleted
     */
    public int deleteFromAlbum(Photo p) {
    	int index = -1;
    	for (int i = 0; i < this.photoList.size(); i++) {
    		if (p == this.photoList.get(i)) {
    			index = i;
    			this.photoList.remove(index);
    			break;
    		}
    	}
    	
    	return index;
    }

    /**
     * @param currPhoto Photo: the place(index of photo in the list) that the new photo to add to 
     * @param p Photo to be added
     * @return Index of the photo to be added
     */
    public int addToAlbum(Photo currPhoto, Photo p) {
    	int index;
        if (currPhoto != null) {
            index = IntStream.range(0, this.photoList.size()).filter(i -> currPhoto == this.photoList.get(i)).findFirst().orElse(-1);
        }
        else {
        	index = this.photoList.size();
        }

		this.photoList.add(index, p);

    	return index;
    }

    /**
     * @return List of photos
     */
    public List<Photo> getPhotoList() {
    	return this.photoList;
    }

    /**
     * @return Size of list of photos
     */
    public int getSize() {
    	return this.photoList.size();
    }

    /**
     * @return Current photo
     */
    public Photo getCurrPhoto() {
    	resetCurrPhotoIndex();
    	
    	if (this.currPhotoIndex >= 0){
    		return this.photoList.get(this.currPhotoIndex);
    	}
    	else{
    		return null;
    	}
    }

    /**
     * @param album Input album
     */
    @Override
	public int compareTo(Album album) {
		return getAlbumName().compareToIgnoreCase(album.getAlbumName());
	}

    /**
     * @return Album name
     */
    @Override
	public String toString() {
		return getAlbumName();
	}

    /**
     * @param tag Condition of tags to search
     * @return List of photos within given tags
     */
    public List<Photo> tagSearch(TagRestriction tag) {
		BiPredicate<Photo,TagRestriction> bp = (p, c)->Helper.search(p.getTags(), c.getTags(), (t1,t2)->t1.compareTo(t2) == 0);

		return Helper.filter(this.photoList, tag, bp);
	}

    /**
     * @param date Condition of dates to search
     * @return List of photos within given dates
     */
    public List<Photo> searchDate(TimeRestriction date) {
		BiPredicate<Photo,TimeRestriction> bp = Photo::isWithinRange;

		return Helper.filter(this.photoList, date, bp);
	}

    /**
     * Sets photo count and the Earliest and latest times of all the photos for the album
     */
    public void setCounterDateTime() {
		if (this.photoList.size()==0) {
			setPhotoCount(0);
		    setStartTime("N/A");
		    setEndTime("N/A");
		}
		else {
			boolean start = true;
			int count = 0;
			long min = 0;
			long max = 0;
			for (Photo p: this.photoList) {
				if (start) {
					count = 1;
					min	= p.getPhotoDate();
					max	= p.getPhotoDate();
					start = false;
				}
				else {
					count++;
					long pd = p.getPhotoDate();
					
					if (pd > max) {
						max = pd;
					}
					
					if (pd < min) {
						min = pd;
					}
				}
			}

			setPhotoCount(count);
		    setStartTime(Photo.epochToLocalTime(min));
		    setEndTime(Photo.epochToLocalTime(max));
		}
	}
}