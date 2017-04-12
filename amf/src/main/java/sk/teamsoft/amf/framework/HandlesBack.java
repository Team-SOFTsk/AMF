package sk.teamsoft.amf.framework;

/**
 * Convenience method for interception of backPressed events from presenters
 *
 * @author Dusan Bartos
 */
public interface HandlesBack {
    /**
     * @return true if event was handled and shouldn't be dispatched further
     */
    boolean onBackPressed();
}
