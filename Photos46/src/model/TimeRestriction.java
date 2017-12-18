package model;

import java.time.LocalDate;
import java.io.Serializable;

/**
 * Helps search through dates.
 * 
 * @author Mohamed Ameer
 * @author Sahil Kumbhani
 */
public class TimeRestriction implements Serializable {
    /**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 1619565176333294847L;

	/**
     * Start date for range of dates
     */
    private LocalDate startDate = null;

    /**
     * End date for range of dates
     */
    private LocalDate endDate = null;
	
	public TimeRestriction() {

	}

    /**
     * @return Start date
     */
    public LocalDate getStartDate() {
		return this.startDate;
	}

    /**
     * @param startDate Start date
     */
    public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

    /**
     * @return End date
     */
    public LocalDate getEndDate() {
		return this.endDate;
	}

    /**
     * @param endDate End date
     */
    public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
}