package taskey.storage;

/**
 * This class is to allow Storage to easily retrieve the last successfully saved tasklist from memory
 * so that Storage can throw it to Logic when an error is encountered during saving.
 * This will in turn allow Logic to easily undo the last operation so that its data remains in sync with Storage.
 *
 * For now, this class is meant to be used by Storage for the above purpose
 * but Logic could also use this for the undo command.
 *
 * @author Dylan
 */
public class History {

}
