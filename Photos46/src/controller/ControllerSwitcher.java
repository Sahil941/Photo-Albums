package controller;

/**
 * This interface helps initialize controller after switch among them.
 * 
 * @author Mohamed Ameer
 * @author Sahil Kumbhani
 */
public interface ControllerSwitcher {
    /**
     * Shows a selected stage
     */
    void initBeforeShow();
}